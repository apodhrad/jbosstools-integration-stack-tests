package org.jboss.tools.switchyard.reddeer.debug;

import org.jboss.reddeer.swt.condition.WaitCondition;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.swt.wait.TimePeriod;
import org.jboss.reddeer.swt.wait.WaitUntil;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;

public class VariablesView extends WorkbenchView {

	public VariablesView() {
		super("Variables");
	}

	public String getValue(final String... variablePath) {
		open();
		new DefaultTreeItem(variablePath).select();
		new WaitUntil(new WaitCondition() {

			@Override
			public boolean test() {
				return new DefaultTreeItem(variablePath).isSelected();
			}

			@Override
			public String description() {
				return "Variable is not selected";
			}
		}, TimePeriod.NORMAL);
		new WaitUntil(new WaitCondition() {

			@Override
			public boolean test() {
				String text = new DefaultStyledText().getText();
				return text != null && text.trim().length() > 0;
			}

			@Override
			public String description() {
				return "Variable doesn't contain any text";
			}
		}, TimePeriod.NORMAL, false);
		return new DefaultStyledText().getText();
	}
}
