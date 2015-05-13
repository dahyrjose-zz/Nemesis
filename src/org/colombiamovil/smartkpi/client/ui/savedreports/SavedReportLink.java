package org.colombiamovil.smartkpi.client.ui.savedreports;

import com.google.gwt.user.client.ui.Label;

public class SavedReportLink extends Label {

	private SavedReportBean bean;
	public SavedReportLink(String label, SavedReportBean bean) {
		super(label);
		setBean(bean);
	}

	public void setBean(SavedReportBean bean) {
		this.bean = bean;
	}

	public SavedReportBean getBean() {
		return bean;
	}
}
