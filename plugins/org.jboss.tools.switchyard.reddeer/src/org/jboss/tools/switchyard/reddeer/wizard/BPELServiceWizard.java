package org.jboss.tools.switchyard.reddeer.wizard;

import org.jboss.reddeer.gef.condition.EditorHasEditParts;
import org.jboss.reddeer.gef.editor.GEFEditor;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.swt.wait.WaitUntil;

/**
 * 
 * @author apodhrad
 * 
 */
public class BPELServiceWizard extends ServiceWizard<BPELServiceWizard> {

	public static final String DIALOG_TITLE = "New File";
	public static final String NAMESPACE = "Namespace:";

	public static final String FILE = "Name:";
	public static final String FILE_NAME = "File name:";

	private GEFEditor editor;

	public BPELServiceWizard() {
		super(DIALOG_TITLE);
	}

	public BPELServiceWizard(GEFEditor editor) {
		super(DIALOG_TITLE);
		this.editor = editor;
	}

	public BPELServiceWizard setFileName(String name) {
		new LabeledText(FILE_NAME).setText(name);
		return this;
	}

	public BPELServiceWizard setProcessName(String name) {
		new LabeledText(FILE).setText(name);
		return this;
	}

	public BPELServiceWizard setProcessNamespace(String name) {
		new LabeledText(NAMESPACE).setText(name);
		return this;
	}

	public BPELServiceWizard selectResource(String... path) {
		new DefaultTreeItem(path).select();
		return this;
	}

	@Override
	protected void browse() {
		new PushButton("Browse...").click();
	}

	@Override
	public void finish() {
		int oldCount = editor.getNumberOfEditParts();
		activate();
		super.finish();
		new WaitUntil(new EditorHasEditParts(editor, oldCount));
		editor.save();
	}

}
