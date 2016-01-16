package org.w2fc.geoportal.utils;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w2fc.conf.Configuration;

public class SendEmail {

	final static Logger logger = LoggerFactory.getLogger(SendEmail.class);
	
    public static String send(String to, Address[] addresses, String text ) {

        // Assuming you are sending email from localhost
        // String host = "localhost";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", Configuration.getProperty("smpt.hostname"));
        String from = Configuration.getProperty("mail.messenger");

        // Get the default Session object.
        // Session session = Session.getDefaultInstance(properties);

        // create a session with an Authenticator
        Session session = Session.getInstance(properties, new Authenticator() {
            /*@Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Configuration.getProperty("smpt.username"), Configuration.getProperty("smpt.password"));
            }*/
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    to));
            
            message.setReplyTo(addresses);
            // Set Subject: header field
            message.setSubject("Сообщение с геопортала ЯО");

            // Now set the actual message
            message.setText(text);

            // Send message
            Transport.send(message);
            logger.debug("Message send: " + text);
            return "Success";
        } catch (MessagingException mex) {
        	logger.error(mex.getLocalizedMessage(), mex);
        	return "Error";
        }
    }
}
