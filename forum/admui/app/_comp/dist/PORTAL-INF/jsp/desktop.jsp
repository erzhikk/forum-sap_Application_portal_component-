<%@ page import = "kz.ecc.controller.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.sql.Timestamp" %>
<%@ page import = "java.math.BigDecimal" %>
<%@ page import = "com.sapportals.portal.prt.resource.IResource" %>
<%@ page import = "java.text.SimpleDateFormat" %>
<%@ page import = "java.util.ResourceBundle" %>
<%@ page import = "com.sapportals.portal.prt.resource.IResource" %>


<%
		
	String mimeUrl = componentRequest.getWebResourcePath();	
	ResourceBundle bundle = componentRequest.getResourceBundle();
			
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap-theme.css"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap-theme.css.map"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap-theme.min.css"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap-theme.min.css.map"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap.css"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap.css.map"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap.min.css"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/bootstrap.min.css.map"));
	aResponse.addResource(componentRequest.getResource(IResource.CSS, "css/richtext.min.css"));

	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/jquery-3.3.1.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap-collapse.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/jquery.richtext.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.min.js"));

%>



<h1>Рабочее место модератора</h1>
<table class = "table" style = "table-layout: fixed; width: 80%;">
	<tr>
		<td>
		<h2>Поиск по фамилии задавшего вопрос</h2>
			<form action = "#" method = "POST">
				<input type="text" name = "search" /><br>
				<input class = "btn btn-primary" type="submit" value = "Поиск" />
				<input class = "btn btn-danger" type="reset" onclick = "searchReset();" value = "Отмена" />
			</form>
		</td>
		<td>
			<h2>Поиск по дате</h2>
			<form action = "#" method="POST">
				с  <input type="date" name = "search_by_date_begin" />

				до <input type="date" name = "search_by_date_end" /><br> 
				<input class = "btn btn-primary" type = "submit"/>
				<input class = "btn btn-danger" type = "reset" onclick = "searchReset();"/>
			</form>
		</td>
	</tr>
</table><br>
<table class = "table" border = "1" style = "table-layout: fixed; width: 80%;">
	<tr style="background-color: #6699FF;">
		<td><b>Название темы</b></td>
		<td><b>Суть вопроса</b></td>
		<td><b>Дата создания</b></td>
		<td><b>Автор</b></td>
	</tr>
	
<%


	ArrayList result = (ArrayList) componentRequest.getNode().getValue("searchResult");
	Iterator it = result.iterator();
	
	long time1 = 0;
	BigDecimal theme_id = new BigDecimal(0);
	BigDecimal prev_theme_id = new BigDecimal(0);
	String theme = "";
	String author_name = "";
	String description ="";
	Timestamp theme_created_time = new Timestamp(time1);
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");		

	int i = 0;
	
	while(it.hasNext()){
		HashMap row = (HashMap) it.next();
		
		theme_id = (BigDecimal) row.get("THEME_ID");


		if(!theme_id.equals(prev_theme_id)){
			i++;
%>

	<tr>
		<td><%= row.get("THEME_RU") %></td>
		<td><%= row.get("DESCRIPTION_RU") %></td>
		<td><%= dateFormat.format(new Date(((Timestamp)row.get("THEME_CREATED_TIME")).getTime())) %></td>
		<td><%= row.get("THEME_AUTHOR") %></td>
	</tr>
</table> <br>

<br>
<h2>Вопросы по теме</h2><br>
<table class "table"  style = "table-layout: fixed; width: 80%">
	<tr style="background-color: #6699FF;">
		<td><b>Вопрос</b></td>
		<td style = "width: 10%"><b>Время</b></td>
		<td style = "width: 10%"><b>Фамилиля Имя</b></td>
		<td style = "width: 20%"><b>Email</b></td>
		<td style = "width: 13%"><b>Статус</b></td>
		<td style = "width: 15%"></td>
	</tr>
</table>
<%
		}
		prev_theme_id = theme_id;
	
%>



	
<%



	long time2 = 0;
	BigDecimal question_id = new BigDecimal(0);
	BigDecimal prev_question_id = new BigDecimal(0);
	String question = "";
	String surname = "";
	String name ="";
	String email = "";
	BigDecimal status_id = new BigDecimal(0);
	Timestamp question_asked_time = new Timestamp(time2);	
	String style_color = "";
	String status = "";
	String hideOrShow = "";

	
%>
<table class = "table"  style = "table-layout: fixed; width: 80%">


<%

	question_id = (BigDecimal) row.get("QUESTION_ID");
	status_id = (BigDecimal) row.get("STATUS_ID");
	
		if(!question_id.equals(prev_question_id)){
			i++;

			if(status_id.equals(new BigDecimal(1))){
				style_color = "#FFFFFF";
				status = "Не просмотрен";
				hideOrShow = "";
			}else if(status_id.equals(new BigDecimal(2))) {
				style_color = "#99FF99";
				status = "Подтвержден";
				hideOrShow = "display: none;";
			}else if(status_id.equals(new BigDecimal(3))){
				style_color="#FF3300";
				status = "Отклонен";
				hideOrShow = "display: none;";
			}else{
				style_color="#FFFFFF";
				status = "Ошибка";
			}

%>

	<tr style="background-color: <%= style_color %>;">
		<td><%= row.get("QUESTION") %></td>
		<td style = "width: 10%"><%= dateFormat.format(new Date(((Timestamp)row.get("QUESTION_CREATED_TIME")).getTime())) %></td>
		<td style = "width: 10%"><%= row.get("SURNAME") %> <%= row.get("NAME") %></td>
		<td style = "width: 20%"><%= row.get("EMAIL") %></td>
		<td style = "width: 13%"><%= status %></td>
		<td style = "width: 15%;">		
<!--	<form action = "#" method = "POST" >
				<input type="hidden" name = "status" value="approve_<%= row.get("QUESTION_ID") %>" />
				<center><input type="submit" name = "questionId" value="Подтвердить" style = "width: 150px;"/></center>
		</form>
-->	
<!--			<form action = "#" method = "POST" >
				<input type="hidden" name = "status" value="reject_<%= row.get("QUESTION_ID") %>" />
				<center><input type="submit" name = "questionId" value="Отклонить" style = "width: 100%; <%= hideOrShow %>"/><center>
			</form>
-->
			<form action="#" method = "POST" id="form_<%= row.get("QUESTION_ID") %>" >
				<input type = "hidden" name = "answer" value="<%= row.get("QUESTION_ID") %>" />
				<input class = "btn btn-success" type = "submit" name="answer"  value = "Ответить" style = "width: 100%; <%= hideOrShow %>"/>
			</form>
			<form action="#" method = "POST" id="form_<%= row.get("QUESTION_ID") %>" >
				<input type = "hidden" name = "email_question_id" value="<%= row.get("QUESTION_ID") %>" />
				<input class = "btn btn-primary" type = "submit" name="send_email_state"  value = "Переслать" style = "width: 100%; "/>
			</form>
		</td>
	</tr>

	
<%
		}
		prev_question_id = question_id;
	}
%>


</table>

<script>
	function answerFunction(el){
		var link = el.id;
		var linkParent = document.getElementById(link).parentElement.attributes['id'].value;
		document.getElementById(linkParent).submit();
	}
	
	function hideFunction(el){
		var link = el.id;
		document.getElementById(link).hide();
	}
	
	function searchReset(){
		window.history.back();
	}
</script>