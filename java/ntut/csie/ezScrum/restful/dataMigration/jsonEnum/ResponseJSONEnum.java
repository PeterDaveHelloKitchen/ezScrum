package ntut.csie.ezScrum.restful.dataMigration.jsonEnum;

public class ResponseJSONEnum {
	// Key
	public static final String JSON_KEY_MESSAGE = "message";
	public static final String JSON_KEY_CONTENT = "content";

	// Message
	public static final String SUCCESS_MESSAGE = "success";
	public static final String ERROR_BAD_REQUEST_MESSAGE = "fail due to bad request";
	public static final String ERROR_NOT_FOUND_MESSAGE = "fail due to resource not found";
	public static final String ERROR_RESOURCE_EXIST_MESSAGE = "fail due to existing resource";
	public static final String ERROR_FORBIDDEN_MESSAGE = "fail due to permission denied";
	public static final String ERROR_INTERNAL_SERVER_ERROR_MESSAGE = "fail due to internal server error";
	
	// Content
	public static final String NO_CONTENT = "";
}
