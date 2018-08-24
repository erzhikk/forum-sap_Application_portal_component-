<%@ page import = "kz.ecc.controller.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.sql.Timestamp" %>
<%@ page import = "java.math.BigDecimal" %>
<%@ page import = "java.text.SimpleDateFormat" %>
<%@ page import = "java.util.ResourceBundle" %>
<%@ page import = "com.sapportals.portal.prt.resource.IResource" %>

	
<%	
	String mimeUrl = componentRequest.getWebResourcePath();
	QuestionHandler handler=(QuestionHandler)componentRequest.getNode().getValue("formHandlerInstance");
	
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

	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/jquery-3.3.1.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap-collapse.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/jquery.richtext.min.js"));
%>

<%!
	private String getNLSString(IPortalComponentRequest request, String nlsKey){
		return MainpageController.getNLSString(request, nlsKey);
	}
	
%>
<style>
textarea {
	resize: none;
	width: 500px;
	height: 100px;
}

input {
	margin-top: 5px;
}
</style>

<br>
<%

	IPortalComponentRequest  currentRequest   =
	 (IPortalComponentRequest)pageContext.getAttribute(javax.servlet.jsp.PageContext.REQUEST);
	String language = MainpageController.getCurrentLanguage(currentRequest);


	ArrayList result = (ArrayList) componentRequest.getNode().getValue("questionResult");
	Iterator it = result.iterator();
	long time1 = 0;
	BigDecimal theme_id = new BigDecimal(0);
	BigDecimal prev_theme_id = new BigDecimal(0);
	BigDecimal question_id = new BigDecimal(0);
	BigDecimal prev_question_id = new BigDecimal(0);
	String theme = "";
	String  author = "";
	Timestamp theme_time = new Timestamp(time1);
	String description = "";
	String surname ="";
	String name = "";
	long time2 = 0;
	Timestamp question_time = new Timestamp(time2);
	String question = "";
	int i = 0;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
	
%>


<%
	
while(it.hasNext()){
		HashMap row = (HashMap) it.next();
// RU VERSION
		if(language.equalsIgnoreCase("ru")){
		theme_id = (BigDecimal) row.get("THEME_ID");
		MainpageController mc = new MainpageController();
		String lastTime = mc.getLastAnswerDate();
		
		if(!theme_id.equals(prev_theme_id)) {
			i++;
		%>
		<table class="table table-bordered" border = "1"  width = "500px">	
			<tr>
				<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"THEME")%></td>
				<td width = "350px"><%= row.get("THEME_RU") %></td>
			</tr>
			<tr>
				<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"AUTHOR")%></td>
				<td width = "350px"><%= row.get("AUTHOR_NAME") %></td>
			</tr>
			<tr>
				<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"THEME_DESCRIPTION")%></td>
				<td width = "350px"><%= row.get("DESCRIPTION_RU") %></td>
			</tr>
			<tr>
				<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"DATE_CREATION")%></td>
				<td width = "350px"><%= dateFormat.format(new Date(((Timestamp)row.get("TIME_OF_CREATION")).getTime())) %></td>
			</tr>
			<tr>
				<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"DATE_OF_LAST_MESSAGE")%></td>
				<td width = "350px"><%= lastTime %></td>
			</tr>
		</table><br>	

		<form id = "ask_form_ru" action = "#" method = "POST">
			<h3><%= getNLSString(currentRequest,"ASK_QUESTION")%></h3>
			<input id="email" type = "email" name = "sender_email" required placeholder = "email" width = "300px"/><font color="red" size="2"></font> email <br>
			<input id="surname" type = "text" name = "sender_surname" required placeholder = "Фамилия" width = "300px"/><font color="red" size="2"></font> Фамилия <br>
			<input id="name" type = "text" name = "sender_name" required placeholder = "Имя" width = "300px"/><font color="red" size="2"></font> Имя <br>
			<br>
			<div style="border-style: groove; padding: 1px">
				<textarea class = "answer_message" name="question" required></textarea>
			</div>
			<br>
			<input type="submit" id = "sub_btn" class = "btn btn-primary"  value = "Отправить" name = "send_question" />
			<input type="reset" class = "btn btn-danger" value = "Отменить" name = "cancel_ask"/>
		</form>
		
		<%
		}
		prev_theme_id = theme_id;	
		
		question_id = (BigDecimal) row.get("QUESTION_ID");
		%> 
		<table width = "100%">
		<%
		if(!question_id.equals(prev_question_id)){
			i++;
			String displayNone = "display: none";
		%>
		<table class="table table-condensed">
			<tr border="1" width = "100%" style="background-color: #CCFFFF; ">
				<td width = "70%">Вопрос от <b><i><%= row.get("SURNAME") %> <%= row.get("NAME") %></i></b></td><td width = "30%"><%= dateFormat.format(new Date(((Timestamp)row.get("DATE_CREATE")).getTime())) %></td>  
			</tr><br>
			<tr width = "100%">
				<td><%= row.get("QUESTION") %></td><td><button class="btn btn-info" data-toggle="collapse" data-target="#answer<%= row.get("QUESTION_ID") %>">Показать ответ</button></td>	
			</tr>
		</table>
		<table id="answer<%= row.get("QUESTION_ID") %>" class = "table table-condensed collapse" >
			<tr width = "100%" style="background-color: #CCFFCC; ">
				<td width = "70%">Ответ от <b><i><%= row.get("ANSWER_AUTHOR") %></i></b></td><td width = "30%"><%= dateFormat.format(new Date(((Timestamp)row.get("ANSWERED_TIME")).getTime())) %></td>
			</tr>
			<tr width="100%">
				<td><%= row.get("ANSWER") %><br>С уважением,<br>Парсегов Борис Анатольевич - председатель Общественного совета</td>
			</tr>
		</table>
		<%			
		}
		%>
		</table><hr>
		<%
		}
