package org.colombiamovil.smartkpi.client.ui.reportform;

import com.google.gwt.user.client.ui.TreeItem;

public class ReportTreeItem extends TreeItem {

	private ReportItem item;
	public ReportTreeItem(ReportItem item) {
		super();
		this.setItem(item);
	}

	public void setItem(ReportItem item) {
		this.item = item;
		setText(item.getName());
		setTitle(item.getTitle());
	}

	public ReportItem getItem() {
		return item;
	}

	public String getCode() {
		return item.getCode();
	}
}
