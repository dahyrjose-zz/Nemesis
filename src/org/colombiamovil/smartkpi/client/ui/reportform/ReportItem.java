package org.colombiamovil.smartkpi.client.ui.reportform;

import java.io.Serializable;

public class ReportItem implements Serializable {

	private static final long serialVersionUID = 8704768717874032570L;
	private String code;
	private String name;
	private String title;

	public ReportItem() {
	}

	public ReportItem(String code, String name) {
		setCode(code);
		setName(name);
	}

	public ReportItem(String code, String name, String title) {
		setCode(code);
		setName(name);
		setTitle(title);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
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

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "\n\t>> " + name + " - " + title;
	}
}