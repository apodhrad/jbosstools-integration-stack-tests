package org.jboss.tools.drools.reddeer.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.core.exception.CoreLayerException;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.link.DefaultLink;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.workbench.impl.view.WorkbenchView;
import org.jboss.tools.drools.reddeer.kienavigator.item.SpaceItem;
import org.jboss.tools.drools.reddeer.kienavigator.item.ProjectItem;
import org.jboss.tools.drools.reddeer.kienavigator.item.RepositoryItem;
import org.jboss.tools.drools.reddeer.kienavigator.item.ServerItem;

public class KieNavigatorView extends WorkbenchView {

	private static final String NO_SERVERS_TEXT = "Use the Servers View to create a new server...";

	public KieNavigatorView() {
		super("Drools", "Kie Navigator");
	}

	/**
	 * Opens Kie Navigator if it is not opened.
	 */
	private void checkAndOpen() {
		if (!isOpen()) {
			open();
		}
	}

	/**
	 * Checks if Kie Navigator contains any server.
	 * 
	 * @return false if no servers are available; true otherwise
	 */
	private boolean isEmpty() {
		checkAndOpen();
		return isLinkToServersViewExists();
	}
	
	/**
	 * Checks if the link to create a new server exists.
	 * @return true if exists, false otherwise
	 */
	public boolean isLinkToServersViewExists() {
		try {
			new DefaultLink(NO_SERVERS_TEXT);
		} catch (CoreLayerException e) {
			return false;
		}
		return true;
	}

	/**
	 * Refreshes Kie Navigator.
	 */
	public void refresh() {
		checkAndOpen();
		new ContextMenuItem("Refresh");
	}

	/**
	 * Returns list of server placed in Kie Navigator.
	 * 
	 * @return List of Kie Navigator servers.
	 */
	public List<ServerItem> getServers() {
		checkAndOpen();
		List<ServerItem> serversList = new ArrayList<ServerItem>();
		if (!isEmpty()) {
			for (TreeItem item : new DefaultTree().getItems()) {
				serversList.add(new ServerItem(item));
			}
		}
		return serversList;
	}

	/**
	 * Returns server item with specified name.
	 * 
	 * @param name
	 *            Server name.
	 * @return Server item with specified name.
	 */
	public ServerItem getServer(String name) {
		checkAndOpen();
		if (!isEmpty()) {
			for (TreeItem item : new DefaultTree().getItems()) {
				if (item.getText().equals(name)) {
					return new ServerItem(item);
				}
			}
		}
		throw new IllegalArgumentException("No such server: " + name);
	}

	public ServerItem getServer(int number) {
		checkAndOpen();
		if (!isEmpty()) {
			List<TreeItem> items = new DefaultTree().getItems();
			if (number <= items.size()) {
				return new ServerItem(items.get(number));
			}
		}
		throw new IllegalArgumentException("No such server: " + number);
	}

	public SpaceItem getSpace(int serverNumber, String space) {
		return getServer(serverNumber).getSpace(space);
	}

	public RepositoryItem getRepository(int serverNumber, String space, String repoName) {
		RepositoryItem ri = getSpace(serverNumber, space).getRepository(repoName);
		ri.importRepository();
		return getSpace(serverNumber, space).getRepository(repoName);
	}

	public ProjectItem getProject(int serverNumber, String space, String repoName, String projectName) {
		return getRepository(serverNumber, space, repoName).getProject(projectName);
	}
}
