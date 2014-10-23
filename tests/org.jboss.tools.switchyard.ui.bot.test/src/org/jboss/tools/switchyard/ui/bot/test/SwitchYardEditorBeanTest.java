package org.jboss.tools.switchyard.ui.bot.test;

import static org.eclipse.swtbot.swt.finder.SWTBotAssert.assertContains;
import static org.junit.Assert.assertEquals;

import org.jboss.reddeer.eclipse.jdt.ui.NewJavaClassWizardDialog;
import org.jboss.reddeer.eclipse.jdt.ui.NewJavaClassWizardPage;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.swt.condition.JobIsRunning;
import org.jboss.reddeer.swt.condition.ShellWithTextIsActive;
import org.jboss.reddeer.swt.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.condition.TableHasRows;
import org.jboss.reddeer.swt.handler.WorkbenchHandler;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.shell.WorkbenchShell;
import org.jboss.reddeer.swt.impl.table.DefaultTable;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.swt.wait.TimePeriod;
import org.jboss.reddeer.swt.wait.WaitUntil;
import org.jboss.reddeer.swt.wait.WaitWhile;
import org.jboss.reddeer.workbench.impl.editor.DefaultEditor;
import org.jboss.reddeer.workbench.impl.editor.TextEditor;
import org.jboss.tools.switchyard.reddeer.component.SwitchYardComponent;
import org.jboss.tools.switchyard.reddeer.editor.SwitchYardEditor;
import org.jboss.tools.switchyard.reddeer.project.SwitchYardProject;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement;
import org.jboss.tools.switchyard.reddeer.requirement.SwitchYardRequirement.SwitchYard;
import org.jboss.tools.switchyard.reddeer.wizard.BeanServiceWizard;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author apodhrad
 *
 */
@SwitchYard
@RunWith(RedDeerSuite.class)
public class SwitchYardEditorBeanTest {

	public static final String PROJECT_NAME = "beanimpl";
	public static final String INTERFACE_NAME = "Hello";
	public static final String BEAN_NAME = "BeanImpl";
	public static final String PACKAGE = "com.example.switchyard." + PROJECT_NAME;
	public static final String EXISTING_BEAN = "MyBean";

	@InjectRequirement
	private static SwitchYardRequirement switchYardRequirement;

	@BeforeClass
	public static void createSwitchYardProject() {
		saveAndCloseSwitchYardFile();

		new WorkbenchShell().maximize();
		new ProjectExplorer().open();

		/* Create SwitchYard Project */
		switchYardRequirement.project(PROJECT_NAME).create();

		/* Create Java Interface */
		new SwitchYardProject(PROJECT_NAME).select();
		new ShellMenu("File", "New", "Other...").select();
		new DefaultShell("New");
		new DefaultTreeItem("Java", "Interface").select();
		new PushButton("Next >").click();
		new DefaultShell("New Java Interface");
		new LabeledText("Name:").setText(INTERFACE_NAME);
		new PushButton("Finish").click();
		new WaitWhile(new ShellWithTextIsActive("New Java Interface"));
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
	}

	@After
	public void closeAllEditors() {
		try {
			new SwitchYardComponent(BEAN_NAME).delete();
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
			// this is ok, we just try to close switchyard.xml if it is open
		}
	}

	@Test
	public void beanTest() throws Exception {
		BeanServiceWizard wizard = new SwitchYardProject(PROJECT_NAME).openSwitchYardFile().addBeanImplementation();
		wizard.selectJavaInterface(INTERFACE_NAME).setName(BEAN_NAME).finish();

		SwitchYardEditor editor = new SwitchYardEditor();
		editor.save();
		String componentQuery = "/switchyard/composite/component[@name='" + BEAN_NAME + "']";
		String className = editor.xpath(componentQuery + "/implementation.bean/@class");
		assertEquals(PACKAGE + "." + BEAN_NAME, className);
		String serviceName = editor.xpath(componentQuery + "/service/@name");
		assertEquals(INTERFACE_NAME, serviceName);

		new SwitchYardComponent(BEAN_NAME).doubleClick();
		String content = new TextEditor(BEAN_NAME + ".java").getText();
		assertContains("@Service(Hello.class)", content);
		assertContains("public class " + BEAN_NAME + " implements Hello", content);
		new DefaultEditor(BEAN_NAME + ".java").close();
	}

	@Test
	public void bottomUpBeanTest() throws Exception {
		// Create Java class
		new SwitchYardProject(PROJECT_NAME).getProjectItem("src/main/java", PACKAGE).select();
		NewJavaClassWizardDialog wizard = new NewJavaClassWizardDialog();
		wizard.open();
		new NewJavaClassWizardPage().setName(EXISTING_BEAN);
		wizard.finish();

		// Add existing Java implementation
		new SwitchYardProject(PROJECT_NAME).openSwitchYardFile().addComponent();
		new SwitchYardComponent("Component").setLabel(BEAN_NAME);
		new SwitchYardEditor().addBeanImplementation(new SwitchYardComponent(BEAN_NAME));
		new DefaultShell("Bean Implementation");
		new PushButton("Browse...").click();
		new DefaultShell("");
		new DefaultText().setText(EXISTING_BEAN);
		new WaitUntil(new TableHasRows(new DefaultTable()), TimePeriod.LONG);
		new PushButton("OK").click();
		new WaitWhile(new ShellWithTextIsAvailable(""));
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new DefaultShell("Bean Implementation");
		new PushButton("Finish").click();
		new WaitWhile(new ShellWithTextIsAvailable("Bean Implementation"));
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		new SwitchYardEditor().save();

		// Check whether the result was correctly generated
		SwitchYardEditor editor = new SwitchYardEditor();
		editor.save();
		String componentQuery = "/switchyard/composite/component[@name='" + BEAN_NAME + "']";
		String className = editor.xpath(componentQuery + "/implementation.bean/@class");
		assertEquals(PACKAGE + "." + EXISTING_BEAN, className);
	}
}
