/*
 * Created on 24.07.2018
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package kz.ecc.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import com.sapportals.portal.prt.component.IPortalComponentRequest;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
/**
 * @author mesh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SendEmail {
	private IPortalComponentRequest request;
	
	private String email_receiver;
	private String email_text;
	private String email_sender;
	
	public static final String DATASOURCE_NAME = "jdbc/roma_temp_ds";

	private static Connection getConnection() throws SQLException {
		// get new connection from connection pool
		try {
			// perform JNDI lookup to get a datasource from container
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(DATASOURCE_NAME);
			return ds.getConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public SendEmail(IPortalComponentRequest request) {
		this.request = request;
		extractFieldsData(request);
	}
	
	private void extractFieldsData(IPortalComponentRequest request) {
		
		email_receiver =
			request.getParameter("recipient") == null
				? null
				: request.getParameter("recipient").trim();

		email_text =
			request.getParameter("email_text") == null
				? null
				: request.getParameter("email_text").trim();
		
		email_sender = "devep@minfin.kz";
		
		Logger.getLogger("forum-project").info("EMAIL, SURNAME, NAME, QUESTION " + email_receiver + " " + email_text);
		
		// Assuming you are sending email from localhost
		String host = "10.58.56.200";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try {
		   // Create a default MimeMessage object.
		   MimeMessage message = new MimeMessage(session);

		   // Set From: header field of the header.
		   message.setFrom(new InternetAddress(email_sender));

		   // Set To: header field of the header.
		   message.addRecipient(Message.RecipientType.TO, new InternetAddress(email_receiver));

		   // Set Subject: header field
		   message.setSubject("This is the Subject Line!");

		   // Now set the actual message
		   message.setText(email_text);

		   // Send message
		   Transport.send(message);
		   System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
		   mex.printStackTrace();
		}
		
	}

	
}
