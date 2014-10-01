package ntut.csie.ezScrum.web.action;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.internal.UserSession;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.ResourceFacade;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

public class FileDownloadAction extends DownloadAction {
	protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) throws Exception {
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		// attach file的資訊
		long fileId = Long.parseLong(request.getParameter("fileID"));
		String fileName = request.getParameter("fileName");
		String fileType = request.getParameter("fileType");
		String projectName = request.getParameter("projectName");

		// 如果project以及session的資訊是空的 則透過專案名稱抓取資料
		if (project == null & session == null) {
			// 根據專案名稱取得 IProject 物件
			project = ResourceFacade.getProject(projectName);
			session = new UserSession(null);
		}

		// 用file id取得檔案
		ProductBacklogHelper helper = new ProductBacklogHelper(project, session);
		AttachFileObject attachFile = helper.getAttachFile(fileId);
		/*
		 * 將字串的 UTF-8 編碼轉成 response 預設編碼 ISO-8859-1 jetty預設處理getParameter的編碼是UTF-8 tomcat預設處理getParameter的邊碼是ISO-8859-1 也就是 jetty server可以跑 new String( fileName.getBytes("UTF-8"),"ISO-8859-1"); tomcat
		 * server可以跑 new String( fileName.getBytes("ISO-8859-1"),"ISO-8859-1");
		 */
		fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		response.setHeader("Content-disposition", "inline; filename=\"" + fileName + "\"");

		// 用fileType預設檔案類型
		String contentType = fileType;
		System.out.println("contentType = " + contentType);
		File file = new File(attachFile.getPath());
		FileStreamInfo info = new FileStreamInfo(contentType, file);
		System.out.println("info = " + info.toString());
		return info;

	}
}
