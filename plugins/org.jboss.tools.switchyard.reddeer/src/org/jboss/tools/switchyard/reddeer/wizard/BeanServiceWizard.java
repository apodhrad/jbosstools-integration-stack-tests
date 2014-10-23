package org.jboss.tools.switchyard.reddeer.wizard;

import org.jboss.reddeer.gef.condition.EditorHasEditParts;
import org.jboss.reddeer.gef.editor.GEFEditor;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.wait.WaitUntil;

/**
 * Wizard for creating a bean service.
 * 
 * @author apodhrad
 * 
 */
public class BeanServiceWizard extends ServiceWizard<BeanServiceWizard> {

	public static final String DIALOG_TITLE = "New Bean Service";

	private GEFEditor editor;

	public BeanServiceWizard(GEFEditor editor) {
		super(DIALOG_TITLE);
		this.editor = editor;
	}

	public BeanServiceWizard setName(String name) {
		activate();
		new LabeledText("Name:").setText(name);
		return this;
	}

	@Override
	protected void browse() {
		new PushButton(2).click();
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
