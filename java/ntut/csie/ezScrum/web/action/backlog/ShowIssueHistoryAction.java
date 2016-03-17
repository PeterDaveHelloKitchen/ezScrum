package ntut.csie.ezScrum.web.action.backlog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.dataObject.UnplanObject;
import ntut.csie.ezScrum.web.support.SessionManager;

public class ShowIssueHistoryAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowIssueHistoryAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// html
		return false;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info(" Show Issue History. ");

		// Get Project
		ProjectObject project = SessionManager.getProject(request);
		
		// get parameter info
		long serialIssueId = Long.parseLong(request.getParameter("issueID"));
		String issueType = request.getParameter("issueType");

		// 用 Gson 轉換 issue 為 json 格式傳出
		IssueHistoryUI ihui = null;
		if (issueType.equals("Task")) {
			TaskObject task = TaskObject.get(project.getId(), serialIssueId);
			if (task != null) {
				ihui = new IssueHistoryUI(task);
			}
		} else if (issueType.equals("Story")){
			StoryObject story = StoryObject.get(project.getId(), serialIssueId);
			if (story != null) {
				ihui = new IssueHistoryUI(story);
			}
		} else {
			UnplanObject unplan = UnplanObject.get(project.getId(), serialIssueId);
			if (unplan != null) {
				ihui = new IssueHistoryUI(unplan);
			}
		}
		Gson gson = new Gson();
		return new StringBuilder(gson.toJson(ihui));
	}

	private class IssueHistoryUI {
		private long Id = -1L;
		private String Link = "";
		private String Name = "";
		private String IssueType = "";

		private List<IssueHistoryList> IssueHistories = new LinkedList<IssueHistoryList>();
		
		public IssueHistoryUI(UnplanObject unplan) {
			if (unplan != null) {
				Id = unplan.getId();
				Link = "";
				Name = unplan.getName();
				IssueType = "Unplan";

				if (unplan.getHistories().size() > 0) {
					for (HistoryObject history : unplan.getHistories()) {
						if (history.getDescription().length() > 0) {
							IssueHistories.add(new IssueHistoryList(history));
						}
					}
				}
			}
		}

		public IssueHistoryUI(StoryObject story) {
			if (story != null) {
				Id = story.getId();
				Link = "";
				Name = story.getName();
				IssueType = "Story";

				if (story.getHistories().size() > 0) {
					for (HistoryObject history : story.getHistories()) {
						if (history.getDescription().length() > 0) {
							IssueHistories.add(new IssueHistoryList(history));
						}
					}
				}
			}
		}

		public IssueHistoryUI(TaskObject task) {
			Id = task.getId();
			Link = "";
			Name = task.getName();
			IssueType = "Task";

			for (HistoryObject history : task.getHistories()) {
				IssueHistories.add(new IssueHistoryList(history));
			}
		}
	}

	private class IssueHistoryList {
		private String Description = "";
		private String HistoryType = "";
		private String ModifiedDate = "";

		public IssueHistoryList(HistoryObject history) {
			parseDate(history.getCreateTime());
			Description = history.getDescription();
			HistoryType = history.getHistoryTypeString();
		}

		private void parseDate(long date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
			Date d = new Date(date);
			ModifiedDate = sdf.format(d);
		}
	}
}
