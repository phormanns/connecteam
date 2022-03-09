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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.mail.MailAccount;
import de.jalin.connecteam.mail.message.AttachmentPath;
import de.jalin.connecteam.mail.message.Post;

public class DataAccess {

	private static final Logger log = Logger.getLogger("DataAccess.class");
	
	private static final String LIST_SPACES = 
			  "SELECT ws.id AS ws_id, ws.name AS ws_name, ws.description AS ws_desc,"
			+ " tp.id AS tp_id, tp.name AS tp_name, tp.description AS tp_desc, tp.web_domain AS tp_webdomain, tp.address AS tp_address,"
			+ " tp.imap_host AS imap_host, tp.imap_port AS imap_port, tp.imap_starttls AS imap_starttls, tp.imap_login AS imap_login, tp.imap_passwd AS imap_passwd,"
			+ " tp.smtp_host AS smtp_host, tp.smtp_port AS smtp_port, tp.smtp_starttls AS smtp_starttls, tp.smtp_login AS smtp_login, tp.smtp_passwd AS smtp_passwd "
			+ "FROM workspace ws, topic tp "
			+ "WHERE tp.workspace_id = ws.id ORDER BY tp.id";

	private static final String SELECT_TOPIC = 
			  "SELECT ws.id AS ws_id, ws.name AS ws_name, ws.description AS ws_desc, tp.web_domain AS tp_webdomain,"
			+ " tp.id AS tp_id, tp.name AS tp_name, tp.description AS tp_desc, tp.address AS tp_address,"
			+ " tp.imap_host AS imap_host, tp.imap_port AS imap_port, tp.imap_starttls AS imap_starttls, tp.imap_login AS imap_login, tp.imap_passwd AS imap_passwd,"
			+ " tp.smtp_host AS smtp_host, tp.smtp_port AS smtp_port, tp.smtp_starttls AS smtp_starttls, tp.smtp_login AS smtp_login, tp.smtp_passwd AS smtp_passwd "
			+ "FROM workspace ws, topic tp "
			+ "WHERE tp.workspace_id = ws.id AND tp.address = ? ";
	
	private static final String SELECT_SUBSCRIPTIONS = 
			  "SELECT scr.id AS scr_id, scr.address AS scr_address, scr.name AS scr_name,"
			+ " scn.id AS scn_id,"
			+ " scn.recieves_digest AS recieves_digest, scn.recieves_messages AS recieves_messages,"
			+ " scn.recieves_messages AS recieves_messages, scn.may_send_messages AS may_send_messages, "
			+ " scn.subscribe_date AS subscribe_date, scn.unsubscribe_date AS unsubscribe_date "
			+ "FROM subscriber scr, subscription scn "
			+ "WHERE scr.id = scn.subscriber_id AND scn.topic_id = ? ";
	
	private static final String INSERT_MESSAGE =
			  "INSERT INTO message (topic_id, subject, processing, sender, message, token)"
			+ " values ( ?, ?, now(), ?, ?, ? )";

	private static final String INSERT_ATTACHMENT =
			  "INSERT INTO attachment (message_id, filename, mime_type, path_token)"
			+ " values ( ?, ?, ?, ? )";

	private static final String SELECT_ATTACHMENT = 
			  "SELECT msg.token AS msg_token, att.path_token AS att_token, att.mime_type AS mime_type, att.filename AS filename "
			+ " FROM message msg, attachment att"
			+ " WHERE att.message_id = msg.id AND msg.token = ? AND att.path_token = ?";

	private static final String LAST_VAL =
			  "SELECT LASTVAL() AS last_id";
	
	private final Connection dbConnection;

	public DataAccess(final Connection dbConnection) {
		this.dbConnection = dbConnection;
	}
	
