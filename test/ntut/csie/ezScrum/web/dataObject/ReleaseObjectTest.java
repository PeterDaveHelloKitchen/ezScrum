package ntut.csie.ezScrum.web.dataObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.dao.ReleaseDAO;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.internal.MySQLControl;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateSprint;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.databasEnum.ReleaseEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReleaseObjectTest {
	private MySQLControl mControl = null;
	private Configuration mConfig = null;
	private CreateProject mCP = null;
	private CreateSprint mCS = null;
	private final int mPROJECT_COUNT = 1;
	private long mProjectId = -1;
	private ReleaseObject mRelease = null;

	@Before
	public void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mCP = new CreateProject(mPROJECT_COUNT);
		mCP.exeCreate();

		mControl = new MySQLControl(mConfig);
		mControl.connect();

		mProjectId = mCP.getAllProjects().get(0).getId();
		mRelease = createRelease();
	}

	@After
	public void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();

		// 讓 config 回到 Production 模式
		mConfig.setTestMode(false);
		mConfig.save();

		mConfig = null;
		mControl = null;
		mCP = null;
	}
	
	@Test
	public void testSaveCreateNewRelease() throws SQLException {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/08/03";
		String releaseDueDate = "2015/10/31";
		
		// Create release object
		ReleaseObject release = new ReleaseObject(mProjectId);
		release.setName(releaseName)
		       .setDescription(releaseDescription)
		       .setStartDate(releaseStartDate)
		       .setDueDate(releaseDueDate)
		       .save();
		
		// 從資料庫撈出  Release
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName(ReleaseEnum.TABLE_NAME);
		valueSet.addEqualCondition(ReleaseEnum.ID, release.getId());

		String query = valueSet.getSelectQuery();
		ResultSet result = mControl.executeQuery(query);
		ReleaseObject releaseCreated = null;
		if (result.next()) {
			releaseCreated = ReleaseDAO.convert(result);
		}
		// Close result set
		closeResultSet(result);

		// assert
		assertEquals(release.getId(), releaseCreated.getId());
		assertEquals(release.getProjectId(), releaseCreated.getProjectId());
		assertEquals(releaseName, releaseCreated.getName());
		assertEquals(releaseDescription, releaseCreated.getDescription());
		assertEquals(releaseStartDate, releaseCreated.getStartDateString());
		assertEquals(releaseDueDate, releaseCreated.getDueDateString());
	}

	@Test
	public void testSaveUpdateRelease() {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME_NEW";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION_NEW";
		String releaseStartDate = "2015/06/03";
		String releaseDueDate = "2015/08/31";
		
		// Update Release
		mRelease.setName(releaseName)
		        .setDescription(releaseDescription)
		        .setStartDate(releaseStartDate)
		        .setDueDate(releaseDueDate)
		        .save();
		
		ReleaseObject release = ReleaseObject.get(mRelease.getId());
		
		// assert
		assertEquals(releaseName, release.getName());
		assertEquals(releaseDescription, release.getDescription());
		assertEquals(releaseStartDate, release.getStartDateString());
		assertEquals(releaseDueDate, release.getDueDateString());
	}
	
	@Test
	public void testDelete() {
		// Get releaseId
		long releaseId = mRelease.getId();
		// Assert release exist
		assertNotNull(mRelease);
		// Delete release
		boolean deleteStatus = mRelease.delete();
		// Assert Delete Status
		assertTrue(deleteStatus);
		
		// Reload release object
		ReleaseObject release = ReleaseObject.get(releaseId);
		// Assert release object is null
		assertNull(release);
	}
	
	@Test
	public void testContainsSprint() {
		ReleaseObject release = new ReleaseObject(mProjectId);
		release.setName("TEST_RELEASE")
		       .setStartDate("2015/08/01")
		       .setDueDate("2015/08/31")
		       .save();
		SprintObject sprint1 = new SprintObject(mProjectId);
		sprint1.setSprintGoal("TEST_SPRINT_GOAL_1")
		       .setStartDate("2015/07/24")
		       .setDueDate("2015/08/01")
		       .save();
		SprintObject sprint2 = new SprintObject(mProjectId);
		sprint2.setSprintGoal("TEST_SPRINT_GOAL_2")
		       .setStartDate("2015/08/31")
		       .setDueDate("2015/09/06")
		       .save();
		SprintObject sprint3 = new SprintObject(mProjectId);
		sprint3.setSprintGoal("TEST_SPRINT_GOAL_3")
		       .setStartDate("2015/08/15")
		       .setDueDate("2015/08/21")
		       .save();
		// assert
		assertFalse(release.containsSprint(sprint1));
		assertFalse(release.containsSprint(sprint2));
		assertTrue(release.containsSprint(sprint3));
	}
	
	@Test
	public void testGetSprints() {
		ReleaseObject release = new ReleaseObject(mProjectId);
		release.setName("TEST_RELEASE")
		       .setStartDate("2015/08/01")
		       .setDueDate("2015/08/31")
		       .save();
		SprintObject sprint1 = new SprintObject(mProjectId);
		sprint1.setSprintGoal("TEST_SPRINT_GOAL_1")
		       .setStartDate("2015/08/01")
		       .setDueDate("2015/08/07")
		       .save();
		SprintObject sprint2 = new SprintObject(mProjectId);
		sprint2.setSprintGoal("TEST_SPRINT_GOAL_2")
		       .setStartDate("2015/08/08")
		       .setDueDate("2015/08/14")
		       .save();
		SprintObject sprint3 = new SprintObject(mProjectId);
		sprint3.setSprintGoal("TEST_SPRINT_GOAL_3")
		       .setStartDate("2015/08/15")
		       .setDueDate("2015/08/21")
		       .save();
		// assert sprint count
		assertEquals(3, release.getSprints().size());
		// assert sprint 1
		assertEquals(sprint1.getId(), release.getSprints().get(0).getId());
		assertEquals(sprint1.getSprintGoal(), release.getSprints().get(0).getSprintGoal());
		assertEquals(sprint1.getStartDateString(), release.getSprints().get(0).getStartDateString());
		assertEquals(sprint1.getDueDateString(), release.getSprints().get(0).getDueDateString());
		// assert sprint 2
		assertEquals(sprint2.getId(), release.getSprints().get(1).getId());
		assertEquals(sprint2.getSprintGoal(), release.getSprints().get(1).getSprintGoal());
		assertEquals(sprint2.getStartDateString(), release.getSprints().get(1).getStartDateString());
		assertEquals(sprint2.getDueDateString(), release.getSprints().get(1).getDueDateString());
		// assert sprint 3
		assertEquals(sprint3.getId(), release.getSprints().get(2).getId());
		assertEquals(sprint3.getSprintGoal(), release.getSprints().get(2).getSprintGoal());
		assertEquals(sprint3.getStartDateString(), release.getSprints().get(2).getStartDateString());
		assertEquals(sprint3.getDueDateString(), release.getSprints().get(2).getDueDateString());
	}
	
	@Test
	public void testGetStories() {
		// Test Data
		String storyName = "TEST_STORY_NAME_";
		String storyNotes = "TEST_STORY_NOTES_";
		String storyHowtodemo = "TEST_STORY_HOW_TO_DEMO_";
		int storyEstimate = 8;
		int storyImportance = 96;
		
		// Create Sprint
		SprintObject sprint = new SprintObject(mProjectId);
		sprint.setInterval(2)
		      .setHoursCanCommit(100)
		      .setMembers(4)
		      .setSprintGoal("TEST_SPRINT_GOAL")
		      .setDailyInfo("TEST_SPRINT_DAILY_INFO")
		      .setStartDate("2015/08/03")
		      .setDemoDate("2015/08/17")
		      .setDueDate("2015/08/17")
		      .save();
		
		// Create Story 1
		StoryObject story1 = new StoryObject(mProjectId);
		story1.setSprintId(sprint.getId())
		      .setName(storyName + 1)
		      .setEstimate(storyEstimate)
		      .setStatus(StoryObject.STATUS_UNCHECK)
		      .setNotes(storyNotes + 1)
		      .setImportance(storyImportance)
		      .setHowToDemo(storyHowtodemo + 1)
		      .save();
		
		// Create Story 2
		StoryObject story2 = new StoryObject(mProjectId);
		story2.setSprintId(sprint.getId())
		        .setName(storyName + 2)
		        .setEstimate(storyEstimate)
		        .setStatus(StoryObject.STATUS_UNCHECK)
		        .setNotes(storyNotes + 2)
		        .setImportance(storyImportance)
		        .setHowToDemo(storyHowtodemo + 2)
		        .save();
		
		// Create Story 3
		StoryObject story3 = new StoryObject(mProjectId);
		story3.setSprintId(sprint.getId())
		        .setName(storyName + 3)
		        .setEstimate(storyEstimate)
		        .setStatus(StoryObject.STATUS_UNCHECK)
		        .setNotes(storyNotes + 3)
		        .setImportance(storyImportance)
		        .setHowToDemo(storyHowtodemo + 3)
		        .save();
		
		// GetStories
		ArrayList<StoryObject> stories = mRelease.getStories();
		
		// Assert
		assertEquals(3, stories.size());
		
		for (int i = 0; i < stories.size(); i++) {
			assertEquals(storyName + (i + 1), stories.get(i).getName());
			assertEquals(storyNotes + (i + 1), stories.get(i).getNotes());
			assertEquals(storyHowtodemo + (i + 1), stories.get(i).getHowToDemo());
			assertEquals(storyEstimate, stories.get(i).getEstimate());
			assertEquals(storyImportance, stories.get(i).getImportance());
			assertEquals(StoryObject.STATUS_UNCHECK, stories.get(i).getStatus());
			assertEquals(sprint.getId(), stories.get(i).getSprintId());
		}
	}
	
	private ReleaseObject createRelease() {
		// Test Data
		String releaseName = "TEST_RELEASE_NAME";
		String releaseDescription = "TEST_RELEASE_DESCRIPTION";
		String releaseStartDate = "2015/08/03";
		String releaseDueDate = "2015/10/31";

		// Create release object
		ReleaseObject release = new ReleaseObject(mProjectId);
		release.setName(releaseName)
		        .setDescription(releaseDescription)
		        .setStartDate(releaseStartDate)
		        .setDueDate(releaseDueDate)
		        .save();

		// assert
		assertNotSame(-1, release.getId());
		assertEquals(mProjectId, release.getProjectId());
		assertEquals(releaseName, release.getName());
		assertEquals(releaseDescription, release.getDescription());
		assertEquals(releaseStartDate, release.getStartDateString());
		assertEquals(releaseDueDate, release.getDueDateString());
		return release;
	}
	
	private void closeResultSet(ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
