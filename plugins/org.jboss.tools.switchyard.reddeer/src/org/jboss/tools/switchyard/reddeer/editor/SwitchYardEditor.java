package org.jboss.tools.switchyard.reddeer.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.reddeer.gef.api.EditPart;
import org.jboss.reddeer.gef.condition.EditorHasEditParts;
import org.jboss.reddeer.gef.editor.GEFEditor;
import org.jboss.reddeer.swt.condition.JobIsRunning;
import org.jboss.reddeer.swt.condition.WaitCondition;
import org.jboss.reddeer.swt.handler.IBeforeShellIsClosed;
import org.jboss.reddeer.swt.handler.ShellHandler;
import org.jboss.reddeer.swt.wait.TimePeriod;
import org.jboss.reddeer.swt.wait.WaitUntil;
import org.jboss.reddeer.swt.wait.WaitWhile;
import org.jboss.tools.switchyard.reddeer.component.SwitchYardComponent;
import org.jboss.tools.switchyard.reddeer.component.SwitchYardComposite;
import org.jboss.tools.switchyard.reddeer.preference.CompositePropertiesPage;
import org.jboss.tools.switchyard.reddeer.wizard.BPMServiceWizard;
import org.jboss.tools.switchyard.reddeer.wizard.BeanServiceWizard;
import org.jboss.tools.switchyard.reddeer.wizard.CamelJavaWizard;
import org.jboss.tools.switchyard.reddeer.wizard.CamelXMLWizard;
import org.jboss.tools.switchyard.reddeer.wizard.DroolsServiceWizard;
import org.jboss.tools.switchyard.reddeer.wizard.ReferenceWizard;
import org.junit.Assert;

/**
 * 
 * @author apodhrad
 *
 */
public class SwitchYardEditor extends GEFEditor {

	public static final String TITLE = "switchyard.xml";
	public static final String TOOL_COMPONENT = "Component";
	public static final String TOOL_SERVICE = "Service";
	public static final String TOOL_REFERENCE = "Reference";
	public static final String TOOL_BEAN = "Bean";
	public static final String TOOL_CAMEL_JAVA = "Camel (Java)";
	public static final String TOOL_CAMEL_XML = "Camel (XML)";
	public static final String TOOL_BPEL = "Process (BPEL)";
	public static final String TOOL_BPMN = "Process (BPMN)";
	public static final String TOOL_RULES = "Rules";

	private static Logger log = Logger.getLogger(SwitchYardEditor.class);
	private static Shell remainedShell = null;

	protected File sourceFile;
	protected SwitchYardComponent composite; 

	public SwitchYardEditor() {
		super(TITLE);
		composite = getComposite();
	}

	public SwitchYardComposite getComposite() {
		String compositeName = getCompositeName();
		return new SwitchYardComposite(compositeName);
	}

	public String getCompositeName() {
		File sourceFile = getSourceFile();
		try {
			XPathEvaluator xpath = new XPathEvaluator(new FileReader(sourceFile));
			String name = xpath.evaluateString("/switchyard/@name");
			return name;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void autoLayout() {
		composite.getContextButton("Auto-layout Diagram").click();
		save();
	}

	public void addComponent() {
		int oldCount = getNumberOfEditParts();
		getPalette().activateTool(TOOL_COMPONENT);
		composite.click();
		new WaitUntil(new EditorHasEditParts(this, oldCount));
	}

	public void addService() {
		getPalette().activateTool(TOOL_SERVICE);
		composite.click();
	}

	public ReferenceWizard addReference() {
		getPalette().activateTool(TOOL_REFERENCE);
		composite.click();
		return new ReferenceWizard();
	}

	public BeanServiceWizard addBeanImplementation() {
		return addBeanImplementation(composite);
	}

	public BeanServiceWizard addBeanImplementation(EditPart editPart) {
		addTool(TOOL_BEAN, editPart);
		return new BeanServiceWizard();
	}

	public CamelJavaWizard addCamelJavaImplementation() {
		return addCamelJavaImplementation(composite);
	}

	public CamelJavaWizard addCamelJavaImplementation(EditPart editPart) {
		addTool(TOOL_CAMEL_JAVA, editPart);
		return new CamelJavaWizard(this);
	}
	
	public CamelXMLWizard addCamelXMLImplementation() {
		return addCamelXmlImplementation(composite);
	}
	
	public CamelXMLWizard addCamelXmlImplementation(EditPart editPart) {
		addTool(TOOL_CAMEL_XML, editPart);
		return new CamelXMLWizard(this);
	}

	public void addBPELImplementation() {
		addBPELImplementation(composite);
	}

	public void addBPELImplementation(EditPart editPart) {
		getPalette().activateTool(TOOL_BPEL);
		editPart.click();
	}

	public BPMServiceWizard addBPMNImplementation() {
		return addBPMNImplementation(composite);
	}

	public BPMServiceWizard addBPMNImplementation(EditPart editPart) {
		addTool(TOOL_BPMN, editPart);
		return new BPMServiceWizard();
	}

	public DroolsServiceWizard addDroolsImplementation() {
		return addDroolsImplementation(composite);
	}

	public DroolsServiceWizard addDroolsImplementation(EditPart editPart) {
		addTool(TOOL_RULES, editPart);
		return new DroolsServiceWizard();
	}

	protected void addTool(String tool, EditPart editPart) {
		getPalette().activateTool(tool);
		editPart.click();
	}
	
	public CompositePropertiesPage showProperties() {
		getComposite().getContextButton("Properties").click();
		return new CompositePropertiesPage("");
	}
	
	public String xpath(String expr) throws FileNotFoundException {
		XPathEvaluator xpath = new XPathEvaluator(new FileReader(getSourceFile()));
		String result = xpath.evaluateString(expr);
		return result;
	}

	public File getSourceFile() {
		if (sourceFile == null) {
			IEditorInput editorInput = editorPart.getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
				sourceFile = fileEditorInput.getPath().toFile();
			}
		}
		return sourceFile;
	}
	
	public String getSource() throws IOException {
		StringBuffer source = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(getSourceFile()));
		String line = null;
		while ((line = in.readLine()) != null) {
			source.append(line).append("\n");
		}
		in.close();
		return source.toString();
	}
	
	private void doSave() {
		log.info("Save SwitchYard editor");
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		
		remainedShell = null;
		ShellHandler.getInstance().closeAllNonWorbenchShells(new IBeforeShellIsClosed() {
			
			@Override
			public void runBeforeShellIsClosed(Shell shell) {
				remainedShell = shell;
			}
		});
		
		super.save();
		
		new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
		
		if (remainedShell != null) {
			Assert.fail("Shell '" + remainedShell.getText() + "' remains open");
		}
	}

	@Override
	public void save() {
		new WaitUntil(new EditorIsSaved(), TimePeriod.VERY_LONG);
	}

	public void saveAndClose() {
		save();
		close();
	}
	
	private class EditorIsSaved implements WaitCondition {

		@Override
		public boolean test() {
			new SwitchYardEditor().doSave();
			return !new SwitchYardEditor().isDirty();
		}

		@Override
		public String description() {
			return "Editor is still dirty";
		}
		
	}
}
