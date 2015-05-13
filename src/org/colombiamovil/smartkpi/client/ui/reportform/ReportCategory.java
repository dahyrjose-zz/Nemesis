package org.colombiamovil.smartkpi.client.ui.reportform;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ReportCategory implements Serializable {

	private static final long serialVersionUID = 8704768717874032570L;
	private String name;
	private String title;
	private String code;
	private boolean reactive;
	private List<ReportCategory> subCategories;
	private List<ReportItem> items;

	public ReportCategory() {
		setReactive(false);
	}

	public ReportCategory(String name, String title) {
		setName(name);
		setTitle(title);
		setReactive(false);
	}

	public ReportCategory(String name, String title, String code, boolean reactive) {
		setName(name);
		setTitle(title);
		setCode(code);
		setReactive(reactive);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String title) {
		this.name = title;
	}

	public void addSubCategories(ReportCategory category) {
		if(subCategories == null) {
			subCategories = new LinkedList<ReportCategory>();
		}
		subCategories.add(category);
	}

	public List<ReportCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<ReportCategory> subCategories) {
		this.subCategories = subCategories;
	}

	public void addItems(ReportItem item) {
		if(items == null) {
			items = new LinkedList<ReportItem>();
		}
		items.add(item);
	}

	public List<ReportItem> getItems() {
		return items;
	}

	public void setItems(List<ReportItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "--> " + name + " -- Reactive: " + isReactive() + " -- " + title + (subCategories != null ? " " + subCategories : "") + (items != null ? " == " + items : "") + "\n";
	}

	public boolean isReactive() {
		return reactive;
	}

	public void setReactive(boolean reactive) {
		this.reactive = reactive;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}