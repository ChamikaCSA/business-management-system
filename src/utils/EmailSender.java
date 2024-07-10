package utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void sendEmail(String to, String subject, String body) {
        final String username = "yourEmail@example.com"; // Your email
        final String password = "yourPassword"; // Your email password

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.example.com"); // SMTP Host
        prop.put("mail.smtp.port", "587"); // TLS Port
        prop.put("mail.smtp.auth", "true"); // Enable authentication
        prop.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("yourEmail@example.com")); // From email
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}