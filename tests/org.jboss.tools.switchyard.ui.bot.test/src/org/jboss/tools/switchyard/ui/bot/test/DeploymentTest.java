package org.jboss.tools.switchyard.ui.bot.test;

import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.tools.switchyard.ui.bot.test.requirement.ServerRequirement;
import org.jboss.tools.switchyard.ui.bot.test.requirement.ServerRequirement.SYServer;
import org.junit.Test;
import org.junit.runner.RunWith;

@CleanWorkspace
@SYServer(state = ServerReqState.PRESENT)
@RunWith(RedDeerSuite.class)
public class DeploymentTest {

	@InjectRequirement
	private ServerRequirement serverRequirement;
	
	@Test
	public void deploymentTest() {
		System.out.println("deploymentTest");
		System.out.println("version" + serverRequirement.getConfig().getServerFamily().getVersion());
	}
}
