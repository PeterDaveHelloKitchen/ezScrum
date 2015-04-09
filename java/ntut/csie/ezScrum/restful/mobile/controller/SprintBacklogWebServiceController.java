package ntut.csie.ezScrum.restful.mobile.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ntut.csie.ezScrum.restful.mobile.service.SprintBacklogWebService;
import ntut.csie.ezScrum.restful.mobile.service.SprintPlanWebService;
import ntut.csie.ezScrum.restful.mobile.support.InformationDecoder;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

@Path("{projectID}/sprint-backlog/")
public class SprintBacklogWebServiceController {
	private SprintPlanWebService mSprintPlanWebService;
	private SprintBacklogWebService mSprintBacklogWebService;

	/****
	 * 取得所有sprint information
	 * http://IP:8080/ezScrum/web-service/{projectID}/sprint
	 * -backlog/sprintlist?userName={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("sprintlist")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSprintInfoList(@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectId) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectId);
			AccountObject user = new AccountObject(decoder.getDecodeUserName());
			user.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(user,
					decoder.getDecodeProjectID());
			jsonString = mSprintPlanWebService.getRESTFulResponseString();
		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: SprintPlanWebService, "
					+ "method: SprintPlanWebService, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: ConvertSprintBacklog, "
					+ "method: readSprintInformationList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 依照sprint id和 handler id 取得sprint backlog(該sprint的story及底下的task資訊)
	 * http://IP
	 * :8080/ezScrum/web-service/{projectID}/sprint-backlog/{sprintID}/{
	 * handlerID}/sprintbacklog?userName={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintID}/{handlerID}/sprintbacklog")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSprintBacklog(@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectId,
			@PathParam("sprintID") String sprintId,
			@PathParam("handlerID") String handlerId) {
		String handler = "ALL";
		if (handlerId != null) {
			handler = handlerId; // filter name
		}

		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectId);
			if (sprintId.equals("currentSprint")) {
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUserName(), decoder.getDecodePwd(),
						decoder.getDecodeProjectID());
			} else {
				int iteration = Integer.parseInt(sprintId);
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUserName(), decoder.getDecodePwd(),
						decoder.getDecodeProjectID(), iteration);
			}

			jsonString = mSprintBacklogWebService
					.getSprintBacklog(handler);

		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out.println("class: SprintPlanWebService, "
					+ "method: SprintPlanWebService, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: ConvertSprintBacklog, "
					+ "method: readSprintInformationList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 取得current sprint information
	 * http://IP:8080/ezScrum/web-service/{projectID
	 * }/sprint-backlog/current-sprint?userName={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("current-sprint")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCurrentSprintBacklog(
			@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectId) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectId);
			AccountObject user = new AccountObject(decoder.getDecodeUserName());
			user.setPassword(decoder.getDecodePwd());
			mSprintPlanWebService = new SprintPlanWebService(user,
					decoder.getDecodeProjectID());
			jsonString = mSprintPlanWebService.getCurrentSprint();
		} catch (Exception e) {
			System.out.println("class: SprintBacklogWebServiceController, "
					+ "method: getCurrentSprintBacklog, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/****
	 * 取得指定sprint 中所有story id list
	 * http://IP:8080/ezScrum/web-service/{projectID}
	 * /sprint-backlog/{sprintID}/storylist
	 * ?userName={userName}&password={password}
	 * 
	 * @param userName
	 * @param password
	 * @param projectId
	 * @param sprintId
	 * @return
	 */
	@GET
	@Path("{sprintID}/storylist")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStoryIDList(@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectId,
			@PathParam("sprintID") String sprintId) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectId);
			if (sprintId.equals("the-latest")) {
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUserName(), decoder.getDecodePwd(),
						decoder.getDecodeProjectID());
			} else {
				int iteration = Integer.parseInt(sprintId);
				mSprintBacklogWebService = new SprintBacklogWebService(
						decoder.getDecodeUserName(), decoder.getDecodePwd(),
						decoder.getDecodeProjectID(), iteration);
			}
			jsonString = mSprintBacklogWebService
					.getRESTFulResponseString();
		} catch (IOException e) {
			System.out.println("class: InformationDecoder, "
					+ "method: decode, " + "exception: " + e.toString());
		} catch (LogonException e) {
			System.out.println("class: SprintBacklogWebServiceController, "
					+ "method: getSprintBacklogList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("class: ConvertSprintBacklog, "
					+ "method: readStoryInformationList, " + "exception: "
					+ e.toString());
			e.printStackTrace();
		}
		return jsonString;
	}

	/***
	 * 取得單一Story的所有Task id list
	 * http://IP:8080/ezScrum/web-service/{projectID}/sprint
	 * -backlog/{sprintID}/{
	 * storyID}/task-id-list?userName={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintID}/{storyID}/task-id-list")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskIDList(@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectId,
			@PathParam("sprintID") String sprintId,
			@PathParam("storyID") String storyId) {
		String taskIDListJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectId);
			mSprintBacklogWebService = new SprintBacklogWebService(
					decoder.getDecodeUserName(), decoder.getDecodePwd(),
					decoder.getDecodeProjectID(), Integer.parseInt(sprintId));
			taskIDListJsonString = mSprintBacklogWebService
					.getTaskIDList(storyId);
		} catch (IOException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskIDList, exception:IOException, "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskIDList, exception:LogonException, "
							+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskIDList, exception:JSONException, "
							+ e.toString());
			e.printStackTrace();
		} finally {
		}
		return taskIDListJsonString;
	}

	/**
	 * 取得單一Task的History
	 * http://IP:8080/ezScrum/web-service/{projectID}/sprint-backlog
	 * /{sprintID}/{taskID}/history?userName={userName}&password={password}
	 * 
	 * @param userName
	 * @param password
	 * @param projectId
	 * @param sprintId
	 * @param storyID
	 * @param taskId
	 * @return
	 */
	@GET
	@Path("{sprintID}/{taskID}/history")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskHistory(@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectId,
			@PathParam("sprintID") String sprintId,
			@PathParam("taskID") String taskId) {
		String taskHistoryJsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectId);
			mSprintBacklogWebService = new SprintBacklogWebService(
					decoder.getDecodeUserName(), decoder.getDecodePwd(),
					decoder.getDecodeProjectID(), Integer.parseInt(sprintId));
			taskHistoryJsonString = mSprintBacklogWebService
					.getTaskHsitoryList(taskId);
		} catch (IOException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:IOException, "
							+ e.toString());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:NumberFormatException, "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:LogonException, "
							+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:JSONException, "
							+ e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskHistory, exception:SQLException, "
							+ e.toString());
			e.printStackTrace();
		} finally {
		}
		return taskHistoryJsonString;
	}

	/****
	 * 取得單一Task的 information
	 * http://IP:8080/ezScrum/web-service/{projectID}/sprint
	 * -backlog/{sprintID}/{taskID}?userName={userName}&password={password}
	 * 
	 * @return
	 */
	@GET
	@Path("{sprintID}/{taskID}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaskInformation(@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@PathParam("projectID") String projectId,
			@PathParam("sprintID") String sprintId,
			@PathParam("taskID") String taskId) {
		String jsonString = "";
		InformationDecoder decoder = new InformationDecoder();
		try {
			decoder.decode(userName, password, projectId);
			mSprintBacklogWebService = new SprintBacklogWebService(
					decoder.getDecodeUserName(), decoder.getDecodePwd(),
					decoder.getDecodeProjectID());
			jsonString = mSprintBacklogWebService
					.getTaskInformation(taskId);
		} catch (IOException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskInformation, exception:IOException, "
							+ e.toString());
			e.printStackTrace();
		} catch (LogonException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskInformation, exception:LogonException, "
							+ e.toString());
			e.printStackTrace();
		} catch (JSONException e) {
			System.out
					.println("Class:SprintBacklogWebServiceController.java, method:getTaskInformation, exception:JSONException, "
							+ e.toString());
			e.printStackTrace();
		} finally {
		}
		return jsonString;
	}

}
