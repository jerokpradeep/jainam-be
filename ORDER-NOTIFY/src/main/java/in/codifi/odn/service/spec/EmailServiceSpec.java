package in.codifi.odn.service.spec;

public interface EmailServiceSpec {
	
	void sendDefaultEmail(String subject ,String body,String recepients);
	
	void sendCustomEmailProvider(String provider);

	void sendExceptionEmailToSupport(String exceptionMessgae, String className, String reason);


}
