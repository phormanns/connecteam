package de.jalin.connecteam.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.mail.MailAccount;

public class DataAccess {

	private static final Logger log = Logger.getLogger("DataAccess.class");
	
	private static final String SELECT_TOPIC = 
			  "SELECT ws.id, ws.name, ws.description, tp.id, tp.name, tp.description, tp.address,"
			+ " tp.imap_host, tp.imap_port, tp.imap_starttls, tp.imap_login, tp.imap_passwd,"
			+ " tp.smtp_host, tp.smtp_port, tp.smtp_starttls, tp.smtp_login, tp.smtp_passwd "
			+ "FROM workspace ws, topic tp "
			+ "WHERE tp.workspace_id = ws.id AND tp.address = ? ";
	private static final String SELECT_SUBSCRIPTIONS = 
			  "SELECT scr.id, scr.address, scr.name, scn.id, scn.digest, scn.moderator, scn.subscribe_date, scn.unsubscribe_date "
			+ "FROM subscriber scr, subscription scn "
			+ "WHERE scr.id = scn.subscriber_id AND scn.topic_id = ? ";
	
	private final Connection dbConnection;

	public DataAccess(final Connection dbConnection) {
		this.dbConnection = dbConnection;
	}
	
	public Topic loadTopic(String address) throws CxException {
		log.info("load topic " + address);
		PreparedStatement stmtSelectTopic = null;
		ResultSet rsTopic = null;
		PreparedStatement stmtSelectSubs = null;
		ResultSet rsSubs = null;
		try {
			final Workspace workspace = new Workspace();
			final Topic topic = new Topic();
			stmtSelectTopic = dbConnection.prepareStatement(SELECT_TOPIC);
			stmtSelectTopic.setString(1, address);
			rsTopic = stmtSelectTopic.executeQuery();
			if (rsTopic.next()) {
				workspace.setId(rsTopic.getLong("ws.id"));
				workspace.setName(rsTopic.getString("ws.name"));
				workspace.setDescription(rsTopic.getString("ws.description"));
				topic.setId(rsTopic.getLong("tp.id"));
				topic.setName(rsTopic.getString("tp.name"));
				topic.setDescription(rsTopic.getString("tp.description"));
				topic.setAddress(rsTopic.getString("tp.address"));
				final MailAccount imapAccount = new MailAccount();
				imapAccount.setHost(rsTopic.getString("tp.imap_host"));
				imapAccount.setPort(rsTopic.getInt("tp.imap_port"));
				imapAccount.setStartTLS(rsTopic.getBoolean("tp.imap_starttls"));
				imapAccount.setLogin(rsTopic.getString("tp.imap_login"));
				imapAccount.setPasswd(rsTopic.getString("tp.imap_passwd"));
				topic.setImapAccount(imapAccount);
				final MailAccount smtpAccount = new MailAccount();
				smtpAccount.setHost(rsTopic.getString("tp.smtp_host"));
				smtpAccount.setPort(rsTopic.getInt("tp.smtp_port"));
				smtpAccount.setStartTLS(rsTopic.getBoolean("tp.smtp_starttls"));
				smtpAccount.setLogin(rsTopic.getString("tp.smtp_login"));
				smtpAccount.setPasswd(rsTopic.getString("tp.smtp_passwd"));
				topic.setSmtpAccount(smtpAccount);
			} else {
				throw new CxException("no topic for " + address);
			}
			stmtSelectSubs = dbConnection.prepareStatement(SELECT_SUBSCRIPTIONS);
			stmtSelectSubs.setLong(1, topic.getId());
			rsSubs = stmtSelectSubs.executeQuery();
			while (rsSubs.next()) {
				final Subscriber scr = new Subscriber();
				scr.setId(rsSubs.getLong("scr.id"));
				scr.setAddress(rsSubs.getString("scr.address"));
				scr.setName(rsSubs.getString("scr.name"));
				final Subscription scn = new Subscription();
				scn.setSubscriber(scr);
				scn.setId(rsSubs.getLong("scn.id"));
				scn.setDigest(rsSubs.getBoolean("scn.digest"));
				scn.setModerator(rsSubs.getBoolean("scn.moderator"));
				scn.setSubscribeDate(convertToLocalDateTime(rsSubs.getDate("scn.subscribe_date")));
				scn.setUnsubscribeDate(convertToLocalDateTime(rsSubs.getDate("scn.unsubscribe_date")));
				topic.add(scn);
			}
			log.info("topic " + address + " has " + topic.getSubscriptions().size() + " subscribers");
			return topic;
		} catch (SQLException e) {
			log.error(e);
			throw new CxException(e);
		} finally {
			if (rsTopic != null) try { rsTopic.close(); } catch (Exception e) { };
			if (stmtSelectTopic != null) try { stmtSelectTopic.close(); } catch (Exception e) { };
			if (rsSubs != null) try { rsSubs.close(); } catch (Exception e) { };
			if (stmtSelectSubs != null) try { stmtSelectSubs.close(); } catch (Exception e) { };
		}
	}

	private LocalDateTime convertToLocalDateTime(Date someDate) {
		final long timeMillis = someDate.getTime();
		final Instant ofEpochMilli = Instant.ofEpochMilli(timeMillis);
		final ZonedDateTime atZone = ofEpochMilli.atZone(ZoneId.systemDefault());
		final LocalDateTime localDateTime = atZone.toLocalDateTime();
		return localDateTime;
	}
	
}
