<%@ page import = "kz.ecc.controller.*" %>
<%@ page import = "java.util.*" %>
<%@ page import = "java.sql.Timestamp" %>
<%@ page import = "java.math.BigDecimal" %>
<%@ page import = "com.sapportals.portal.prt.resource.IResource" %>

<h2>Переслать вопрос по email</h2>

<%
ArrayList result = (ArrayList) componentRequest.getNode().getValue("sendEmail");
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
<table border="1" style = "table-layout: fixed; width: 80%;">
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
</table><br>


<form id = "email_text" action = "#" method = "POST">
										<p>Выбрать получателя:
										<select name="recipient" >
											<option value=''> </option>
											<option value="sagadiev@uib.kz" >
												Сагадиев Кенжегали Абенович	sagadiev@uib.kz
											</option>
											<option value="komek_astana@mail.ru"  >
												Бердимбетов Тахир Закирович	komek_astana@mail.ru
											</option>	
											<option value="abildina@tsb.kz"  >
												Жаксыбек Даурен Адильбекулы	abildina@tsb.kz
											</option>
											<option value="D.utegenova@tsb.kz"  >
												Жаксыбек Даурен Адильбекулы	D.utegenova@tsb.kz 
											</option>
											<option value="leila_as@mail.ru"  >
												Асылбекова Лейла Умурзаковна	leila_as@mail.ru 
											</option>	
											<option value="yuliya.stepantsova@gmail.com"  >
												Степанцова Юлия Юрьевна	yuliya.stepantsova@gmail.com
											</option>
											<option value="kaskeyev@mail.ru"  >
												Каскеев Чингиз Кавкенович	kaskeyev@mail.ru 
											</option>
											<option value="baizakov37@mail.ru"  >
												Байзаков Сайлау Байзакович	baizakov37@mail.ru
											</option>	
											<option value="nurumov-aldash@mail.ru"  >
												Нурумов Алданыш Арыстангалиевич	nurumov-aldash@mail.ru
											</option>
											<option value="R.Zhursunov@palata.kz"  >
												Журсунов Рустам Манарбекович	R.Zhursunov@palata.kz 
											</option>
											<option value="n.akshabayeva@atameken.kz"  >
												Журсунов Рустам Манарбекович	n.akshabayeva@atameken.kz
											</option>	
											<option value="n.zhangоzhina@atameken.kz"  >
												Журсунов Рустам Манарбекович	n.zhangоzhina@atameken.kz 
											</option>
											<option value="s.koshkimbaev@bdokz.com"  >
												Кошкимбаев Сапар Хайсаханович	s.koshkimbaev@bdokz.com
											</option>
											<option value="mariyash_2004@mail.ru"  >
												Кабикенова Марияш Майрамбаевна	mariyash_2004@mail.ru
											</option>
											<option value="souz_astana@mail.ru"  >
												Жуматов Мереке Кайруллаевич	souz_astana@mail.ru
											</option>
											<option value="mereke_jum@mail.ru"  >
												Жуматов Мереке Кайруллаевич	mereke_jum@mail.ru
											</option>	
											<option value="zhomart@almex.kz"  >
												Нурабаев Жомарт Досанкулович	zhomart@almex.kz
											</option>
											<option value="pnk@pnk.kz"  >
												Нурабаев Жомарт Досанкулович	pnk@pnk.kz
											</option>	
											<option value="erzhikk@mail.ru	ye.meshitbayev@ecc.kz"  >
												Нурабаев Жомарт Досанкулович	pnk@pnk.kz
											</option>																																																																																																																																																						
										</select></p>
	<textarea form = "email_text" rows="10" cols="95" name ="email_text" style = "resize: none;">
	Вопрос: <%= row.get("QUESTION") %>
	Отправитель: <%= row.get("SURNAME") %> <%= row.get("NAME") %>
	email отправителя: <%= row.get("EMAIL") %>
	Время вопроса: <%= row.get("DATE_CREATE") %>
	
	_______________________________________________
	
	Ваш ответ: 
	
	</textarea>
	<br>
	<input type="submit" name = "send_email"/>
	<input type = "reset" onclick = "searchReset();" value = "Отмена"/>
</form>
<%
	}
%>
<script>
	function searchReset(){
		window.history.back();
	}
</script>