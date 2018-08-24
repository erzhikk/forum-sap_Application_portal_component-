<%@ page import = "kz.ecc.controller.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.sql.Timestamp" %>
<%@ page import = "java.math.BigDecimal" %>
<%@ page import = "com.sapportals.portal.prt.resource.IResource" %>
<%@ page import = "java.util.ResourceBundle" %>


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
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/jquery-ui-1.9.2.custom.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/jquery.richtext.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap-collapse.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.min.js"));

%>
<h2>Ответ на вопрос</h2>

<%

	ArrayList result = (ArrayList) componentRequest.getNode().getValue("answerResult");
	Iterator it = result.iterator();
	
	BigDecimal ID = new BigDecimal(0);
	long time1 = 0;
	String question = "";
	String surname = "";
	String name ="";
	String email = "";
	Timestamp question_create_time = new Timestamp(time1);
	
	while(it.hasNext()){
		HashMap row = (HashMap) it.next();
		ID = (BigDecimal) row.get("ID");
%>
<table class = "table" border = "2" style = "table-layout: fixed; width: 80%;">
	<tr style="background-color: #6699FF;">
		<td><b><center>Вопрос</center></b></td>
		<td><b><center>Фамилия</center></b></td>
		<td><b><center>Имя</center></b></td>
		<td><b><center>email</center></b></td>
		<td><b><center>Время создания вопроса</center></b></td>
	</tr>
	<tr>
		<td><%= row.get("QUESTION") %></td>
		<td><%= row.get("SURNAME") %></td>
		<td><%= row.get("NAME") %></td>
		<td><%= row.get("EMAIL") %></td>
		<td><%= row.get("DATE_CREATE") %></td>
	</tr>
</table>
<br>
<%
	}
	String end_text = "%n С уважением, %n Парсегов Борис Анатольевич - председатель Общественного совета";
%>
<form id = "answer_form" action = "#" method = "POST">
	<div style ="border-style: solid; padding: 1px;">
	<textarea class = "form-control" id="comment" name="answer_text" required rows="5" col="8" style="resize: none; border-style: outset; padding: 3px;"></textarea>
	</div>
	<br>
	<input type = "hidden"  name = "send_answer" value = "<%= ID %>" />
	<button class="btn btn-info btn-lg" onclick = "alertFunction()" style = "width: 200px;" >Ответить</button>
	<div id="preview"></div><br>
<button type="button" class="btn btn-info btn-lg" id="myBtn" style = "width: 200px;">Просмотр</button>
  <!-- Modal -->
  <div class="modal fade" id="myModal" role="dialog">
    <div class="modal-dialog">
    
      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Предварительный просмотр</h4>
        </div>
        <div class="modal-body">
          
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>
      
    </div>
  </div>
  <div id = "aaa"></div>	
</form>

<form id = "answer_form" action = "#" method = "POST">
	<input class = "btn btn-danger" type="submit" value = "Отменить" name = "cancel_ask" style = "width: 200px;"/>
</form>


<script>
	function alertFunction() {
		alert("Ваш ответ отправлен!");
		document.getElementById("answer_form").submit();
	}
	
$(document).ready(function(){
    $("#myBtn").click(function(){
    	var text = document.getElementById('comment').value;
    	console.log(text);
    	
    	$("#myModal").modal().find(".modal-body").html(text + " <br>С уважением,<br>Парсегов Борис Анатольевич - председатель Общественного совета");
    });
});
		


	$('#comment').richText();

	
</script>