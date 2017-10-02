package org.jboss.tools.switchyard.ui.bot.test.condition;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.junit.execution.TestMethodShouldRun;
import org.jboss.tools.switchyard.ui.bot.test.util.JiraClient;
import org.junit.runners.model.FrameworkMethod;

/**
 * 
 * @author apodhrad
 *
 */
public class IssueIsClosed implements TestMethodShouldRun {

	private Logger log = new Logger(IssueIsClosed.class);

	@Override
	public boolean shouldRun(FrameworkMethod method) {
		boolean skipUnfixedIssues = Boolean.valueOf(System.getProperty("reddeer.skipUnfixedIssues", "false"));
		if (!skipUnfixedIssues) {
			return true;
		}
		JiraClient jiraClient = new JiraClient();
		for (Annotation annotation : method.getMethod().getDeclaredAnnotations()) {
			if (annotation instanceof Jira) {
				String issueId = ((Jira) annotation).value();
				if (!jiraClient.isIssueClosed(issueId)) {
					log.info("Issue '" + issueId + "' is still open, skipping test '" + method.getName() + "'");
					return false;
				}
			}
		}
		return true;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Jira {

		String value();

	}

}