// KZ VERSION
		if(language.equalsIgnoreCase("kz")){
				theme_id = (BigDecimal) row.get("THEME_ID");
				MainpageController mc = new MainpageController();
				String lastTime = mc.getLastAnswerDate();
		
				if(!theme_id.equals(prev_theme_id)) {
					i++;
				%>
				<table class="table table-bordered" border = "1"  width = "500px">	
					<tr>
						<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"THEME")%></td>
						<td width = "350px"><%= row.get("THEME_KZ") %></td>
					</tr>
					<tr>
						<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"AUTHOR")%></td>
						<td width = "350px"><%= row.get("AUTHOR_NAME") %></td>
					</tr>
					<tr>
						<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"THEME_DESCRIPTION")%></td>
						<td width = "350px"><%= row.get("DESCRIPTION_KZ") %></td>
					</tr>
					<tr>
						<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"DATE_CREATION")%></td>
						<td width = "350px"><%= dateFormat.format(new Date(((Timestamp)row.get("TIME_OF_CREATION")).getTime())) %></td>
					</tr>
					<tr>
						<td style="font-weight:bold" width = "150px"><%= getNLSString(currentRequest,"DATE_OF_LAST_MESSAGE")%></td>
						<td width = "350px"><%= lastTime %></td>
					</tr>
				</table><br>	

				<form id = "ask_form_kz" action = "#" method = "POST">
					<h3><%= getNLSString(currentRequest,"ASK_QUESTION")%></h3>
					<input id="email" type = "email" name = "sender_email" required placeholder = "email" width = "300px"/><font color="red" size="2"></font> email <br>
					<input id="surname" type = "text" name = "sender_surname" required placeholder = "Фамилия" width = "300px"/><font color="red" size="2"></font> Тегі <br>
					<input id="name" type = "text" name = "sender_name" required placeholder = "Имя" width = "300px"/><font color="red" size="2"></font> Аты <br>
					<br>
					<div style="border-style: groove; padding: 1px">
						<textarea class = "answer_message" name="question" required></textarea>
					</div>
					<br>
					<input type="submit" id = "sub_btn" class = "btn btn-primary"  value = "Жеберу" name = "send_question" />
					<input type="reset" class = "btn btn-danger" value = "Болдырмау" name = "cancel_ask"/>
				</form>
		
				<%

				}
				prev_theme_id = theme_id;	
		
				question_id = (BigDecimal) row.get("QUESTION_ID");
				%> 
				
				<%
				if(!question_id.equals(prev_question_id)){
					i++;
					
					String displayNone = "display: none";
					String displayNull = "";
		
				%>
				<table class = "table table-condensed">
					<tr border="1" width = "100%" style="background-color: #CCFFFF; ">
						<td width = "70%">Сұрақ --- <b><i><%= row.get("SURNAME") %> <%= row.get("NAME") %></i></b></td><td width = "30%"><%= dateFormat.format(new Date(((Timestamp)row.get("DATE_CREATE")).getTime())) %></td>  
					</tr><br>
					<tr width = "100%">
						<td><%= row.get("QUESTION") %></td><td><button class="btn btn-info" data-toggle="collapse" data-target="#answer<%= row.get("QUESTION_ID") %>">Жауабын көру</button></td>	
					</tr>
				</table>	
				<table id="answer<%= row.get("QUESTION_ID") %>" class = "table table-condensed collapse" >				
					<tr width = "100%" style="background-color: #CCFFCC;  ">
						<td width = "70%">Жауап --- <b><i><%= row.get("ANSWER_AUTHOR") %></i></b></td><td width = "30%"><%= dateFormat.format(new Date(((Timestamp)row.get("ANSWERED_TIME")).getTime())) %></td>
					</tr>
					<tr width="100%" >
						<td><%= row.get("ANSWER") %><br>С уважением,<br>Парсегов Борис Анатольевич - председатель Общественного совета</td>
					</tr>
				</table>
					
				<%			
				}
				%>
				
				<%
				}

	}
	
	
