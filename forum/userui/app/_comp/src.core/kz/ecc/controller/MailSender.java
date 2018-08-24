/*
 * Created on 21.05.2018
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package kz.ecc.controller;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sapportals.portal.prt.component.IPortalComponentContext;
import com.sapportals.portal.prt.component.IPortalComponentProfile;
import com.sapportals.portal.prt.component.IPortalComponentRequest;

/**
 * @author mesh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MailSender {
	public static final String ENCODING="UTF-8";
	public static final String PROPERTY_SMTP_PORT="SMTP_PORT"; 
	public static final String PROPERTY_SMTP_HOST="SMTP_HOST";
	public static final String PROPERTY_TO_ADDRESS="EMAIL_RECIPIENT";
	public static final String PROPERTY_FROM_ADDRESS="EMAIL_SENDER";
	
	private String smtpPort;
	private String smtpHost;
	private String toAddress;
	private String fromAddress;
	private String username;
	private String password;
	
	public MailSender(IPortalComponentRequest request) {
		super();
		//Извлекаю настройки из компонента
		IPortalComponentContext context = request.getComponentContext();
		IPortalComponentProfile profile = context.getProfile();
		
		smtpHost = profile.getProperty(PROPERTY_SMTP_HOST)==null ? "" : profile.getProperty(PROPERTY_SMTP_HOST);
		smtpPort = profile.getProperty(PROPERTY_SMTP_PORT)==null ? "" : profile.getProperty(PROPERTY_SMTP_PORT);
		toAddress = profile.getProperty(PROPERTY_TO_ADDRESS)==null ? "" : profile.getProperty(PROPERTY_TO_ADDRESS);
		fromAddress = profile.getProperty(PROPERTY_FROM_ADDRESS)==null ? "" : profile.getProperty(PROPERTY_FROM_ADDRESS);
		username=fromAddress;
		password="";
		
		if (smtpHost.length()<=0 || smtpPort.length()<=0 ||toAddress.length()<=0 || fromAddress.length()<=0)
			new Exception("MailSender: Не установлены конфигурационные данные");			
	}
	
	public void sendMail(String content) throws AddressException, MessagingException {		
		Properties props = System.getProperties();
		props.put("mail.smtp.port", this.smtpPort);
		props.put("mail.smtp.host", this.smtpHost);
		//props.put("mail.smtp.auth", "true");
		props.put("mail.mime.charset", ENCODING);
		//props.put("mail.smtp.starttls.enable", "true");	
		Session session = Session.getInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
					}
				});
			
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(this.fromAddress));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(this.toAddress));
		msg.setSubject("Заявка на прием к руководству МФ РК");
		msg.setText(content);
		Transport.send(msg);
	}
}
