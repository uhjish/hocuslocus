package org.omelogic.utils;


/**
 * @author Chris Zaleski
 * @version 1.0
 *
 */

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Emailer
{
	//private String SMTP_HOST_NAME = null;
	private String SMTP_AUTH_USER = null;
	private String SMTP_AUTH_PWD  = null;
    
	//----------------------------------------------------------
	
	public Emailer()
	{}
	
	//----------------------------------------------------------
	
	public void sendSimpleEmail(String SMTPHost, String subject, String body, String toAddress, String fromAddress)
	throws MessagingException
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTPHost);
		
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		
		message.setSubject(subject);
		message.setText(body);
		
		message.setFrom(new InternetAddress(fromAddress));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
		
		Transport.send(message);
	}
	
	//----------------------------------------------------------
	
	public void sendSimpleEmailAuthenticated(String SMTPHost, String userName, String password, String subject, String body, String toAddress, String fromAddress)
	throws MessagingException
	{
		SMTP_AUTH_USER = userName;
		SMTP_AUTH_PWD = password;
		
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTPHost);
		props.put("mail.smtp.auth", "true");

		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getDefaultInstance(props, auth);

		MimeMessage message = new MimeMessage(session);
		
		message.setSubject(subject);
		message.setContent(body, "text/plain");
		
		message.setFrom(new InternetAddress(fromAddress));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));

		Transport.send(message);
	}
	
	//###########################################################
	
    private class SMTPAuthenticator extends Authenticator
    {
        public PasswordAuthentication getPasswordAuthentication()
        {
           return new PasswordAuthentication(SMTP_AUTH_USER, SMTP_AUTH_PWD);
        }
    }
}
