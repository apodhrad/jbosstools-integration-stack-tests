package org.jboss.tools.switchyard.ui.bot.test;

import static org.junit.Assert.assertEquals;

import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.swt.condition.JobIsRunning;
import org.jboss.reddeer.swt.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.condition.TableHasRows;
import org.jboss.reddeer.swt.handler.WorkbenchHandler;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.shell.WorkbenchShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.wait.TimePeriod;
import org.jboss.reddeer.swt.wait.WaitUntil;
import org.jboss.reddeer.swt.wait.WaitWhile;
import org.jboss.tools.bpel.reddeer.editor.BpelEditor;
import org.jboss.tools.switchyard.reddeer.component.SwitchYardComponent;
import org.jboss.tools.switchyard.reddeer.editor.SwitchYardEditor;
import org.jboss.tools.switchyard.reddeer.project.SwitchYardProject;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement.SwitchYard;
import org.jboss.tools.switchyard.reddeer.wizard.BPELServiceWizard;
import org.jboss.tools.switchyard.reddeer.wizard.ImportFileWizard;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@SwitchYard
@RunWith(RedDeerSuite.class)
public class SwitchYardEditorBPELTest {

	public static final String PROJECT_NAME = "bpelimpl";
	public static final String INTERFACE_NAME = "SayHelloArtifacts";
	public static final String BPEL_PROCESS_NAME = "BPELImpl";
	public static final String PACKAGE = "com.example.switchyard." + PROJECT_NAME;
	public static final String EXISTING_BPEL = "SayHello";

	@InjectRequirement
	private static SwitchYardRequirement switchYardRequirement;

	@BeforeClass
	public static void createSwitchYardProject() {
		saveAndCloseSwitchYardFile();

		new WorkbenchShell().maximize();
		new ProjectExplorer().open();

		/* Create SwitchYard Project */
		switchYardRequirement.project(PROJECT_NAME).create();

		// Import wsdl file
		new SwitchYardProject(PROJECT_NAME).getProjectItem("src/main/resources", PACKAGE).select();
		new ImportFileWizard().importFile("resources/wsdl", "Hello.wsdl");
		new ImportFileWizard().importFile("resources/bpel", "SayHello.bpel");
		new ImportFileWizard().importFile("resources/wsdl", "SayHelloArtifacts.wsdl");
	}

	@After
	public void closeAllEditors() {
		try {
			new SwitchYardComponent(BPEL_PROCESS_NAME).delete();
		} catch (Exception ex) {
			// this is ok
		}
		WorkbenchHandler.getInstance().closeAllEditors();
	}

	@AfterClass
	public static void deleteSwitchYardProject() {
		saveAndCloseSwitchYardFile();
		new ProjectExplorer().deleteAllProjects();
	}

	public static void saveAndCloseSwitchYardFile() {
		try {
			new SwitchYardEditor().saveAndClose();
		} catch (Exception ex) {
			// it is ok, we just try to close switchyard.xml if it is open
		}
	}

	@Test
	public void bpelTest() throws Exception {
		BPELServiceWizard wizard = new SwitchYardProject(PROJECT_NAME).openSwitchYardFile().addBPELImplementation();
		wizard.setFileName(BPEL_PROCESS_NAME).selectJavaInterface(INTERFACE_NAME + ".wsdl").next();
		wizard.setProcessName(BPEL_PROCESS_NAME + "Process").finish();

		SwitchYardEditor editor = new SwitchYardEditor();
		editor.save();
		String componentQuery = "/switchyard/composite/component[@name='" + BPEL_PROCESS_NAME + "']";
		String processName = editor.xpath(componentQuery + "/implementation.bpel/@process");
		assertEquals("process:" + BPEL_PROCESS_NAME + "Process", processName);
		String serviceName = editor.xpath(componentQuery + "/service/@name");
		// assertEquals(INTERFACE_NAME, serviceName);

		new SwitchYardComponent(BPEL_PROCESS_NAME).doubleClick();
		new BpelEditor(BPEL_PROCESS_NAME + "Process.bpel");
	}

	@Test
	public void bottomUpBpelTest() throws Exception {
		// Import java file
		new SwitchYardProject(PROJECT_NAME).getProjectItem("src/main/java", PACKAGE).select();
		new ImportFileWizard().importFile("resources/camel", EXISTING_BPEL + ".java");

		// Add existing Camel Java implementation
		new SwitchYardProject(PROJECT_NAME).openSwitchYardFile().addComponent();
		new SwitchYardComponent("Component").setLabel(BPEL_PROCESS_NAME);
		new SwitchYardEditor().addBPELImplementation(new SwitchYardComponent(BPEL_PROCESS_NAME));
		new DefaultShell("");
		new PushButton("Browse...").click();
		new DefaultShell("Select Resource");
		new DefaultText().setText(EXISTING_BPEL + ".bpel");
		new WaitUntil(new TableHasRows(new DefaultTable()), TimePeriod.LONG);
		new PushButton("OK").click();
		new WaitWhile(new ShellWithTextIsAvailable("Select Resource"));
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new DefaultShell("");
		new PushButton("Finish").click();
		new WaitWhile(new ShellWithTextIsAvailable(""));
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new SwitchYardEditor().save();

		// Check whether the result was correctly generated
		SwitchYardEditor editor = new SwitchYardEditor();
		editor.save();
		String componentQuery = "/switchyard/composite/component[@name='" + BPEL_PROCESS_NAME + "']";
		String processName = editor.xpath(componentQuery + "/implementation.bpel/@process");
		assertEquals("sayHello:" + EXISTING_BPEL, processName);
	}

}
