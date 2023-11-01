/**
 * 
 */
package in.codifi.sso.auth.controller;

import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.microprofile.jwt.JsonWebToken;

import in.codifi.cache.model.ClinetInfoModel;

/**
 * @author mohup
 *
 */
public class DefaultRestController {
	private static final String USER_ID_KEY = "preferred_username";
	private static final String UCC = "ucc";
	private static final String RAW_TOKEN = "raw_token";
	/**
	 *      * Injection point for the ID Token issued by the OpenID Connect Provider
	 *     
	 */

	@Inject
	JsonWebToken idToken;

	public String getUserId() {
		return this.idToken.getClaim(USER_ID_KEY).toString().toUpperCase();
	}

	/**
	 * 
	 * Method to get client details
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	public ClinetInfoModel clientInfo() {
		ClinetInfoModel model = new ClinetInfoModel();
		model.setUserId(this.idToken.getClaim(USER_ID_KEY).toString().toUpperCase());
		if (this.idToken.containsClaim(UCC)) {
//			Set<String> s = this.idToken.getClaimNames();
//			for (Iterator iterator = s.iterator(); iterator.hasNext();) {
//				String string = (String) iterator.next();
//				System.out.println(string);
//			}
			model.setUcc(this.idToken.getClaim(UCC).toString());
		}
		return model;
	}

	/**
	 * Method to get raw token
	 * 
	 * @author Dinesh Kumar
	 * @return
	 */
	public String rawToken() {
		String token = "";
		if (this.idToken.containsClaim(RAW_TOKEN)) {
			token = this.idToken.getClaim(RAW_TOKEN).toString();
		}
		return token;
	}
}
