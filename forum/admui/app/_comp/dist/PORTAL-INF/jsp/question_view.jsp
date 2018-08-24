<%@ page import = "kz.ecc.controller.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.sql.Timestamp" %>
<%@ page import = "java.math.BigDecimal" %>

<h1>QUESTION VIEW</h1>
<br>
<form onload = "loadSubmit()"  action="#" method = "POST">
<table id = "questionTable" name "question_view" border = "1">
	<tr>
		<td><b>QUESTION</b></td>
		<td><b>DATE</b></td>
		<td><b>SURNAME</b></td>
		<td><b>NAME</b></td>
		<td><b>EMAIL</b></td>
	</tr>
	
<%

	ArrayList result2 = (ArrayList) componentRequest.getNode().getValue("questionResult");
	Iterator it2 = result2.iterator();
	long time2 = 0;
	BigDecimal question_id = new BigDecimal(0);
	BigDecimal prev_question_id = new BigDecimal(0);
	String question = "";
	String surname = "";
	String name ="";
	String email = "";
	Timestamp question_asked_time = new Timestamp(time2);	

	int j = 0;
	
	while(it2.hasNext()){
		HashMap row = (HashMap) it2.next();
		
		question_id = (BigDecimal) row.get("ID");


		if(!question_id.equals(prev_question_id)){
			j++;
%>

	<tr>
		<td><%= row.get("QUESTION") %></td>
		<td><%= row.get("DATE_CREATE") %></td>
		<td><%= row.get("SURNAME") %></td>
		<td><%= row.get("NAME") %></td>
		<td><%= row.get("EMAIL") %></td>
	</tr>

<%
		}
		prev_question_id = question_id;
	}
%>
</table>
</form>

<script>
function loadSubmit() {
	document.getElementById("questionTable").submit();
}
</script>