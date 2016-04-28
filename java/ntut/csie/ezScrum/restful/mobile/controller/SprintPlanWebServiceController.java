package ntut.csie.ezScrum.restful.mobile.controller;

import java.sql.SQLException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;

import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

@Path("{projectName}/sprint/")
public class SprintPlanWebServiceController {
	SprintPlanWebService mSprintPlanWebService;

	/**
	 * Create Sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint/create
	 * ?username={userName}&password={password}
	 * **/
	@POST
	@Path("create")
	@Produces(MediaType.APPLICATION_JSON)
	public String createSprint(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String sprintJson) {
		String responseString = "";
		try {
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(username, password, projectName);
			// 使用者帳號
			AccountObject account = new AccountObject(decoder.getDecodeUsername());
			account.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(account, decoder.getDecodeProjectName());
			mSprintPlanWebService.createSprint(sprintJson);
			responseString = mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString = "JSONException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: createSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			responseString = "LogonException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: createSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		}
		return responseString;
	}

	/**
	 * Delete Sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint/delete
	 * /{sprintId}?username={userName}&password={password}
	 * **/
	@DELETE
	@Path("delete/{sprintId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSprint(@PathParam("projectName") String projectName,
			@PathParam("sprintId") long sprintId,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String responseString = "";
		try {
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectName());
			mSprintPlanWebService.deleteSprint(sprintId);
			responseString = mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString = "JSONException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: deleteSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			responseString = "LogonException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: deleteSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		}
		return responseString;
	}

	/**
	 * Update Sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint/update
	 * ?username={userName}&password={password}
	 * **/
	@PUT
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSprint(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password, String sprintJson) {
		String responseString = "";
		try {
			InformationDecoder decoder = new InformationDecoder();
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectName());
			mSprintPlanWebService.updateSprint(sprintJson);
			responseString += mSprintPlanWebService.getRESTFulResponseString();
		} catch (JSONException e) {
			responseString += "JSONException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: updateSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			responseString += "LogonException";
			System.out.println("class: SprintWebServiceController, "
					+ "method: updateSprint, " + "exception: " + e.toString());
			e.printStackTrace();
		}
		return responseString;
	}

	/****
	 * 取得 project 中所有的 sprint
	 * http://IP:8080/ezScrum/web-service/{projectName}/sprint
	 * /all?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllSprints(@PathParam("projectName") String projectName,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectName());
			jsonString = mSprintPlanWebService.getAllSprints();
		} catch (LogonException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getAllSprints, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getAllSprints, " + "exception: " + e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 取得 project 中所有的 sprint 包含 story 和 task
	 * http://IP:8080/ezScrum/web-service/
	 * {projectName}/sprint/{sprintId}/all?username={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintId}/all")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSprintWithStories(
			@PathParam("projectName") String projectName,
			@PathParam("sprintId") long sprintId,
			@QueryParam("username") String username,
			@QueryParam("password") String password) {
		String sprintJson = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(username, password, projectName);
			AccountObject userObject = new AccountObject(decoder.getDecodeUsername());
			userObject.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(userObject, decoder.getDecodeProjectName());
			sprintJson = mSprintPlanWebService.getSprintWithStories(sprintId);
		} catch (LogonException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getSprintWithStories, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("class: SprintWebServiceController, "
					+ "method: getSprintWithStories, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: SprintWebServiceController, "
			        + "method: getSprintWithStories, " + "exception: "
			        + e.toString());
			e.printStackTrace();
        }
		return sprintJson;
	}
}
