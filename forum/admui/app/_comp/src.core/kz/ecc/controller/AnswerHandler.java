/*
 * Created on 22.05.2018
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package kz.ecc.controller;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.logging.Logger;
import com.sapportals.portal.prt.component.IPortalComponentRequest;

/**
 * @author mesh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AnswerHandler {
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
	
	public static final String ANSWER = "answer_text";
	
	private String answerDB;;
	
	public AnswerHandler(IPortalComponentRequest request){
		this.request = request;
		extractFieldsData(request);
	}
	
	private void extractFieldsData(IPortalComponentRequest request) {
		this.request=request;

			answerDB =
				request.getParameter(ANSWER) == null
					? null
					: request.getParameter(ANSWER).trim();
			
			
		String questionId = request.getParameter("send_answer");		
			System.err.println("ANSWER_HANDLER --- " + answerDB);
			Logger.getLogger("moderator_project").info("QUESTION ANSWER = " + answerDB);
			Logger.getLogger("moderator_project").info("ID ID ID ID ===== " + questionId);
			insertIntoDB(answerDB);
		}

	/**
	 * @param answerDB
	 */
	private void insertIntoDB(String answerDB) {
		// TODO Auto-generated method stub
		
		String questionId = request.getParameter("send_answer");
		String author = "Председатель Общественного совета";
		
					Connection conn = null;
					try {
						PreparedStatement st = null;
						try {
							String sql = "insert into ECC_FORUM_ANSWER (ANSWER, AUTHOR, QUESTION_ID) values (?, ?, ?)";
							conn = getConnection();
							st = conn.prepareStatement(sql);
							st.setString(1, answerDB);
							st.setString(2, author);
							st.setString(3, questionId);
							st.executeUpdate();
							
							
							String sql2 = "update ECC_FORUM_QUESTION set STATUS_ID = 2 where ID = ?";
							st = conn.prepareStatement(sql2);
							st.setString(1, questionId);
							st.executeUpdate();
					
						} catch (Exception ex) {
							ex.printStackTrace();
							//			request.setAttribute("error", ex.getCause());
						} finally {
							if (st != null) {
								try {
									st.close();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								st = null;
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
