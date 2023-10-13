package in.codifi.auth.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.auth.config.HazelcastConfig;
import in.codifi.auth.config.RestProperties;
import in.codifi.auth.entity.logs.AccessLogModel;
import in.codifi.auth.repository.AccessLogManager;
import in.codifi.auth.utility.AppConstants;
import in.codifi.auth.utility.StringUtil;
import io.quarkus.arc.Priority;
import io.quarkus.logging.Log;

@Provider
@Priority(Priorities.USER)
public class AccessLogFilter implements ContainerRequestFilter, ContainerResponseFilter {

	ObjectMapper objectMapper = null;

	@Inject
	io.vertx.core.http.HttpServerRequest req;

	@Inject
	AccessLogManager accessLogManager;
	@Inject
	RestProperties props;

//	@Inject
//	JsonWebToken idToken;

	@Context
	HttpServletRequest request;

	/**
	 * Method to capture and single save request and response
	 * 
	 * @param requestContext
	 * @param responseContext
	 */

	private void caputureInSingleShot(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {

//		if (this.idToken.containsClaim("preferred_username")) {
//			uId = this.idToken.getClaim("preferred_username").toString();
//		}
//		if (this.idToken.containsClaim("ucc")) {
//			clientId = this.idToken.getClaim("ucc").toString();
//		}

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					objectMapper = new ObjectMapper();

					AccessLogModel accLogModel = new AccessLogModel();
					UriInfo uriInfo = requestContext.getUriInfo();
					MultivaluedMap<String, String> headers = requestContext.getHeaders();
					accLogModel.setContentType(headers.getFirst(AppConstants.CONTENT_TYPE));
					accLogModel.setDeviceIp(headers.getFirst("X-Forwarded-For"));
					accLogModel.setDomain(headers.getFirst("Host"));
					long lagTime = System.currentTimeMillis() - System.currentTimeMillis();
					accLogModel.setInTime((Timestamp) requestContext.getProperty("inTime"));
					accLogModel.setOutTime(new Timestamp(System.currentTimeMillis()));
					accLogModel.setMethod(requestContext.getMethod());
					accLogModel.setModule(AppConstants.MODULE);
					accLogModel.setReqBody(objectMapper.writeValueAsString(requestContext.getProperty("reqBody")));
					Object reponseObj = responseContext.getEntity();
					accLogModel.setResBody(objectMapper.writeValueAsString(reponseObj));
					accLogModel.setSource("");// TODO
					accLogModel.setUri(uriInfo.getPath().toString());
					accLogModel.setUserAgent(headers.getFirst("User-Agent"));
					String userId = objectMapper.writeValueAsString(requestContext.getProperty("userId"));
					accLogModel.setUserId(userId.replaceAll("\"", ""));
//					accLogModel.setUcc(ucc);
					accLogModel.setLagTime(lagTime);
					accLogModel.setVendor("");// TODO
					accLogModel.setSession(headers.getFirst(AppConstants.AUTHORIZATION));
					accLogModel.setReqId(requestContext.getProperty("threadId") != null
							? requestContext.getProperty("threadId").toString()
							: "singlecapture");
					accessLogManager.insertAccessLog(accLogModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		caputureInSingleShot(requestContext, responseContext);

	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = null;
		try {
			System.out.println("Reached filter");
			requestContext.setProperty("inTime", new Timestamp(System.currentTimeMillis()));
			byte[] body = requestContext.getEntityStream().readAllBytes();

			InputStream stream = new ByteArrayInputStream(body);
			requestContext.setEntityStream(stream);
			String formedReq = new String(body);
			requestContext.setProperty("reqBody", formedReq);
			String userId = "";
			if (StringUtil.isNotNullOrEmpty(formedReq)) {
				JSONObject json = new JSONObject(formedReq);
				if (json.has("userId")) {
					userId = (String) json.get("userId");
					requestContext.setProperty("userId", userId);
				}
			}
			String path = requestContext.getUriInfo().getPath().trim();

			String[] verifyTokenMethod = AppConstants.VERIFY_TOKEN_METHODS;
			boolean containsVerify = Arrays.stream(verifyTokenMethod).anyMatch(path::equals);
			if (containsVerify) {
				authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
				MultivaluedMap<String, String> map = requestContext.getHeaders();
				// Looping over the map
				for (String key : map.keySet()) {
					List<String> values = map.get(key);
					for (String value : values) {
						System.out.println(key + " - " + value);
					}
				}

				/** The below condition is only for checking the ODDIN token request **/
				if (path.equalsIgnoreCase(AppConstants.VERIFYTOKEN_PATH)) {
					Log.info("Header - " + requestContext.getHeaderString(AppConstants.X_AUTH_KEY));
					authorizationHeader = requestContext.getHeaderString(AppConstants.X_AUTH_KEY);
					if (StringUtil.isNullOrEmpty(authorizationHeader)
							|| !authorizationHeader.equalsIgnoreCase(props.getXAuthKeyValue()))
						throw new NotAuthorizedException("x-auth-key header is must and valid");
				}

			}

			String[] securedMethod = AppConstants.SECURED_METHODS;
			authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			boolean contains = Arrays.stream(securedMethod).anyMatch(path::equals);
			if (contains) {
				/** Check if the HTTP Authorization header is present **/
				if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
					throw new NotAuthorizedException("Authorization header must be provided");
				}
				String token[] = authorizationHeader.substring("Bearer".length()).trim().split(" ");
				if (token.length < 3) {
					throw new NotAuthorizedException("Invalid Authorization header");
				}
				if (StringUtil.isNotNullOrEmpty(userId) && StringUtil.isNotNullOrEmpty(token[0])
						&& userId.equals(token[0])) {
					validateToken(token[0], token[1], token[2]);
				} else {
					throw new NotAuthorizedException("Invalid User");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
			requestContext
					.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(AppConstants.UNAUTHORIZED).build());
		}

	}

	private void validateToken(String userId, String source, String token) throws Exception {

		String key = userId + "_" + source + AppConstants.HAZEL_KEY_OTP_SESSION;
		if (HazelcastConfig.getInstance().getUserSessionOtp().containsKey(key)) {
			if (!token.equals(HazelcastConfig.getInstance().getUserSessionOtp().get(key))) {
				throw new NotAuthorizedException("Not Authorized");
			}
		} else {
			throw new NotAuthorizedException("Not Authorized");
		}

	}

}