%>

				<form id = "ask_form_kz" action = "#" method = "POST">
					<h3><%= getNLSString(currentRequest,"ASK_QUESTION")%></h3>
					<input id="email" type = "email" name = "sender_email" required placeholder = "email" width = "300px"/><font color="red" size="2"></font> email <br>
					<input id="surname" type = "text" name = "sender_surname" required placeholder = "Фамилия" width = "300px"/><font color="red" size="2"></font> Тегі <br>
					<input id="name" type = "text" name = "sender_name" required placeholder = "Имя" width = "300px"/><font color="red" size="2"></font> Аты <br>
					<br>
					<div style="border-style: groove; padding: 1px">
						<textarea class = "answer_message" name="question" required></textarea>
					</div>
					<br>
					<input type="submit" id = "sub_btn" class = "btn btn-primary" value = "Жеберу" name = "send_question" />
					<input type="reset" class = "btn btn-danger" value = "Болдырмау" name = "cancel_ask"/>
				</form>

<script>
	function alertFunctionRU() {
		var email = document.getElementById('email').value;
		var name = document.getElementById('name').value;
		var surname = document.getElementById('surname').value;
		if(email == null || email == ""){
			alert('Напишите ваш email');
		}
		 if(name == null || name == ""){
			alert('Напишите ваше имя');
		}
		if(surname == null || surname == ""){
			alert('Напишите вашу фамилию');
		}
		
		if(email.length >0 && surname.length > 0 && name.length > 0) {
			console.log("is going to be submitted");
			document.getElementById('ask_form_ru').submit();
		}
	}
	
	function alertFunctionKZ() {
		var email = document.getElementById('email').value;
		var name = document.getElementById('name').value;
		var surname = document.getElementById('surname').value;
		if(email == null || email == ""){
			alert('Напишите ваш email');
		}
		 if(name == null || name == ""){
			alert('Напишите ваше имя');
		}
		if(surname == null || surname == ""){
			alert('Напишите вашу фамилию');
		}
		
		if(email.length >0 && surname.length > 0 && name.length > 0) {
			console.log("is going to be submitted");
			document.getElementById('ask_form_kz').submit();
		}
	}
	
	function alertFunc(el){
		var link = el.id;
		alert("hello world!" + link);
	}
	
	$('.answer_message').richText();
</script>