package de.jalin.connecteam.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import de.jalin.connecteam.data.DataAccess;
import de.jalin.connecteam.data.Subscriber;
import de.jalin.connecteam.data.Subscription;
import de.jalin.connecteam.data.Topic;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.mail.loop.Sendmail;
import de.jalin.connecteam.mail.message.Post;

public class ApprovalServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger("ApprovalServlet.class");
	
	@Resource(name = "jdbc/connecteam")
	private DataSource dataSource;

	public ApprovalServlet() {
		super();
	}

	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException {
		try (Connection connection = dataSource.getConnection()) {
			final DataAccess dataAccess = new DataAccess(connection);
			final String pathInfo = req.getPathInfo();
			final String[] pathItems = pathInfo.substring(1).split("/");
			final String messageId = pathItems[0];
			log.info("approve message " + messageId);
			final Post post = dataAccess.loadMessage(messageId);
			final HttpSession session = req.getSession();
			if (post == null) {
				session.setAttribute("errormessage", "Diese Nachricht gibt es nicht in der Datenbank. Bitte prüfen Sie den Link.");
				req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
				return;
			}
			session.setAttribute("post", post);
			req.getRequestDispatcher("/WEB-INF/jsp/approval.jsp").forward(req, resp);
		} catch (SQLException | IOException | CxException e) {
			throw new ServletException(e);
		} 
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (Connection connection = dataSource.getConnection()) {
			final DataAccess dataAccess = new DataAccess(connection);
			final String pathInfo = req.getPathInfo();
			final String[] pathItems = pathInfo.substring(1).split("/");
			final String messageId = pathItems[0];
			log.info("approve message " + messageId);
			Object senderMaySendParam = req.getParameter("sendermaysend");
			Object approvalMessageParam = req.getParameter("approval");
			boolean senderMaySend = senderMaySendParam instanceof String &&  "on".equalsIgnoreCase((String) senderMaySendParam);
			boolean approveMessage = approvalMessageParam instanceof String && "approve-message".equals(approvalMessageParam);   
			log.info("  senderMaySend " + senderMaySend);
			log.info("  approval " + approveMessage);
			final Post post = dataAccess.loadMessage(messageId);
			if (post == null) {
				throw new ServletException("message_not_found_in_database");
			}
			final HttpSession session = req.getSession();
			session.setAttribute("post", post);
			if (approvalMessageParam == null) {
				session.setAttribute("errormessage", "Bitte entscheiden Sie, ob die Nachricht freigegeben werden kann.");
				req.getRequestDispatcher("/WEB-INF/jsp/error-box.jsp").forward(req, resp);
				return;
			}
			
			post.setStatus(approveMessage ? Post.POST_ACCEPTED : Post.POST_REJECTED);
			dataAccess.updateMessageStatus(post);
			
			if (approveMessage) {
				final Topic topic = dataAccess.loadTopic(post.getFromAddress());
				final Sendmail sendmail = new Sendmail(topic);
				sendmail.sendPost(post);
			}
			
			if (senderMaySend) {
				final String senderAddress = post.getOriginalFrom();
				Subscription senderSubscription = dataAccess.loadSubscription(post.getTopic().getId(), senderAddress);
				if (senderSubscription != null) {
					senderSubscription.setTopic(post.getTopic());
					senderSubscription.setMaySendMessages(true);
				} else {
					senderSubscription = new Subscription();
					senderSubscription.setMaySendMessages(true);
					senderSubscription.setRecievesDigest(false);
					senderSubscription.setRecievesMessages(false);
					senderSubscription.setRecievesModeration(false);
					senderSubscription.setSubscribeDate(LocalDateTime.now());
					final Subscriber subscriber = new Subscriber();
					subscriber.setAddress(senderAddress);
					senderSubscription.setSubscriber(subscriber);
					senderSubscription.setTopic(post.getTopic());
				}
				dataAccess.saveSubscription(senderSubscription);
			}
			
			session.setAttribute("successmessage", approveMessage ? "Die Nachricht wurde freigegeben." : "Die Nachricht wurde zurückgewiesen.");
			req.getRequestDispatcher("/WEB-INF/jsp/success-box.jsp").forward(req, resp);
		} catch (SQLException | IOException | CxException e) {
			throw new ServletException(e);
		} 
	}
}
