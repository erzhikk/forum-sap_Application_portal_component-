<%@ page import = "kz.ecc.controller.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.sql.Timestamp" %>
<%@ page import = "java.math.BigDecimal" %>
<%@ page import = "java.text.SimpleDateFormat" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Iterator" %>
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

	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/jquery-3.3.1.min.js"));
	aResponse.addResource(componentRequest.getResource(IResource.SCRIPT, "scripts/bootstrap-collapse.js"));
%>


<%!
	private String getNLSString(IPortalComponentRequest request, String nlsKey){
		return MainpageController.getNLSString(request, nlsKey);
	}
	
%>
<%

	IPortalComponentRequest  currentRequest   =
	 (IPortalComponentRequest)pageContext.getAttribute(javax.servlet.jsp.PageContext.REQUEST);
	String language = MainpageController.getCurrentLanguage(currentRequest);
	

	ArrayList result = (ArrayList) componentRequest.getNode().getValue("themeResult");
	Iterator it = result.iterator();
	long time2 = 0;
	BigDecimal id = new BigDecimal(0);
	String theme = "";
	String  author = "";
	Timestamp time = new Timestamp(time2);
	String prevTheme = "";
	Timestamp prevTime = new Timestamp(time2);
	int i = 0;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");	
	
	
%>




<%
	while(it.hasNext()){
		HashMap row = (HashMap) it.next();

		
		id = (BigDecimal) row.get("ID");
		theme = (String) row.get("THEME_RU");
		time = (Timestamp) row.get("TIME_OF_CREATION");
		
		MainpageController mc = new MainpageController();
		String lastTime = mc.getLastAnswerDate();
		int messageCount = mc.getMessageCount();
		if(!theme.equals(prevTheme)&&!time.equals(prevTime)){
			i++;
			if(language.equalsIgnoreCase("ru")){
%>


			<br>
			<form action="#" method="POST" id = "myForm" >
			<table class="table table-bordered">
			<thead 
				class="bg-aqua" style = "background-color: #CCFFFF;">
				<th><center>
					<%= getNLSString(currentRequest,"THEME")%> 
				</th></center>
				<th><center>
					<%= getNLSString(currentRequest,"AUTHOR")%>
				</center></th>
				<th><center>
					<%= getNLSString(currentRequest,"MESSAGE_COUNT")%>
				</center></th>
				<th><center>
					<%= getNLSString(currentRequest,"DATE_CREATION")%>
				</center></th>
				<th><center>
					<%= getNLSString(currentRequest,"DATE_OF_LAST_MESSAGE")%>
				</center></th>
			</thead>

<%
			}else if(language.equalsIgnoreCase("kz")){
%>				
				<br>
			<form action="#" method="POST" id = "myForm" >
			<table class="table table-bordered">
			<thead 
				class="bg-aqua" style = "background-color: #CCFFFF;">
				<th><center>
					<%= getNLSString(currentRequest,"THEME")%> 
				</center></th>
				<th><center>
					<%= getNLSString(currentRequest,"AUTHOR")%>
				</center></th>
				<th><center>
					<%= getNLSString(currentRequest,"MESSAGE_COUNT")%>
				</center></th>
				<th><center>
					<%= getNLSString(currentRequest,"DATE_CREATION")%>
				</center></th>
				<th><center>
					<%= getNLSString(currentRequest,"DATE_OF_LAST_MESSAGE")%>
				</center></th>
			</thead>
<%				

			}
			
%>

		<tr>
			<td><a href="#" onclick="myFunction(this);" id = "Ask" name = "ask_question2" ><%= row.get("THEME_RU") %></a></td>
			<td><%= row.get("AUTHOR_NAME") %></td>
			<td><center><%= messageCount %></center></td>
			<td><%= dateFormat.format(new Date(((Timestamp)row.get("TIME_OF_CREATION")).getTime())) %></td>
			<td><%= lastTime %></td>
			<input type = "hidden" name ="ask_question<%= row.get("ID") %>" />
		</tr>

<%

			
		}
			prevTheme = theme;
			prevTime = time;		

	}
	
		
%>
</table>
</form>
<script>
function myFunction(Element) {
	document.getElementById("myForm").submit();
}
</script>