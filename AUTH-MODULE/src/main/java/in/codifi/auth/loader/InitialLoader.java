package in.codifi.auth.loader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import in.codifi.auth.service.AuthService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

@SuppressWarnings("serial")
@ApplicationScoped
public class InitialLoader extends HttpServlet {

	@Inject
	AuthService authService;



	public void init(@Observes StartupEvent ev) throws ServletException {
		Log.info("All the pre-Lodings are started");
		authService.loadTwoFAUserPreference();
		Log.info("All the pre-Lodings are ended");
	}

}
