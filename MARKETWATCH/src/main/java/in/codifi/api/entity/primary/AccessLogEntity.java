package in.codifi.api.entity.primary;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "tbl_access_log")
public class AccessLogEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private long id;
	@Column(name = "URI")
	private String uri;
	@Column(name = "USER_ID")
	private String user_id;
	@Column(name = "ENTRY_TIME")
	private Date entry_time;
	@Column(name = "DEVICE_IP")
	private String device_ip;
	@Column(name = "USER_AGENT")
	private String user_agent;
	@Column(name = "CONTENT_TYPE")
	private String content_type;
	@Column(name = "AUTHENTICATE_TOKEN")
	private int authenticate_token;
	@Column(name = "REPONSE_dATA")
	private String response_data;
	@Column(name = "ELAPSED_TIME")
	private String elapsed_time;
	@Column(name = "INPUT")
	private String input;
	@Column(name = "CREATED_ON")
	@CreationTimestamp
	private java.sql.Timestamp created_on;
	@Column(name = "CREATED_BY")
	private String created_by;
	@Column(name = "UPDATED_ON")
	@UpdateTimestamp
	private Date updated_on;
	@Column(name = "UPDATED_BY")
	private String updated_by;
	@Column(name = "ACTIVE_STATUS")
	private int active_status;
	@Column(name = "DOMAIN")
	private String domain;
	@Column(name = "USER_SESSION_ID")
	private String userSessionID;
	@Column(name = "SESSION_EXPIRED_TAG")
	private int sessionExpiredTag;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Date getEntry_time() {
		return entry_time;
	}

	public void setEntry_time(Date entry_time) {
		this.entry_time = entry_time;
	}

	public String getDevice_ip() {
		return device_ip;
	}

	public void setDevice_ip(String device_ip) {
		this.device_ip = device_ip;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public int getAuthenticate_token() {
		return authenticate_token;
	}

	public void setAuthenticate_token(int authenticate_token) {
		this.authenticate_token = authenticate_token;
	}

	public String getResponse_data() {
		return response_data;
	}

	public void setResponse_data(String response_data) {
		this.response_data = response_data;
	}

	public String getElapsed_time() {
		return elapsed_time;
	}

	public void setElapsed_time(String elapsed_time) {
		this.elapsed_time = elapsed_time;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public java.sql.Timestamp getCreated_on() {
		return created_on;
	}

	public void setCreated_on(java.sql.Timestamp created_on) {
		this.created_on = created_on;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public Date getUpdated_on() {
		return updated_on;
	}

	public void setUpdated_on(Date updated_on) {
		this.updated_on = updated_on;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public int getActive_status() {
		return active_status;
	}

	public void setActive_status(int active_status) {
		this.active_status = active_status;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUserSessionID() {
		return userSessionID;
	}

	public void setUserSessionID(String userSessionID) {
		this.userSessionID = userSessionID;
	}

	public int getSessionExpiredTag() {
		return sessionExpiredTag;
	}

	public void setSessionExpiredTag(int sessionExpiredTag) {
		this.sessionExpiredTag = sessionExpiredTag;
	}

}
