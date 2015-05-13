package org.colombiamovil.smartkpi.client.alarms;

import java.io.Serializable;

public class CfgAlarm implements Serializable {

	private static final long serialVersionUID = -5217318193133933598L;
	private String alarmId, title, indicator, threshold, constraints, period, objectType;

	public CfgAlarm() {
	}

	public CfgAlarm(String alarmId, String title, String indicator, String threshold, String constraints, String period, String objectType) {
		setAlarmId(alarmId);
		setTitle(title);
		setIndicator(indicator);
		setThreshold(threshold);
		setConstraints(constraints);
		setPeriod(period);
		setObjectType(objectType);
	}

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = !objectType.startsWith("nms_") ? objectType : "nms_" + objectType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIndicator() {
		return indicator;
	}

	public void setIndicator(String indicator) {
		this.indicator = indicator.replace("1*", "").replace("\n", " ");
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold.replace("1*", "").replace("\n", " ");
	}

	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints == null ? "No Constraints" : constraints.replace("o.", "").replace("\n", " ");
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period == null ? "Every Hour" : 
			period.equals("") ? "Every Hour" :
			period.equals("d") ? "Every Day" : 
			period.equals("w") ? "Every Week" : 
			period.equals("m") ? "Every Month" : "Undefined";
	}
}