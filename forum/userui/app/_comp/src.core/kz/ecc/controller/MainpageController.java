/*
 * Created on 02.04.2018
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.resource.IResource;

/**
 * @author mesh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MainpageController extends AbstractPortalComponent {
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

	static final int FORUM_STATE = 0;
	static final int QUESTION_STATE = 1;
	static final int ERROR_STATE = 2;

	protected void doContent(
		IPortalComponentRequest request,
		IPortalComponentResponse response) {
			try {
		int state = FORUM_STATE;

		boolean isThemeLinkClicked =
			request.getParameter("ask_question2") != null;

		boolean isAskQuestionClicked =
			request.getParameter("send_question") != null;
			
			
		if (isThemeLinkClicked) {
			Logger.getLogger("forum-project").info("isThemeLinkClicked " + isThemeLinkClicked);
			state = QUESTION_STATE;
		}else if (isAskQuestionClicked) {
			Logger.getLogger("forum-project").info("isAskQuestionClicked is active" + isAskQuestionClicked);
			QuestionHandler handler = new QuestionHandler(request);	
			request.getNode().putValue("formHandlerInstance", handler);
			if(handler.validate()){
				Logger.getLogger("forum-project").info("Valid is true");
				MailSender sender = new MailSender(request);
				sender.sendMail(handler.getRegistationForm());
				state=FORUM_STATE;
			}else{
				Logger.getLogger("forum-project").info("Valid is false");
				state = FORUM_STATE;
			}
				

		}else{
			Logger.getLogger("forum-project").info("Something is wrong");
		}

		showPage(request, response, state);
			} catch (Exception e) {
				showPage(request, response, ERROR_STATE);
				response.write(
					"<p><font size=\"1px\">Error: "
						+ e.getMessage()
						+ "</font></p>");
				e.printStackTrace();
			}
	}

	private void showPage(
		IPortalComponentRequest request,
		IPortalComponentResponse response,
		int state) {
		IResource form = null;
		
		switch (state) {
			case FORUM_STATE :
				Logger.getLogger("USER PROJECT").info("FORUM STATE is active");
				request.getNode().putValue("themeResult", getMainpageData());
				form = request.getResource("jsp", "jsp/mainpage.jsp");
				break;
			case QUESTION_STATE :
				Logger.getLogger("USER PROJECT").info("QUESTION STATE is active");
				request.getNode().putValue("questionResult", getQuestionData());
				form = request.getResource("jsp", "jsp/askquestion.jsp");
				break;
			case ERROR_STATE : 
				Logger.getLogger("USER PROJECT").info("ERROR STATE is active");
				form = request.getResource("jsp", "jsp/neg_answer.jsp");
				break; 
				
		}
		response.include(request, form);
	}
	
	public static String getNLSString(
		IPortalComponentRequest request,
		String nlsKey) {
		String value = nlsKey;
		try {
			ResourceBundle bundle = request.getResourceBundle();
			if (bundle != null)
				value = bundle.getString(nlsKey);
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}

		return value;
	}

	/**
	 * 
	 * @return ru || en || kz || null
	 * */
	public static String getCurrentLanguage(IPortalComponentRequest request) {
		Locale locale = null;
		String loLocale = null;
		HttpSession sess = request.getServletRequest().getSession();
		// пробуем достать из сессии(если аноним) 
		locale = (Locale) sess.getAttribute("sessionLocale");
		if (locale == null) // если авторизованный
			locale = request.getUser().getLocale();
		if (locale != null)
			loLocale = locale.getLanguage();
		if (loLocale != null
			&& ("z1".equalsIgnoreCase(loLocale)
				|| "kk".equalsIgnoreCase(loLocale)))
			loLocale = "kz";
		if (loLocale != null && loLocale.length() > 0)
			return loLocale;
		return "kz"; //default
	}
	
	public String getLastAnswerDate() {
		String lastAnswerDate = null;
		
		String sql = "select answered_time from ecc_forum_answer where answered_time = (select max(answered_time) from ecc_forum_answer)";
		Connection conn = null;
				PreparedStatement ps = null;
							ResultSet rs = null;
				try {
					conn = getConnection();
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					
					if(rs.next()){
						Timestamp time = rs.getTimestamp("answered_time");
						lastAnswerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
						
					}
				} catch (Exception ex) {
							ex.printStackTrace();
			
							System.err.println(ex);
						} finally {
							if (rs != null) {
								try {
									rs.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								rs = null;
							}
							if (ps != null) {
								try {
									ps.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									System.err.println(e);
								}
								ps = null;
							}

							if (conn != null) {
								try {
									conn.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									System.err.println(e);
								}
								conn = null;
							}
						}					
					
		
		Logger.getLogger("forum-project").info("LAST ANSWER DATE " + lastAnswerDate);
		return lastAnswerDate;
	}
	
	public int getMessageCount(){
		int messageCount = 0;
		String sql = "SELECT count(id) as rowcount from ECC_FORUM_ANSWER";
		Connection conn = null;
						PreparedStatement ps = null;
									ResultSet rs = null;
						try {
							conn = getConnection();
							ps = conn.prepareStatement(sql);
							rs = ps.executeQuery();
					
							if(rs.next()){
								messageCount = rs.getInt("rowcount");
							}
						} catch (Exception ex) {
									ex.printStackTrace();
			
									System.err.println(ex);
								} finally {
									if (rs != null) {
										try {
											rs.close();
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										rs = null;
									}
									if (ps != null) {
										try {
											ps.close();
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											System.err.println(e);
										}
										ps = null;
									}

									if (conn != null) {
										try {
											conn.close();
										} catch (SQLException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											System.err.println(e);
										}
										conn = null;
									}
								}		
		return messageCount;		
	}

	// method to get data for question form
	public static ArrayList getQuestionData() {
		ArrayList list = new ArrayList();
		String sql =
			"SELECT t.ID as THEME_ID, " +
			"t.THEME_RU, " +			"t.THEME_KZ, " +			"t.AUTHOR_NAME, " +
			"t.TIME_OF_CREATION, " +
			"t.DESCRIPTION_RU, " +			"t.DESCRIPTION_KZ, " +
			"q.ID as QUESTION_ID, " +
			"q.surname, " +
			"q.name, " +
			"q.DATE_CREATE, " +
			"q.QUESTION, " +
			"a.id as answer_id, " +
			"a.answer, " +
			"a.question_id as answer_question_id, " +
			"a.author as answer_author, " +
			"a.answered_time " +
			"FROM ECC_FORUM_THEME t, " +
			"ECC_FORUM_QUESTION q, " +
			"ECC_FORUM_ANSWER a " +
			"where t.ID = 2 and t.id = q.THEME_ID and q.STATUS_ID = 2 and q.ID = a.question_id order by q.DATE_CREATE desc";

		Connection conn = null;
		PreparedStatement ps = null;
					ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			Object theme_id = null;
			Object theme_name_ru = null;
			Object theme_name_kz = null;
			Object author_name = null;
			Timestamp date = null;
			Object description_ru = null;
			Object description_kz = null;
			Object question_id = null;
			Object surname = null;
			Object name = null;
			Timestamp time = null;
			Object question = null;
			Object answer_id = null;
			Object answer = null;
			Object answer_questionId = null;
			Object answer_author = null;
			Timestamp answered_time = null;
			int columns = rsmd.getColumnCount();
			while (rs.next()) {
				theme_id = rs.getObject(1);
				theme_name_ru = rs.getObject(2);
				theme_name_kz = rs.getObject(3);
				author_name = rs.getObject(4);
				date = rs.getTimestamp(5);
				description_ru = rs.getObject(6);
				description_kz = rs.getObject(7);
				question_id = rs.getObject(8);
				surname = rs.getObject(9);
				name = rs.getObject(10);
				time = rs.getTimestamp(11);
				question = rs.getObject(12);
				answer_id = rs.getObject(13);
				answer = rs.getObject(14);
				answer_questionId = rs.getObject(15);
				answer_author = rs.getObject(16);
				answered_time = rs.getTimestamp(17);
				HashMap row = new HashMap();
				for (int i = 1; i <= columns; i++) {
					row.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				row.put("THEME_ID", theme_id);
				row.put("THEME_RU", theme_name_ru);
				row.put("THEME_KZ", theme_name_kz);
				row.put("AUTHOR_NAME", author_name);
				row.put("TIME_OF_CREATION", date);
				row.put("DESCRIPTION_RU", description_ru);
				row.put("DESCRIPTION_KZ", description_kz);
				row.put("QUESTION_ID", question_id);
				row.put("SURNAME", surname);
				row.put("NAME", name);
				row.put("DATE_CREATE", time);
				row.put("QUESTION", question);
				row.put("ANSWER_ID", answer_id);
				row.put("ANSWER", answer);
				row.put("ANSWER_QUESTION_ID", answer_questionId);
				row.put("ANSWER_AUTHOR", answer_author);
				row.put("ANSWERED_TIME", answered_time);
				list.add(row);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			
			System.err.println(ex);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rs = null;
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println(e);
				}
				ps = null;
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println(e);
				}
				conn = null;
			}
		}

		return list;
	}

	public static ArrayList getMainpageData() {
		ArrayList list = new ArrayList();
		String sql =
			"SELECT ID, THEME_RU, THEME_KZ, AUTHOR_NAME, AUTHOR_EMAIL, TIME_OF_CREATION, DESCRIPTION_RU, DESCRIPTION_KZ FROM ECC_FORUM_THEME order by TIME_OF_CREATION";
		try {
			Connection conn = null;
			try {
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					conn = getConnection();
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					ResultSetMetaData rsmd = rs.getMetaData();
					Object id = null;
					Object theme_ru = null;
					Object theme_kz = null;
					Object author_name = null;
					Object author_email = null;
					Timestamp date = null;
					Object description_ru = null;
					Object description_kz = null;
					int columns = rsmd.getColumnCount();
					while (rs.next()) {
						id = rs.getObject(1);
						theme_ru = rs.getObject(2);
						theme_kz = rs.getObject(3);
						author_name = rs.getObject(4);
						author_email = rs.getObject(5);
						date = rs.getTimestamp(6);
						description_ru = rs.getObject(7);
						description_kz = rs.getObject(8);
						HashMap row = new HashMap();
						for (int i = 1; i <= columns; i++) {
							row.put(rsmd.getColumnName(i), rs.getObject(i));
						}
						row.put("ID", id);
						row.put("THEME_RU", theme_ru);
						row.put("THEME_KZ", theme_kz);
						row.put("AUTHOR_NAME", author_name);
						row.put("AUTHOR_EMAIL", author_email);
						row.put("TIME_OF_CREATION", date);
						row.put("DESCRIPTION_RU", description_ru);
						row.put("DESCRIPTION_KZ", description_kz);
						list.add(row);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					//			request.setAttribute("error", ex.getCause());
				} finally {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (ps != null) {
						ps.close();
						ps = null;
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
		return list;
	}
}
