package org.jboss.tools.switchyard.reddeer.condition;

import org.hamcrest.Matcher;
import org.jboss.reddeer.swt.api.TreeItem;
import org.jboss.reddeer.swt.condition.WaitCondition;
import org.jboss.reddeer.swt.matcher.RegexMatcher;

/**
 * Returns true if a given tree item has any text or if its text matches the specified regular expression.
 * 
 * @author apodhrad
 *
 */
public class TreeItemHasText implements WaitCondition {

	private TreeItem treeItem;
	private String text;
	private Matcher<?> matcher;

	public TreeItemHasText(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

	public TreeItemHasText(TreeItem treeItem, String regex) {
		this.treeItem = treeItem;
		this.matcher = new RegexMatcher(regex);
	}

	@Override
	public boolean test() {
		text = treeItem.getText();
		if (matcher != null) {
			return matcher.matches(text);
		}
		return text != null && text.trim().length() > 0;
	}

	@Override
	public String description() {
		return "Tree item has text '" + text + "'";
	}

}
