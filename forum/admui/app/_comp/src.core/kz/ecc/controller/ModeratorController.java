package kz.ecc.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.sapportals.portal.prt.component.AbstractPortalComponent;
import com.sapportals.portal.prt.component.IPortalComponentRequest;
import com.sapportals.portal.prt.component.IPortalComponentResponse;
import com.sapportals.portal.prt.resource.IResource;
import com.sapportals.portal.prt.component.*;

/**
 * @author mesh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ModeratorController extends AbstractPortalComponent {
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

	static final int MODERATOR_DESKTOP = 0;
	static final int STATUS_CHANGE = 1;
	static final int ERROR_STATE = 2;
	static final int ANSWER_STATE = 3;
	static final int SEARCH_STATE = 4;
	static final int SEARCH_BY_DATE_STATE = 5;
	static final int SEND_EMAIL_STATE = 6;
	
	protected void doContent(IPortalComponentRequest request, IPortalComponentResponse response) {
		try {
			int state = MODERATOR_DESKTOP;
			
			boolean isSearchByDateBegin = request.getParameter("search_by_date_begin") != null;
			boolean isSearchByDateEnd = request.getParameter("search_by_date_end") != null;
			boolean isSearchBySurname = request.getParameter("search") != null;
			
			boolean isStatusDefined = request.getParameter("status") != null;
			
			boolean isAnswerButtonClicked = request.getParameter("answer") != null;
			boolean isAnswerQuestionClicked = request.getParameter("send_answer") != null;
			boolean isCancelAskClicked = request.getParameter("cancel_ask") != null;
			boolean isSendEmailButtonClicked = request.getParameter("send_email_state") != null;	
			boolean isSendEmail = request.getParameter("send_email") != null;	
			
			if (isSearchByDateBegin && isSearchByDateEnd) {
				state = SEARCH_BY_DATE_STATE;				
			}else if(isSearchBySurname){
				state = SEARCH_STATE;
			}else if(isStatusDefined){
				String questionId = (String) request.getParameter("status");
				String[] defineMethodId = questionId.split("_");
				String method = defineMethodId[0];
				String Id = defineMethodId[1];
				updateStatus(method, Id);
			}else if(isAnswerButtonClicked){
				state = ANSWER_STATE;
			}else if (isAnswerQuestionClicked) {
				AnswerHandler handler = new AnswerHandler(request);
				state = MODERATOR_DESKTOP;	
			}else if(isSendEmailButtonClicked){
				String id = request.getParameter("email_question_id");
				Logger.getLogger("moderator_project").info("1 SEND EMAIL STATE is " + isSendEmailButtonClicked + " id = " + id);
				state = SEND_EMAIL_STATE;
			}else if(isSendEmail){
				Logger.getLogger("moderator_project").info(" EMAIL is sent");
				SendEmail send = new SendEmail(request);		
				state = MODERATOR_DESKTOP;	
			}else if(isCancelAskClicked){
				state = MODERATOR_DESKTOP;
			}else {
				state = MODERATOR_DESKTOP;
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
	
	
	public void showPage(IPortalComponentRequest request, IPortalComponentResponse response, int state) {
		try {		
		IResource form = null;

		String surname = request.getParameter("search");

			switch (state) {
				case MODERATOR_DESKTOP : 
					Logger.getLogger("moderator_project").info("MODERATOR STATE is active");
					request.getNode().putValue("searchResult", getMainpageData());
					form = request.getResource("jsp", "jsp/desktop.jsp");
					break;
				
				case SEARCH_STATE :  
					Logger.getLogger("moderator_project").info("SEARCH NAME STATE is active");
					request.getNode().putValue("searchResult", searchBySurname(surname));
					form = request.getResource("jsp", "jsp/desktop.jsp");
					break;
				
				
				case SEARCH_BY_DATE_STATE : 
					Logger.getLogger("moderator_project").info("SEARCH DATE STATE is active");
					String date_begin = request.getParameter("search_by_date_begin");
					String date_end = request.getParameter("search_by_date_end");
			
					SimpleDateFormat oldStringFormatyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat formatForOracleDB = new SimpleDateFormat("dd-MM-yyyy");
			
					Date date_01 = oldStringFormatyyyyMMdd.parse(date_begin);
					Date date_02 = oldStringFormatyyyyMMdd.parse(date_end);
				
					String dateForOracle01 = formatForOracleDB.format(date_01);
					String dateForOracle02 = formatForOracleDB.format(date_02);
					request.getNode().putValue("searchResult", searchByDate(dateForOracle01, dateForOracle02));
					form = request.getResource("jsp", "jsp/desktop.jsp");
					break;
				case SEND_EMAIL_STATE : 
					String id = request.getParameter("email_question_id");
					Logger.getLogger("moderator_project").info("2 SEND EMAIL STATE is active, id === " + id);
					request.getNode().putValue("sendEmail", sendEmailInfo(id));
					form = request.getResource("jsp", "jsp/send_email.jsp");
					break;
				
				case ERROR_STATE : 
					Logger.getLogger("moderator_project").info("ERROR STATE is active");
					form = request.getResource("jsp", "jsp/neg_answer.jsp");
					break;
				
				case ANSWER_STATE : 
					Logger.getLogger("moderator_project").info("ANSWER STATE is active");
					request.getNode().putValue("answerResult", getAnswerQuestionData(request));
					form = request.getResource("jsp", "jsp/answer_question.jsp");
					break;
				
				default :
					form = request.getResource("jsp", "jsp/desktop.jsp");
			}
			response.include(request, form);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static ArrayList sendEmailInfo(String id) {
		ArrayList list = new ArrayList();
		String sql =
			"SELECT ID, "
				+ "QUESTION, "
				+ "DATE_CREATE, "
				+ "SURNAME, "
				+ "NAME, "
				+ "EMAIL "
				+ "FROM ECC_FORUM_QUESTION WHERE ID = "
				+ id;
		Connection conn = null;
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				Object question_id = null;
				Object question = null;
				Timestamp date_create = null;
				Object surname = null;
				Object name = null;
				Object email = null;

				int columns = rsmd.getColumnCount();
				while (rs.next()) {
					question_id = rs.getObject(1);
					question = rs.getObject(2);
					date_create = rs.getTimestamp(3);
					surname = rs.getObject(4);
					name = rs.getObject(5);
					email = rs.getObject(6);
					HashMap row = new HashMap();
					for (int i = 1; i <= columns; i++) {
						row.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					row.put("ID", question_id);
					row.put("QUESTION", question);
					row.put("DATE_CREATE", date_create);
					row.put("SURNAME", surname);
					row.put("NAME", name);
					row.put("EMAIL", email);
					list.add(row);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				//			request.setAttribute("error", ex.getCause());
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
					}
					ps = null;
				}
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return list;
	}	

	public static ArrayList getAnswerQuestionData(IPortalComponentRequest request) {
		ArrayList list = new ArrayList();
		String ID = request.getParameter("answer");
		String sql =
			"SELECT ID, "
				+ "QUESTION, "
				+ "DATE_CREATE, "
				+ "SURNAME, "
				+ "NAME, "
				+ "EMAIL "
				+ "FROM ECC_FORUM_QUESTION WHERE ID = "
				+ ID;
		Connection conn = null;
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				Object question_id = null;
				Object question = null;
				Timestamp date_create = null;
				Object surname = null;
				Object name = null;
				Object email = null;

				int columns = rsmd.getColumnCount();
				while (rs.next()) {
					question_id = rs.getObject(1);
					question = rs.getObject(2);
					date_create = rs.getTimestamp(3);
					surname = rs.getObject(4);
					name = rs.getObject(5);
					email = rs.getObject(6);
					HashMap row = new HashMap();
					for (int i = 1; i <= columns; i++) {
						row.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					row.put("ID", question_id);
					row.put("QUESTION", question);
					row.put("DATE_CREATE", date_create);
					row.put("SURNAME", surname);
					row.put("NAME", name);
					row.put("EMAIL", email);
					list.add(row);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				//			request.setAttribute("error", ex.getCause());
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
					}
					ps = null;
				}
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return list;
	}


	public static ArrayList getMainpageData() {
		ArrayList list = new ArrayList();
		String sql =
			"SELECT t.ID as THEME_ID, "
				+ "t.THEME_RU, "
				+ "t.DESCRIPTION_RU, "
				+ "t.AUTHOR_NAME as THEME_AUTHOR, "
				+ "t.TIME_OF_CREATION as THEME_CREATED_TIME, "
				+ "q.ID as QUESTION_ID, "
				+ "q.QUESTION, "
				+ "q.DATE_CREATE as QUESTION_CREATED_TIME, "
				+ "q.SURNAME, "
				+ "q.NAME, "
				+ "q.EMAIL, "
				+ "q.STATUS_ID "
				+ "FROM ECC_FORUM_THEME t, "
				+ "ECC_FORUM_QUESTION q "
				+ "order by THEME_CREATED_TIME, "
				+ "QUESTION_CREATED_TIME desc";

		Connection conn = null;
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				Object theme_id = null;
				Object theme_ru = null;
				Object description_ru = null;
				Object theme_author = null;
				Timestamp theme_created_time = null;
				Object question_id = null;
				Object question = null;
				Timestamp question_created_time = null;
				Object surname = null;
				Object name = null;
				Object email = null;
				Object status_id = null;
				Object answer = null;
				Object author = null;
				Timestamp answered_time = null;
				Object answer_questionId = null;
				int columns = rsmd.getColumnCount();
				while (rs.next()) {
					theme_id = rs.getObject(1);
					theme_ru = rs.getObject(2);
					description_ru = rs.getObject(3);
					theme_author = rs.getObject(4);
					theme_created_time = rs.getTimestamp(5);
					question_id = rs.getObject(6);
					question = rs.getObject(7);
					question_created_time = rs.getTimestamp(8);
					surname = rs.getObject(9);
					name = rs.getObject(10);
					email = rs.getObject(11);
					status_id = rs.getObject(12);

					HashMap row = new HashMap();
					for (int i = 1; i <= columns; i++) {
						row.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					row.put("THEME_ID", theme_id);
					row.put("THEME_RU", theme_ru);
					row.put("DESCRIPTION_RU", description_ru);
					row.put("THEME_AUTHOR", theme_author);
					row.put("THEME_CREATED_TIME", theme_created_time);
					row.put("QUESTION_ID", question_id);
					row.put("QUESTION", question);
					row.put("QUESTION_CREATED_TIME", question_created_time);
					row.put("SURNAME", surname);
					row.put("NAME", name);
					row.put("EMAIL", email);
					row.put("STATUS_ID", status_id);
					list.add(row);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				//			request.setAttribute("error", ex.getCause());
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
					}
					ps = null;
				}
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return list;
	}

	public void updateStatus(String method, String Id) {
		if (method.equalsIgnoreCase("approve")) {
			String sql =
				"update ecc_forum_question set status_id = 2 where id = " + Id;

			Connection conn = null;
			try {
				Statement ps = null;
				try {
					conn = getConnection();
					ps = conn.createStatement();
					ps.executeUpdate(sql);
				} catch (Exception ex) {
					ex.printStackTrace();
					//			request.setAttribute("error", ex.getCause());
				} finally {
					if (ps != null) {
						try {
							ps.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ps = null;
					}
				}
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					conn = null;
				}
			}
		} else if (method.equalsIgnoreCase("reject")) {
			String sql =
				"update ecc_forum_question set status_id = 3 where id = " + Id;

			Connection conn = null;
			try {
				Statement ps = null;
				try {
					conn = getConnection();
					ps = conn.createStatement();
					ps.executeUpdate(sql);
				} catch (Exception ex) {
					ex.printStackTrace();
					//			request.setAttribute("error", ex.getCause());
				} finally {
					if (ps != null) {
						try {
							ps.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ps = null;
					}
				}
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					conn = null;
				}
			}
		}

	}
	public ArrayList searchBySurname(String req_surname) {
		ArrayList list = new ArrayList();
		String sql =
			"SELECT t.ID as THEME_ID, "
				+ "t.THEME_RU, "
				+ "t.DESCRIPTION_RU, "
				+ "t.AUTHOR_NAME as THEME_AUTHOR, "
				+ "t.TIME_OF_CREATION as THEME_CREATED_TIME, "
				+ "q.ID as QUESTION_ID, "
				+ "q.QUESTION, "
				+ "q.DATE_CREATE as QUESTION_CREATED_TIME, "
				+ "q.SURNAME, "
				+ "q.NAME, "
				+ "q.EMAIL, "
				+ "q.STATUS_ID "
				+ "FROM ECC_FORUM_THEME t, "
				+ "ECC_FORUM_QUESTION q "
				+ "where q.SURNAME = '" + req_surname + "' " 
				+ "order by THEME_CREATED_TIME, "
				+ "QUESTION_CREATED_TIME desc";
				
		Logger.getLogger("moderator_project").info("searchBySurname method is working " );

		Connection conn = null;
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				Object theme_id = null;
				Object theme_ru = null;
				Object description_ru = null;
				Object theme_author = null;
				Timestamp theme_created_time = null;
				Object question_id = null;
				Object question = null;
				Timestamp question_created_time = null;
				Object surname = null;
				Object name = null;
				Object email = null;
				Object status_id = null;
				Object answer = null;
				Object author = null;
				Timestamp answered_time = null;
				Object answer_questionId = null;
				int columns = rsmd.getColumnCount();
				while (rs.next()) {
					theme_id = rs.getObject(1);
					theme_ru = rs.getObject(2);
					description_ru = rs.getObject(3);
					theme_author = rs.getObject(4);
					theme_created_time = rs.getTimestamp(5);
					question_id = rs.getObject(6);
					question = rs.getObject(7);
					question_created_time = rs.getTimestamp(8);
					surname = rs.getObject(9);
					name = rs.getObject(10);
					email = rs.getObject(11);
					status_id = rs.getObject(12);

					HashMap row = new HashMap();
					for (int i = 1; i <= columns; i++) {
						row.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					row.put("THEME_ID", theme_id);
					row.put("THEME_RU", theme_ru);
					row.put("DESCRIPTION_RU", description_ru);
					row.put("THEME_AUTHOR", theme_author);
					row.put("THEME_CREATED_TIME", theme_created_time);
					row.put("QUESTION_ID", question_id);
					row.put("QUESTION", question);
					row.put("QUESTION_CREATED_TIME", question_created_time);
					row.put("SURNAME", surname);
					row.put("NAME", name);
					row.put("EMAIL", email);
					row.put("STATUS_ID", status_id);
					list.add(row);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				//			request.setAttribute("error", ex.getCause());
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
					}
					ps = null;
				}
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return list;
	}
	
	public ArrayList searchByDate(String dateBegin, String dateEnd) {
		ArrayList list = new ArrayList();
		String sql =
			"SELECT t.ID as THEME_ID, "
				+ "t.THEME_RU, "
				+ "t.DESCRIPTION_RU, "
				+ "t.AUTHOR_NAME as THEME_AUTHOR, "
				+ "t.TIME_OF_CREATION as THEME_CREATED_TIME, "
				+ "q.ID as QUESTION_ID, "
				+ "q.QUESTION, "
				+ "q.DATE_CREATE as QUESTION_CREATED_TIME, "
				+ "q.SURNAME, "
				+ "q.NAME, "
				+ "q.EMAIL, "
				+ "q.STATUS_ID "
				+ "FROM ECC_FORUM_THEME t, "
				+ "ECC_FORUM_QUESTION q "
				+ "where q.DATE_ASK BETWEEN to_date('" + dateBegin + "', 'dd-MM-yyyy') AND  to_date('" + dateEnd  
				+ "', 'dd-MM-yyyy') order by THEME_CREATED_TIME, "
				+ "QUESTION_CREATED_TIME desc";

		Logger.getLogger("moderator_project").info("AAA searchByDate method is working " + dateBegin + " -- " + dateEnd);


		Connection conn = null;
		try {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				Object theme_id = null;
				Object theme_ru = null;
				Object description_ru = null;
				Object theme_author = null;
				Timestamp theme_created_time = null;
				Object question_id = null;
				Object question = null;
				Timestamp question_created_time = null;
				Object surname = null;
				Object name = null;
				Object email = null;
				Object status_id = null;
				Object answer = null;
				Object author = null;
				Timestamp answered_time = null;
				Object answer_questionId = null;
				int columns = rsmd.getColumnCount();
				while (rs.next()) {
					theme_id = rs.getObject(1);
					theme_ru = rs.getObject(2);
					description_ru = rs.getObject(3);
					theme_author = rs.getObject(4);
					theme_created_time = rs.getTimestamp(5);
					question_id = rs.getObject(6);
					question = rs.getObject(7);
					question_created_time = rs.getTimestamp(8);
					surname = rs.getObject(9);
					name = rs.getObject(10);
					email = rs.getObject(11);
					status_id = rs.getObject(12);

					HashMap row = new HashMap();
					for (int i = 1; i <= columns; i++) {
						row.put(rsmd.getColumnName(i), rs.getObject(i));
					}
					row.put("THEME_ID", theme_id);
					row.put("THEME_RU", theme_ru);
					row.put("DESCRIPTION_RU", description_ru);
					row.put("THEME_AUTHOR", theme_author);
					row.put("THEME_CREATED_TIME", theme_created_time);
					row.put("QUESTION_ID", question_id);
					row.put("QUESTION", question);
					row.put("QUESTION_CREATED_TIME", question_created_time);
					row.put("SURNAME", surname);
					row.put("NAME", name);
					row.put("EMAIL", email);
					row.put("STATUS_ID", status_id);
					list.add(row);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				//			request.setAttribute("error", ex.getCause());
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					rs = null;
				}
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					ps = null;
				}
			}
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				conn = null;
			}
		}		
		return list;
	}
}