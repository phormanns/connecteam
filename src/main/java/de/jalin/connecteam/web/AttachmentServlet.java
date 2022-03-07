package de.jalin.connecteam.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import de.jalin.connecteam.data.DataAccess;
import de.jalin.connecteam.etc.Config;
import de.jalin.connecteam.etc.DataDir;
import de.jalin.connecteam.etc.Logger;
import de.jalin.connecteam.mail.message.AttachmentPath;

public class AttachmentServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger("AttachmentServlet.class");
	
	@Resource(name = "jdbc/connecteam")
	private DataSource dataSource;

	private DataDir datadir;
	
	public AttachmentServlet() {
        super();
    }

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			final Config conf = Config.load(Paths.get("conf/config.yaml"));
			datadir = conf.getDatadir();
		} catch (IOException e) {
			new ServletException(e);
		}

	}
	
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		try (Connection connection = dataSource.getConnection()) {
			final DataAccess dataAccess = new DataAccess(connection);
			final String pathInfo = request.getPathInfo();
			log.info("load attachment " + pathInfo);
			final String[] pathItems = pathInfo.substring(1).split("/");
			final String messageId = pathItems[0]; 
			final String attachmentId = pathItems[1];
			final AttachmentPath attachmentPath = dataAccess.loadAttachment(messageId, attachmentId);
			if (attachmentPath == null) {
				throw new ServletException("attachment not found " + pathInfo);
			}
			final String mimeType = attachmentPath.getContentType();
			response.setContentType(mimeType);
			String path = datadir.getPath() + "/" + messageId + "/" + attachmentPath.getFilename();
			FileInputStream inputStream = new FileInputStream(path);
			final ServletOutputStream outputStream = response.getOutputStream();
			byte[] buf = new byte[4096];
			int rd = inputStream.read(buf);
			while (rd > 0) {
				outputStream.write(buf, 0, rd);
				rd = inputStream.read(buf);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException | SQLException e) {
			throw new ServletException(e);
		} 
	}

}
