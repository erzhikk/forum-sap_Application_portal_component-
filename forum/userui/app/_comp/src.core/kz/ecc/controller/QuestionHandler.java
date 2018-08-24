/*
 * Created on 21.05.2018
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package kz.ecc.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.sapportals.portal.prt.component.IPortalComponentRequest;

/**
 * @author mesh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class QuestionHandler {
	private IPortalComponentRequest request;
	
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

		public static final String EMAIL = "sender_email";
		public static final String SURNAME = "sender_surname";
		public static final String NAME = "sender_name";
		public static final String FATHERNAME = "sender_fathername";
		public static final String QUESTION = "question";

		private Hashtable errors;
		private String emailDB;
		private String surnameDB;
		private String nameDB;
		private String fathernameDB;
		private String questionDB;

		/**
		 * @param request
		 */
		private void extractFieldsData(IPortalComponentRequest request) {
			errors = new Hashtable();
		
			emailDB =
				request.getParameter(EMAIL) == null
					? null
					: request.getParameter(EMAIL).trim();

			surnameDB =
				request.getParameter(SURNAME) == null
					? null
					: request.getParameter(SURNAME).trim();

			nameDB =
				request.getParameter(NAME) == null
					? null
					: request.getParameter(NAME).trim();


			questionDB =
				request.getParameter(QUESTION) == null
					? null
					: request.getParameter(QUESTION).trim();
			Logger.getLogger("forum-project").info("EMAIL, SURNAME, NAME, QUESTION " + emailDB + " " + surnameDB + " " + nameDB + " " + questionDB);

			insertIntoDB(emailDB, surnameDB, nameDB, questionDB);
		}

		public QuestionHandler(IPortalComponentRequest request) {
			this.request = request;
			extractFieldsData(request);
		}

		private void insertIntoDB(
			String emailDB,
			String surnameDB,
			String nameDB,
			String questionDB) {
			
			String id = "2";	
			String email = emailDB;
			String surname = surnameDB;
			String name = nameDB;
			String fathername = fathernameDB;
			String question = questionDB;
			try {
				Connection conn = null;
				try {
					PreparedStatement st = null;
					try {
						String sql = "insert into ECC_FORUM_QUESTION (theme_id, question, surname, name, email) values (?, ?, ?, ?, ?)";
						conn = getConnection();
						st = conn.prepareStatement(sql);
						st.setString(1, id);
						st.setString(2, question);
						st.setString(3, surname);
						st.setString(4, name);
						st.setString(5, email);
						st.executeUpdate();
					
					} catch (Exception ex) {
						ex.printStackTrace();
						//			request.setAttribute("error", ex.getCause());
					} finally {
						if (st != null) {
							st.close();
							st = null;
						}
					}
				} finally {
					if (conn != null) {
						conn.close();
						conn = null;
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				//		request.setAttribute("error", ex.getCause());
			}
		}
	
		public String getRegistationForm() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\n			Новый вопрос по форуму");
			buffer.append(
				"\n___________________________________________________________");
			buffer.append("\nФ.И.О. задающий вопрос: ");
			buffer.append(this.surnameDB + " " + this.nameDB);

			buffer.append("\nОписание вопроса: ");
			buffer.append(this.questionDB);
			buffer.append("\n\n			email заявителя: ");
			buffer.append(this.emailDB);
			return buffer.toString();
		}
	
		public boolean validate() {
			ResourceBundle bundle = request.getResourceBundle();
			boolean isValid = true;
			if (emailDB != null && emailDB.length() == 0) {
				errors.put(EMAIL, bundle.getString("sender_email"));
				isValid = false;
			}
		
			if (surnameDB != null && surnameDB.length() == 0) {
				errors.put(SURNAME, bundle.getString("sender_surname"));
				isValid = false;
			}
		
			if (nameDB != null && nameDB.length() == 0) {
				errors.put(NAME, bundle.getString("sender_name"));
				isValid = false;
			}
		
			if (questionDB != null && questionDB.length() == 0) {
				errors.put(QUESTION, bundle.getString("question"));
				isValid = false;
			}
		
			return isValid;
		}
}
