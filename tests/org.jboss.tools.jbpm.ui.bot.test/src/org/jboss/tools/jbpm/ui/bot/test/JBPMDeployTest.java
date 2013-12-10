/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jbpm.ui.bot.test;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.junit.logging.Logger;
import org.jboss.reddeer.swt.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.wait.AbstractWait;
import org.jboss.reddeer.swt.wait.WaitWhile;
import org.jboss.tools.jbpm.ui.bot.test.editor.JBPMEditor;
import org.jboss.tools.jbpm.ui.bot.test.suite.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.tools.jbpm.ui.bot.test.suite.JBPMRequirement.JBPM;
import org.jboss.tools.jbpm.ui.bot.test.suite.JBPMSuite;
import org.jboss.tools.jbpm.ui.bot.test.suite.PerspectiveRequirement.Perspective;
import org.jboss.tools.jbpm.ui.bot.test.suite.ServerRequirement.Server;
import org.jboss.tools.jbpm.ui.bot.test.suite.ServerRequirement.State;
import org.jboss.tools.jbpm.ui.bot.test.suite.ServerRequirement.Type;
import org.jboss.tools.jbpm.ui.bot.test.wizard.JBPMProjectWizard;
import org.junit.BeforeClass;
import org.junit.Test;

@JBPM
@CleanWorkspace
@Perspective(name = "jBPM jPDL 3")
@Server(type = Type.ALL, state = State.RUNNING)
public class JBPMDeployTest extends SWTBotTestCase {

	public static final String PROJECT = "deploytest";
	
	protected static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    private static final Logger log = Logger.getLogger(JBPMDeployTest.class);
    
	@BeforeClass
	public static void createProject() {
		/* Get Focus */
		hack_MacOSX_ReturnFocus();
		
		/* Create jBPM3 Project */
		JBPMProjectWizard projectWizard = new JBPMProjectWizard();
		projectWizard.open();
		projectWizard.setName(PROJECT).next();
		projectWizard.setRuntime(JBPMSuite.getJBPMRuntime()).finish();
	}

	@Test
	public void deployTest() {
		/* Open Simple Diagram */
		new ProjectExplorer().getProject(PROJECT).getProjectItem("src", "main", "jpdl", "simple.jpdl.xml").open();

		// Deploy
		JBPMEditor editor = new JBPMEditor("simple");
		SWTBotMultiPageEditor multi = new SWTBotMultiPageEditor(editor.getReference(), bot);
		multi.activatePage("Deployment");

		new LabeledText("Server Name:").setText("127.0.0.1");
		new LabeledText("Server Deployer:").setText("gpd-deployer/upload");
		editor.save();

		editor.show();
		editor.setFocus();
		new CheckBox("Use credentials").toggle(true);
		new LabeledText("Username:").setText("admin");
		new LabeledText("Password:").setText("admin");

		editor.show();
		editor.setFocus();
		new ShellMenu("jBPM", "Ping Server").select();

		// Confirm ping message dialog
		new DefaultShell("Ping Server Successful");
		new PushButton("OK").click();
		new WaitWhile(new ShellWithTextIsAvailable("Ping Server Successful"));
		
		// Deploy
		editor.show();
		editor.setFocus();
		new ShellMenu("jBPM", "Deploy Process").select();

		// Confirm deployed message dialog
		new DefaultShell("Deployment Successful");
		new PushButton("OK").click();
		new WaitWhile(new ShellWithTextIsAvailable("Deployment Successful"));

		// TODO - check via jpdl console
	}
	
	/**
     * Hack for Mac OS X
     * Returns focus from Apache.karaf.main back to IDE (after starting server)
     */
    private static void hack_MacOSX_ReturnFocus() {
    	if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
    		try {
    			Robot robot = new Robot();
                robot.mouseMove(200, 200);
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                AbstractWait.sleep(1000);
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                } catch (AWTException e) {
                	log.error("Error during click into IDE (OS X)", e);
                }
            }
    }
}
