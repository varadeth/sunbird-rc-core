package io.opensaber.pojos;

import java.util.Map;

public class Response {
	private String id;
	private String ver;
	private Long ets;
	private ResponseParams params;

	public Response() {
		this.ver = "1.0";
		this.ets = System.currentTimeMillis();
	}

	public Response(API_ID apiId, String httpStatus, ResponseParams responseParams) {
		this.ver = "1.0";
		this.ets = System.currentTimeMillis();
		this.id = apiId.getId();
		this.responseCode = httpStatus;
		this.params = responseParams;
	}

	public enum API_ID {
		CREATE("open-saber.registry.create"),
		READ("open-saber.registry.read"),
		UPDATE("open-saber.registry.update"),
		AUDIT("open-saber.registry.audit"),
		HEALTH("open-saber.registry.health"),
		DELETE("open-saber.registry.delete"),
		SEARCH("open-saber.registry.search"),
		SIGN("open-saber.utils.sign"),
		VERIFY("open-saber.utils.verify"),
		KEYS("open-saber.utils.keys"),
		ENCRYPT("open-saber.utils.encrypt"),
		DECRYPT("open-saber.utils.decrypt"),
		NONE("");
		private String id;

		private API_ID(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}

	public enum Status {
		SUCCESSFUL, UNSUCCESSFUL;
	}

	private String responseCode;
	private Object result;

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public Long getEts() {
		return ets;
	}
	public void setEts(Long ets) {
		this.ets = ets;
	}
	public ResponseParams getParams() {
		return params;
	}
	public void setParams(ResponseParams params) {
		this.params = params;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public String getResponseCode() {
		return responseCode;
	}

}
