package in.codifi.odn.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

import in.codifi.odn.config.EmailConfig;
import in.codifi.odn.config.EmailProperties;
import in.codifi.odn.config.HazelcastConfig;
import io.quarkus.logging.Log;

@ApplicationScoped
public class EmailUtils {

	@Inject
	EmailProperties props;
	
	@Inject
	EmailConfig emailConfig;


	public void sendEmail(String htmlBody, String subject, String recpient,String provider) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				try {
					String userName = getUserName(provider);
					String password = getPassword(provider);
					String mailFrom = getMailFrom(provider);
					Session session = getSessionpForProvider(getEmailProps(provider),
							userName,
							password);
					try {
					        String[] recipientArray = recpient.split(",\\s*");
					        Address[] addresses = Arrays.stream(recipientArray)
					                .map(email -> {
					                    try {
					                        return new InternetAddress(email.trim());
					                    } catch (javax.mail.internet.AddressException e) {
					                        e.printStackTrace(); // Handle the exception as needed
					                        return null;
					                    }
					                })
					                .filter(address -> address != null)
					                .toArray(Address[]::new);
						
						builder.append(htmlBody);
						MimeMessage message = new MimeMessage(session);
						message.setFrom(new InternetAddress(mailFrom));
						message.setRecipients(Message.RecipientType.TO, addresses);
						message.setSubject(subject);
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
				}finally {
					pool.shutdown();
				}
			}

		});
	}

	protected Session getSessionpForProvider(Properties properties, String userName,String Passs) {
		return Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, Passs);
			}
		});
	}

	protected Properties getEmailProps(String provider) {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		if (provider.equalsIgnoreCase("zoho")) {
			properties.put("mail.smtp.host", props.getZohoHostName());
			properties.put("mail.smtp.user", props.getZohoUserName());
			properties.put("mail.smtp.port", props.getZohoPort());
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
			return properties;
		}
		properties.put("mail.smtp.host", props.getKasploHostName());
		properties.put("mail.smtp.user", props.getKasploUserName());
		properties.put("mail.smtp.port", props.getKasploPort());
		properties.put("mail.smtp.socketFactory.port", props.getKasploPort());
		properties.put("mail.smtp.debug", "true");

		return properties;
	}
	
	protected String getMailFrom(String provider) {
		if(provider.equalsIgnoreCase("zoho")) {
			return props.getZohoFrom();
		}
		return props.getKasploFrom();
	}

	protected String getPassword(String provider) {
		if(provider.equalsIgnoreCase("zoho")) {
			return props.getZohoPassword();
		}
		return props.getKasploPassword();
	}

	protected String getUserName(String provider) {
		if(provider.equalsIgnoreCase("zoho")) {
			return props.getZohoUserName();
		}
		
		return props.getKasploUserName();
	}
	
	public void sendExceptionEmail(String subject, String emailId, String module, String exception) {
	        try {

	            String htmlBody = "<!DOCTYPE html><html><head><style>*{font-family:'Open Sans',"
	                    + " Helvetica, Arial;color: #1e3465}table {margin-left:100px;font-family: arial, sans-serif;border-collapse:"
	                    + " separate;}td, th {border: 1px solid #1e3465;text-align: left;padding: 8px;}"
	                    + "th{background :#1e3465;color:white;}</style></head><body><div>"
	                    + "<div  style='font-size:14px'><p>Hi Team,</p><p> There has been an exception - " + exception
	                    + " occureded in the module "+ module +" which required immediate technical attention.</p></div>"
	                    + "<div><p align='left'>" + "<b>Regards," + "<br>"+props.getSignature()+"</b></p></div></div></body></html>";

					long mailSettings =  HazelcastConfig.getInstance().getOtpMailSettingsKB().get(AppConstants.MAIL_SETTINGS) != null ?
							HazelcastConfig.getInstance().getOtpMailSettingsKB()
							.get(AppConstants.MAIL_SETTINGS) : 0;
					switch (String.valueOf(mailSettings)) {
					case "1":
						sendEmail(htmlBody, subject, emailId,"");
						break;
					case "2":
						sendEmail(htmlBody, subject, emailId,"zoho");
						break;
					default:
						decideRandomMailProvider(htmlBody, subject, emailId);
						break;
					}
		
	        } catch (Exception e) {
	            Log.error(e.getMessage());
	        }
	    }

	private void decideRandomMailProvider(String htmlBody, String subject, String emailId) {
		if (new Random().nextInt(1000) % 2 == 0) {
			sendEmail(htmlBody, subject, emailId,"" );
		} else {
			sendEmail(htmlBody, subject, emailId,"zoho");
		}
		
	}

	
}
