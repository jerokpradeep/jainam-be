package in.codifi.odn.service;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import in.codifi.odn.config.ApplicationProperties;
import in.codifi.odn.config.EmailConfig;
import in.codifi.odn.config.EmailProperties;
import in.codifi.odn.service.spec.EmailServiceSpec;
import io.quarkus.logging.Log;

@ApplicationScoped
public class EmailService implements EmailServiceSpec {

//	@Inject
//	EmailUtils emailUtils;

	@Inject
	EmailConfig emailConfig;

	@Inject
	ApplicationProperties applicationProperties;

	@Inject
	EmailProperties emailProperties;

	@Override
	public void sendDefaultEmail(String subject,String body, String recepients) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();

				try {
					String userName = emailConfig.getFrom();
					String password = emailConfig.getSmtpPassword();
					String mailFrom = emailConfig.getFrom();
					Properties properties = getMailProperties(); new Properties();
					properties.put("mail.smtp.auth", "true");
					properties.put("mail.smtp.host", emailConfig.getSmtpHost());
					properties.put("mail.smtp.user", emailConfig.getSmtpUsername());
					properties.put("mail.smtp.port", emailConfig.getSmtpPort());
					properties.put("mail.smtp.socketFactory.port", emailConfig.getSmtpPort());
					properties.put("mail.smtp.debug", "true");
					properties.put("mail.smtp.starttls.enable", "true");
					properties.put("mail.smtp.starttls.enable", emailConfig.isSmtpAuth());
					properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
					Session session = getSessionpForProvider(properties, userName, password);
					try {
						String[] recipientArray = recepients.split(",\\s*");
						Address[] addresses = Arrays.stream(recipientArray).map(email -> {
							try {
								return new InternetAddress(email.trim());
							} catch (javax.mail.internet.AddressException e) {
								e.printStackTrace(); // Handle the exception as needed
								return null;
							}
						}).filter(address -> address != null).toArray(Address[]::new);

						builder.append(body);
						MimeMessage message = new MimeMessage(session);
						message.setFrom(new InternetAddress(mailFrom));
						message.setRecipients(Message.RecipientType.TO, addresses);
						message.setSubject("Sample Email Test");
						BodyPart messageBodyPart1 = new MimeBodyPart();
						messageBodyPart1.setContent(builder.toString(), "text/html");
						Multipart multipart = new MimeMultipart();
						multipart.addBodyPart(messageBodyPart1);
						message.setContent(multipart);
						Transport.send(message);
					} catch (MessagingException ex) {
						Log.error("MessagingException", ex);
					}
				} catch (Exception e) {
					Log.error("Exception", e);
				} finally {
					pool.shutdown();
				}
			}

		});

	}

	protected Session getSessionpForProvider(Properties properties, String userName, String Passs) {
		return Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, Passs);
			}
		});
	}

	@Override
	public void sendCustomEmailProvider(String provider) {
		// TODO Auto-generated method stub

	}
	
	protected Properties getMailProperties() {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", emailConfig.getSmtpHost());
		properties.put("mail.smtp.user", emailConfig.getSmtpUsername());
		properties.put("mail.smtp.port", emailConfig.getSmtpPort());
		properties.put("mail.smtp.socketFactory.port", emailConfig.getSmtpPort());
		properties.put("mail.smtp.debug", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.starttls.enable", emailConfig.isSmtpAuth());
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		return properties;
	}

	@Override
	public void sendExceptionEmailToSupport(String exceptionMessgae, String className, String reason) {
		String htmlBody = "<!DOCTYPE html><html><head><style>*{font-family:'Open Sans',"
				+ " Helvetica, Arial;color: #1e3465}table {margin-left:100px;font-family: arial, sans-serif;border-collapse:"
				+ " separate;}td, th {border: 1px solid #1e3465;text-align: left;padding: 8px;}"
				+ "th{background :#1e3465;color:white;}</style></head><body><div>"
				+ "<div  style='font-size:14px'><p>Hi Team,</p><p> There has been an exception - in  <div>"+ className +" and the reason is - "+ reason +"<p align='left'>"
				+ "<b>Regards," + "<br>" + emailProperties.getSignature() + "</b></p></div></div></body></html>";
		this.sendDefaultEmail(exceptionMessgae,htmlBody, applicationProperties.getSupportEmail());
	}

}
