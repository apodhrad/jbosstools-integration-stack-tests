package org.jboss.tools.switchyard.ui.bot.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.switchyard.reddeer.editor.SwitchYardEditor;
import org.jboss.tools.switchyard.reddeer.project.SwitchYardProject;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement.SwitchYard;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;

/**
 * Performance test
 * 
 * @author apodhrad
 * 
 */
@SwitchYard
@RunWith(RedDeerSuite.class)
public class PerformanceTest {

	public static final String PROJECT = "performance";

	@InjectRequirement
	private static SwitchYardRequirement switchyardRequirement;

	@BeforeClass
	public static void maximizeWorkbench() {
		new WorkbenchShell().maximize();
	}

	@BeforeClass
	public static void createProject() {
		try {
			new SwitchYardEditor().saveAndClose();
		} catch (Exception ex) {
			// it is ok, we just try to close switchyard.xml if it is open
		}

		switchyardRequirement.project(PROJECT).create();
	}

	@AfterClass
	public static void deleteAllProjects() {
		EditorHandler.getInstance().closeAll(true);

		// workaround for deleting all projects
		Exception exception = null;
		for (int i = 0; i < 10; i++) {
			try {
				exception = null;
				new WorkbenchShell().setFocus();
				new ProjectExplorer().deleteAllProjects();
				break;
			} catch (Exception e) {
				exception = e;
			}
		}
		if (exception != null) {
			throw new RuntimeException("Cannot delete all projects", exception);
		}
	}

	@Test
	public void testUpdatingProject() throws Exception {
		SwitchYardProject project = new SwitchYardProject(PROJECT);
		long start = System.currentTimeMillis();
		project.update();
		long end = System.currentTimeMillis();

		logPerformance("testUpdatingProject", end - start);
	}

	private void logPerformance(String description, long time) throws IOException {
		File file = new File("/home/apodhrad/Temp/performance.csv");
		try (BufferedWriter out = new BufferedWriter(new FileWriter(file, true))) {
			out.write(
					getSwitchYardVersion() + ";" + getSwitchYardInfo() + ";" + description + ";" + timeToString(time));
			out.newLine();
		}
	}

	private static String getSwitchYardVersion() {
		Bundle bundle = Platform.getBundle("org.switchyard.tools");
		return bundle.getVersion().toString();
	}

	private static String getSwitchYardInfo() {
		return switchyardRequirement.getLibraryVersionLabel() + "/" + switchyardRequirement.getTargetRuntimeLabel();
	}

	private static String timeToString(long time) {
		return new SimpleDateFormat("mm:ss:SSS").format(new Date(time));
	}

}