	public AttachmentPath loadAttachment(final String msgToken, final String attToken) {
		log.info("load attachment " + msgToken + "/" + attToken);
		PreparedStatement stmtSelectAttachment = null;
		ResultSet resAttachment = null;
		try {
			stmtSelectAttachment = dbConnection.prepareStatement(SELECT_ATTACHMENT);
			stmtSelectAttachment.setString(1, msgToken);
			stmtSelectAttachment.setString(2, attToken);
			resAttachment = stmtSelectAttachment.executeQuery();
			if (resAttachment.next()) {
				final AttachmentPath attPath = new AttachmentPath();
				attPath.setContentType(resAttachment.getString("mime_type"));
				attPath.setFilename(resAttachment.getString("att_token"));
				attPath.setName(resAttachment.getString("filename"));
				return attPath;
			} else {
				log.error("attachment not found " + msgToken + "/" + attToken);
			}
		} catch (SQLException e) {
			log.error(e);
		}
		return null;
	}
	
	public void storeMessage(final Post post, final long topicId) {
		log.info("store message from " + post.getOriginalFrom());
		PreparedStatement stmtInsertMessage = null;
		PreparedStatement stmtInsertAttachment = null;
		PreparedStatement stmtLastVal = null;
		ResultSet resLastVal = null;
		try {
			dbConnection.setAutoCommit(false);
			stmtInsertMessage = dbConnection.prepareStatement(INSERT_MESSAGE);
			stmtInsertMessage.setLong(1, topicId);
			stmtInsertMessage.setString(2, post.getSubject());
			stmtInsertMessage.setString(3, post.getOriginalFrom());
			stmtInsertMessage.setString(4, post.getTextContent());
			stmtInsertMessage.setString(5, post.getRandom());
			stmtInsertMessage.execute();
			final Collection<AttachmentPath> attachments = post.getAttachments();
			if (attachments != null && !attachments.isEmpty()) {
				stmtLastVal = dbConnection.prepareStatement(LAST_VAL);
				resLastVal = stmtLastVal.executeQuery();
				resLastVal.next();
				long lastVal = resLastVal.getLong("last_id");
				stmtInsertAttachment = dbConnection.prepareStatement(INSERT_ATTACHMENT);
				for (AttachmentPath att : attachments) {
					stmtInsertAttachment.setLong(1, lastVal);
					stmtInsertAttachment.setString(2, att.getName());
					stmtInsertAttachment.setString(3, att.getContentType());
					stmtInsertAttachment.setString(4, att.getFilename());
					stmtInsertAttachment.execute();
				}
			}
			dbConnection.commit();
			dbConnection.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Collection<Topic> listTopics() throws CxException {
		log.info("list spaces");
		final Map<Long,Workspace> spacesById = new HashMap<>();
		final Map<Long,Topic> topicsById = new HashMap<>();
		PreparedStatement stmtSelectTopic = null;
		ResultSet rsTopic = null;
		try {
			Workspace workspace = null;
			Topic topic = null;
			stmtSelectTopic = dbConnection.prepareStatement(LIST_SPACES);
			rsTopic = stmtSelectTopic.executeQuery();
			while (rsTopic.next()) {
				final long workspaceId = rsTopic.getLong("ws_id");
				final Long workspaceIdLong = Long.valueOf(workspaceId);
				if (spacesById.containsKey(workspaceIdLong)) { 
					workspace = spacesById.get(workspaceIdLong);
				} else {
					workspace = new Workspace();
					workspace.setId(workspaceId);
					workspace.setName(rsTopic.getString("ws_name"));
					workspace.setDescription(rsTopic.getString("ws_desc"));
					spacesById.put(workspaceIdLong, workspace);
				}
				topic = new Topic();
				final long topicId = rsTopic.getLong("tp_id");
				topic.setId(topicId);
				topic.setName(rsTopic.getString("tp_name"));
				topic.setDescription(rsTopic.getString("tp_desc"));
				topic.setWebDomain(rsTopic.getString("tp_webdomain"));
				topic.setAddress(rsTopic.getString("tp_address"));
				final MailAccount imapAccount = new MailAccount();
				imapAccount.setHost(rsTopic.getString("imap_host"));
				imapAccount.setPort(rsTopic.getInt("imap_port"));
				imapAccount.setStartTLS(rsTopic.getBoolean("imap_starttls"));
				imapAccount.setLogin(rsTopic.getString("imap_login"));
				imapAccount.setPasswd(rsTopic.getString("imap_passwd"));
				topic.setImapAccount(imapAccount);
				final MailAccount smtpAccount = new MailAccount();
				smtpAccount.setHost(rsTopic.getString("smtp_host"));
				smtpAccount.setPort(rsTopic.getInt("smtp_port"));
				smtpAccount.setStartTLS(rsTopic.getBoolean("smtp_starttls"));
				smtpAccount.setLogin(rsTopic.getString("smtp_login"));
				smtpAccount.setPasswd(rsTopic.getString("smtp_passwd"));
				topic.setSmtpAccount(smtpAccount);
				topic.setWorkspace(workspace);
				topicsById.put(Long.valueOf(topicId), topic);
			}
		} catch (SQLException e) {
			log.error(e);
			throw new CxException(e);
		} finally {
			if (rsTopic != null) try { rsTopic.close(); } catch (Exception e) { };
			if (stmtSelectTopic != null) try { stmtSelectTopic.close(); } catch (Exception e) { };
		}
		return topicsById.values();
		
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
				workspace.setId(rsTopic.getLong("ws_id"));
				workspace.setName(rsTopic.getString("ws_name"));
				workspace.setDescription(rsTopic.getString("ws_desc"));
				topic.setId(rsTopic.getLong("tp_id"));
				topic.setName(rsTopic.getString("tp_name"));
				topic.setDescription(rsTopic.getString("tp_desc"));
				topic.setWebDomain(rsTopic.getString("tp_webdomain"));
				topic.setAddress(rsTopic.getString("tp_address"));
				final MailAccount imapAccount = new MailAccount();
				imapAccount.setHost(rsTopic.getString("imap_host"));
				imapAccount.setPort(rsTopic.getInt("imap_port"));
				imapAccount.setStartTLS(rsTopic.getBoolean("imap_starttls"));
				imapAccount.setLogin(rsTopic.getString("imap_login"));
				imapAccount.setPasswd(rsTopic.getString("imap_passwd"));
				topic.setImapAccount(imapAccount);
				final MailAccount smtpAccount = new MailAccount();
				smtpAccount.setHost(rsTopic.getString("smtp_host"));
				smtpAccount.setPort(rsTopic.getInt("smtp_port"));
				smtpAccount.setStartTLS(rsTopic.getBoolean("smtp_starttls"));
				smtpAccount.setLogin(rsTopic.getString("smtp_login"));
				smtpAccount.setPasswd(rsTopic.getString("smtp_passwd"));
				topic.setSmtpAccount(smtpAccount);
			} else {
				throw new CxException("no topic for " + address);
			}
			stmtSelectSubs = dbConnection.prepareStatement(SELECT_SUBSCRIPTIONS);
			stmtSelectSubs.setLong(1, topic.getId());
			rsSubs = stmtSelectSubs.executeQuery();
			while (rsSubs.next()) {
				final Subscriber scr = new Subscriber();
				scr.setId(rsSubs.getLong("scr_id"));
				scr.setAddress(rsSubs.getString("scr_address"));
				scr.setName(rsSubs.getString("scr_name"));
				final Subscription scn = new Subscription();
				scn.setSubscriber(scr);
				scn.setId(rsSubs.getLong("scn_id"));
				scn.setRecievesDigest(rsSubs.getBoolean("recieves_digest"));
				scn.setRecievesMessages(rsSubs.getBoolean("recieves_messages"));
				scn.setRecievesModeration(rsSubs.getBoolean("recieves_moderation"));
				scn.setMaySendMessages(rsSubs.getBoolean("may_send_messages"));
				scn.setSubscribeDate(convertToLocalDateTime(rsSubs.getDate("subscribe_date")));
				final Date unsubscribeDate = rsSubs.getDate("unsubscribe_date");
				if (unsubscribeDate == null) {
					scn.setUnsubscribeDate(LocalDateTime.of(2199, 12, 31, 23, 59, 59));
				} else {
					scn.setUnsubscribeDate(convertToLocalDateTime(unsubscribeDate));
				}
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
