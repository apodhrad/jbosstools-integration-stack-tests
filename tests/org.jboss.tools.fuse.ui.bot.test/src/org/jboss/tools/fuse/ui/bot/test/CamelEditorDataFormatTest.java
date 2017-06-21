package org.jboss.tools.fuse.ui.bot.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.component.CamelComponent;
import org.jboss.tools.fuse.reddeer.component.Marshal;
import org.jboss.tools.fuse.reddeer.dataformat.DataFormat;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.ui.bot.test.utils.ProjectFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

@CleanWorkspace
@RunWith(RedDeerSuite.class)
@OpenPerspective(JavaEEPerspective.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class CamelEditorDataFormatTest {

	public static final String PROJECT = "dataformat-test";
	public static final String DATA_FORMAT_LABEL = "Data Format Type *";
	public static final CamelComponent COMPONENT = new Marshal();

	@BeforeClass
	public static void createProject() {
		new WorkbenchShell().maximize();
		ProjectFactory.newProject(PROJECT).type(ProjectType.BLUEPRINT).create();
		new CamelProject(PROJECT).openCamelContext("blueprint.xml");
		new CamelEditor("blueprint.xml").addCamelComponent(COMPONENT, "Route _route1");
	}

	@Parameters(name = "{0}")
	public static Collection<DataFormat> data() {
		return DataFormat.getDataFormats();
	}

	private DataFormat dataFormat;

	public CamelEditorDataFormatTest(DataFormat dataFormat) {
		this.dataFormat = dataFormat;
	}

	@Before
	public void openCamelEditor() {
		new CamelProject(PROJECT).openCamelContext("blueprint.xml");
	}

	@Test
	public void testDataFormat() throws Exception {
		List<String> failures = new ArrayList<>();
		List<String> oldDeps = new CamelProject(PROJECT).getDependencies();

		CamelEditor editor = new CamelEditor("blueprint.xml");
		editor.activate();
		editor.setComboProperty(COMPONENT.getLabel(), DATA_FORMAT_LABEL, dataFormat.getName());
		editor.save();

		List<String> newDeps = new CamelProject(PROJECT).getDependencies();
		if (!newDeps.containsAll(oldDeps)) {
			failures.add("Some dependencies were removed!");
		}
		newDeps.removeAll(oldDeps);
		if (newDeps.isEmpty()) {
			failures.add("No dependency was added");
		}
		if (newDeps.size() > 1) {
			failures.add("Too many dependencies were added");
		}
		if (!failures.isEmpty()) {
			fail(failures.toString());
		}
		String[] newDep = newDeps.get(0).split("/");
		String groupId = newDep[0];
		String artifactId = newDep[1];
		assertEquals(dataFormat.getGroupId() + "/" + dataFormat.getArtifactId(), groupId + "/" + artifactId);
	}

}
