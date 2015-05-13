package org.colombiamovil.smartkpi.client.ui.reportform;

import com.google.gwt.user.client.ui.TreeItem;

public class ReportTreeCategory extends TreeItem {

	private ReportCategory category;
	public ReportTreeCategory(ReportCategory category) {
		super();
		setCategory(category);
	}

	public ReportCategory getCategory() {
		return category;
	}

	public void setCategory(ReportCategory category) {
		this.category = category;
		setText(category.getName());
		setTitle(category.getTitle());
	}
}
