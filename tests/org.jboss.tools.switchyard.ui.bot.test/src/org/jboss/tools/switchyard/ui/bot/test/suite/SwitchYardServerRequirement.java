package org.jboss.tools.switchyard.ui.bot.test.suite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.reddeer.junit.requirement.PropertyConfiguration;
import org.jboss.reddeer.junit.requirement.Requirement;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.tools.switchyard.ui.bot.test.suite.SwitchYardServerRequirement.SwitchYardServer;

/**
 * 
 * @author apodhrad
 * 
 */
public class SwitchYardServerRequirement implements Requirement<SwitchYardServer>, PropertyConfiguration {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SwitchYardServer {
		ServerReqState state() default ServerReqState.RUNNING;
	}

	private String version;
	private String runtime;

	@Override
	public boolean canFulfill() {
		System.out.println("caFulfill?");
		return true;
	}

	@Override
	public void fulfill() {
		System.out.println("fulfill");
	}

	@Override
	public void setDeclaration(SwitchYardServer declaration) {
		System.out.println("setDeclaration");
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRuntime() {
		return runtime;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

}
