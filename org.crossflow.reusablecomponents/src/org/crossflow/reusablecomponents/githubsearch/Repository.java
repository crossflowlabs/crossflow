package org.crossflow.reusablecomponents.githubsearch;

import org.crossflow.runtime.Job;

public class Repository extends Job {

	protected String fullName;
	protected String htmlUrl;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}

	@Override
	public String toString() {
		return "Techs (" + " fullName=" + fullName + " htmlUrl=" + htmlUrl + " jobId=" + jobId + " correlationId="
				+ correlationId + " destination=" + destination + ")";
	}

}