package org.colombiamovil.smartkpi.client.alarms;

import java.io.Serializable;

public class HistoryAlarm implements Serializable {

	private static final long serialVersionUID = 4829501400210906496L;
	private String date, time, element, value, code, objectType;

	public HistoryAlarm() {
	}

	public HistoryAlarm(String date, String time, String element, String value, String code, String objectType) {
		this.date = date;
		this.time = time;
		this.element = element;
		this.value = value;
		this.code = code;
		this.objectType = objectType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
