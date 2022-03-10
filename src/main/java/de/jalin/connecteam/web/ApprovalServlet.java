package de.jalin.connecteam.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import de.jalin.connecteam.data.DataAccess;
import de.jalin.connecteam.etc.CxException;
import de.jalin.connecteam.etc.Logger;
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
			if (post == null) {
				throw new ServletException("message_not_found_in_database");
			}
			final HttpSession session = req.getSession();
			session.setAttribute("approvalpath", pathInfo);
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
			Object senderMaySend = req.getParameter("sendermaysend");
			Object approvalMessage = req.getParameter("approval");
			log.info("  senderMaySend " + senderMaySend);
			log.info("  approval " + approvalMessage);
			final Post post = dataAccess.loadMessage(messageId);
			if (post == null) {
				throw new ServletException("message_not_found_in_database");
			}
			final HttpSession session = req.getSession();
			session.setAttribute("approvalpath", pathInfo);
			session.setAttribute("post", post);
			// req.getRequestDispatcher("/WEB-INF/jsp/approval.jsp").forward(req, resp);
			resp.getWriter().write("<div>DONE</div>\n");
		} catch (SQLException | IOException | CxException e) {
			throw new ServletException(e);
		} 
	}
}
