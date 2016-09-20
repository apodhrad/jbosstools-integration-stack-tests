package org.jboss.tools.switchyard.ui.bot.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.switchyard.reddeer.component.SwitchYardComponent;
import org.jboss.tools.switchyard.reddeer.editor.SwitchYardEditor;
import org.jboss.tools.switchyard.reddeer.project.SwitchYardProject;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement.SwitchYard;
import org.jboss.tools.switchyard.reddeer.wizard.SwitchYardProjectWizard;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;
import org.osgi.framework.Bundle;

/**
 * Performance test
 * 
 * @author apodhrad
 * 
 */
@SwitchYard
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class PerformanceTest {

	public static final String PROJECT = "performance";

	public static final String[] GATEWAY_BINDINGS = new String[] {
		"Atom",
		"Camel Core (SEDA/Timer/URI)",
		"CXF",
		"File",
		"File Transfer (FTP/FTPS/SFTP)",
		"HTTP",
		"JCA",
		"JMS",
		"JPA",
		"Mail",
		"MQTT",
		"Network (TCP/UDP)",
		"REST",
		"RSS",
		"SAP",
		"SCA",
		"Scheduling",
		"SOAP",
		"SQL" };

	@Parameters(name = "{0}")
	public static Collection<String> data() {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			list.add("Num" + i);
		}
		return list;
	}

	private String name;

	public PerformanceTest(String name) {
		this.name = name;
	}

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

		new SwitchYardProjectWizard(PROJECT).config("2.0").library("2.1.0.Final").binding(GATEWAY_BINDINGS).osgi()
				.create();
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

	@After
	public void removeBean() {
		new SwitchYardComponent("Hello" + name + "Bean").delete();
		new SwitchYardEditor().saveAndClose();
	}

	@Test
	public void testUpdatingProject() throws Exception {
		new SwitchYardProject(PROJECT).openSwitchYardFile();
		long start = System.currentTimeMillis();
		new SwitchYardEditor().addBeanImplementation().createJavaInterface("Hello" + name).finish();
		// project.update();
		new SwitchYardEditor().save();
		long end = System.currentTimeMillis();

		logPerformance("testAddingBeanComponent", end - start);
	}

	private void logPerformance(String description, long time) throws IOException {
		File file = new File("/home/apodhrad/Temp/performance.csv");
		try (BufferedWriter out = new BufferedWriter(new FileWriter(file, true))) {
			out.write(new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis())) + ";"
					+ getSwitchYardVersion() + ";" + getSwitchYardInfo() + ";" + description + ";"
					+ timeToString(time));
			out.newLine();
		}
	}

	private static String getSwitchYardVersion() {
		Bundle bundle = Platform.getBundle("org.switchyard.tools");
		return bundle.getVersion().toString();
	}

	private static String getSwitchYardInfo() {
		// return switchyardRequirement.getLibraryVersionLabel() + "/" + switchyardRequirement.getTargetRuntimeLabel();
		return "2.1.0.Final" + "/" + "none/osgi";
	}

	private static String timeToString(long time) {
		return new SimpleDateFormat("mm:ss:SSS").format(new Date(time));
	}

}
