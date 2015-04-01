package org.jboss.tools.switchyard.reddeer.editor;

import java.util.List;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.wait.AbstractWait;
import org.jboss.reddeer.swt.wait.TimePeriod;

/**
 * 
 * @author apodhrad
 * 
 */
public class SimpleTextEditor {

	private SWTBotEclipseEditor editor;
	private static SWTWorkbenchBot bot = new SWTWorkbenchBot(); 

	public SimpleTextEditor(String fileName) {
		AbstractWait.sleep(TimePeriod.SHORT);
		editor = bot.editorByTitle(fileName).toTextEditor();
		editor.show();
		editor.setFocus();
	}

	public SimpleTextEditor type(String text) {
		AbstractWait.sleep(TimePeriod.SHORT);
		editor.typeText(text);
		editor.save();
		return this;
	}

	public SimpleTextEditor typeAfter(String word, String text) {
		editor.navigateTo(getLineNum(word) + 1, 0);
		return type(text);
	}

	public SimpleTextEditor typeBefore(String word, String text) {
		editor.navigateTo(getLineNum(word) - 1, 0);
		return type(text);
	}

	public SimpleTextEditor deleteLine(int lineNum) {
		editor.selectLine(lineNum);
		editor.pressShortcut(Keystrokes.DELETE);
		editor.save();
		return this;
	}

	public SimpleTextEditor deleteLineWith(String word) {
		return deleteLine(getLineNum(word));
	}

	public SimpleTextEditor newLine() {
		return type("\n");
	}

	public void saveAndClose() {
		formatText();
		editor.saveAndClose();
	}

	public SimpleTextEditor formatText() {
		AbstractWait.sleep(TimePeriod.SHORT);
		try {
			editor.pressShortcut(Keystrokes.CTRL, Keystrokes.SHIFT, KeyStroke.getInstance("O"));
			editor.pressShortcut(Keystrokes.CTRL, Keystrokes.SHIFT, KeyStroke.getInstance("F"));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("Shortcut exception");
		}
		editor.save();
		return this;
	}

	public int getLineNum(String word) {
		List<String> lines = editor.getLines();
		int lineNum = 0;
		for (String line : lines) {
			if (line.contains(word)) {
				return lineNum;
			}
			lineNum++;
		}
		throw new RuntimeException("Cannot find line with '" + word + "'");
	}
	
	/**
	 * Generate getters and setters to all attributes
	 * @param firstAttribute name of first attribute
	 */
	public void generateGettersSetters(String firstAttribute){
		editor.navigateTo(getLineNum(firstAttribute),0);
		new ShellMenu("Source", "Generate Getters and Setters...").select();
		AbstractWait.sleep(TimePeriod.SHORT);
		new PushButton("Select All").click();
		new PushButton("OK").click();
		editor.save();
	}
}
