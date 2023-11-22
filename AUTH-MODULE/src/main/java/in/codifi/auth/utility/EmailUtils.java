package in.codifi.auth.utility;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import in.codifi.auth.config.EmailProperties;
import io.quarkus.logging.Log;

@ApplicationScoped
public class EmailUtils {

	@Inject
	EmailProperties props;

	/**
	 * Method to send Email via ZOHO
	 * 
	 * @author Gowthaman
	 * @param htmlBody
	 * @param subject
	 * @param recpient
	 */
	public void sendEmailWithZoho(String htmlBody, String subject, String recpient) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				System.out.println("Sending mail by Zoho " + recpient);
				System.out.println(props.getJainamHost());
				System.out.println(props.getJainamUserName());
				System.out.println(props.getJainamPort());
				System.out.println(props.getJainamPW());
				System.out.println(props.getJainamFrom());
				try {
					// Get system properties
					Properties properties = new Properties();
					// Setup mail server
					properties.put("mail.smtp.host", props.getJainamHost());
					properties.put("mail.smtp.user", props.getJainamUserName());
					properties.put("mail.smtp.port", props.getJainamPort());
					properties.put("mail.smtp.auth", "true");
					properties.put("mail.smtp.starttls.enable", "false");
//					properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
//					properties.put("mail.smtp.starttls.enable", "true");
					Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(props.getJainamUserName(), props.getJainamPW());
						}
					});
					try {
						builder.append(htmlBody);
						MimeMessage message = new MimeMessage(session);
						message.setFrom(new InternetAddress(props.getJainamFrom()));
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(recpient));
						message.setSubject(subject);
						BodyPart messageBodyPart1 = new MimeBodyPart();
						messageBodyPart1.setContent(builder.toString(), "text/html");
						Multipart multipart = new MimeMultipart();
						multipart.addBodyPart(messageBodyPart1);
						message.setContent(multipart);
						// 7) send message
						Transport.send(message);
					} catch (MessagingException ex) {
						ex.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pool.shutdown();
				}
			}
		});
	}

	/**
	 * Method to send email for payment failure
	 * 
	 * @author Gowthaman
	 * @param message
	 */
	public void sendEmailOtp(String otp, String emailId) {

		try {

			String subject = "";
			String htmlBody = "<!DOCTYPE html><html><head><style>*{font-family:'Open Sans',"
					+ " Helvetica, Arial;color: #1e3465}table {margin-left:100px;font-family: arial, sans-serif;border-collapse:"
					+ " separate;}td, th {border: 1px solid #1e3465;text-align: left;padding: 8px;}"
					+ "th{background :#1e3465;color:white;}</style></head><body><div>"
					+ "<div  style='font-size:14px'><p>Dear User,</p><p>" + otp
					+ " is your one time password for logging into the system, this code is valid for next 30 seconds.</p></div>"
					+ "<div><p align='left'>" + "<b>Regards," + "<br>Jainam Broking Limited</b></p></div></div></body></html>";

			subject = AppConstants.EMAIL_OTP_SUBJECT;

			sendEmailWithZoho(htmlBody, subject, emailId);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
	}

}
