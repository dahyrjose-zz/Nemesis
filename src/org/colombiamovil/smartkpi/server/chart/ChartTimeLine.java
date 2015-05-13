package org.colombiamovil.smartkpi.server.chart;

public class ChartTimeLine {

	private String lineName;
	private double value;
	private String annotTitle = null;
	private String annotBody = null;

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String indicator) {
		this.lineName = indicator;
	}

	public String getAnnotTitle() {
		return annotTitle;
	}

	public void setAnnotTitle(String annotTitle) {
		this.annotTitle = annotTitle;
	}

	public String getAnnotBody() {
		return annotBody;
	}

	public void setAnnotBody(String annotBody) {
		this.annotBody = annotBody;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getJsonArray();
	}

	public String getJsonArray() {
		return "{v:"+value+"}" + "," + (annotTitle != null ? "{v:'"+annotTitle+"'}" : "") + "," + (annotBody != null ? "{v:'"+annotBody+"'}" : "");
	}

	public String getJsonArrayToAppend() {
		return value + "," + (annotTitle != null ? "'"+annotTitle+"'" : "null") + "," + (annotBody != null ? "'"+annotBody+"'" : "null");
	}
}
