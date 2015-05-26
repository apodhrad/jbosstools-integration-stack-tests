package org.jboss.tools.runtime.reddeer.impl;

import java.io.IOException;
import java.net.ServerSocket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.reddeer.eclipse.jdt.ui.WorkbenchPreferenceDialog;
import org.jboss.reddeer.eclipse.wst.server.ui.RuntimePreferencePage;
import org.jboss.tools.runtime.reddeer.Namespaces;
import org.jboss.tools.runtime.reddeer.ServerBase;
import org.jboss.tools.runtime.reddeer.wizard.ServerRuntimeWizard;
import org.jboss.tools.runtime.reddeer.wizard.ServerWizard;

/**
 * AS Server
 * 
 * @author apodhrad
 * 
 */
@XmlRootElement(name = "as", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerAS extends ServerBase {

	private final String category = "JBoss Community";

	private final String label = "JBoss AS";
	
	public String getCategory() {
		return category;
	}

	public String getServerType() {
		return label + " " + getVersion();
	}

	public String getRuntimeType() {
		return "JBoss " + getVersion() + " Runtime";
	}

	@Override
	public void create() {
		addJre();
		
		WorkbenchPreferenceDialog preferences = new WorkbenchPreferenceDialog();
		preferences.open();
		
		// Add runtime
		RuntimePreferencePage runtimePreferencePage = new RuntimePreferencePage();
		preferences.select(runtimePreferencePage);
		runtimePreferencePage.addRuntime();
		ServerRuntimeWizard runtimeWizard = new ServerRuntimeWizard();
		runtimeWizard.activate();
		runtimeWizard.setType(getCategory(), getRuntimeType());
		runtimeWizard.next();
		runtimeWizard.setName(name);
		runtimeWizard.setHomeDirectory(getHome());
		runtimeWizard.selectJre(getJreName());
		runtimeWizard.finish();
		runtimePreferencePage.ok();

		// Add server
		ServerWizard serverWizard = new ServerWizard();
		serverWizard.open();
		serverWizard.setType(getCategory(), getServerType());
		serverWizard.setName(name);
		serverWizard.next();
		serverWizard.setRuntime(name);
		serverWizard.finish();
	}

	@Override
	protected boolean canStart() {
		int port = 8080;
		try {
			new ServerSocket(port).close();
			return true;
		} catch (IOException e) {
			throw new RuntimeException("Port '" + port + "' is already in use!", e);
		}
	}
	
	
}
