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

	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try (Connection connection = dataSource.getConnection()) {
			final DataAccess dataAccess = new DataAccess(connection);
			final String pathInfo = request.getPathInfo();
			final String[] pathItems = pathInfo.substring(1).split("/");
			final String messageId = pathItems[0];
			final Post post = dataAccess.loadMessage(messageId);
			log.info("approve message " + messageId);
			final HttpSession httpSession = request.getSession();
			httpSession.setAttribute("post", post);
			request.getRequestDispatcher("/WEB-INF/jsp/approval.jsp").forward(request, response);
		} catch (SQLException | IOException e) {
			throw new ServletException(e);
		} 
	}
}
