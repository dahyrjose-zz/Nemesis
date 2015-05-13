package org.colombiamovil.smartkpi.client.ui.charts;

import java.io.Serializable;

public class ControllerConfigBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int confIndex, confOrder;
	private String confHeader, objectType, iniDate, endDate, kpis, objects, tableType;

	public ControllerConfigBean() {	
	}

	public ControllerConfigBean(int confIndex, int confOrder, String confHeader, String objectType, String iniDate, String endDate, String kpis, String objects, String tableType) {
		this.confIndex = confIndex;
		this.confOrder = confOrder;
		this.confHeader = confHeader;
		this.objectType = objectType;
		this.iniDate = iniDate;
		this.endDate = endDate;
		this.kpis = kpis;
		this.objects = objects;
		this.tableType = tableType;
	}

	/**
	 * @param confHeader the confHeader to set
	 */
	public void setConfHeader(String confHeader) {
		this.confHeader = confHeader;
	}

	/**
	 * @return the confHeader
	 */
	public String getConfHeader() {
		return confHeader;
	}

	public int getConfIndex() {
		return confIndex;
	}
	public void setConfIndex(int confIndex) {
		this.confIndex = confIndex;
	}
	public int getConfOrder() {
		return confOrder;
	}
	public void setConfOrder(int confOrder) {
		this.confOrder = confOrder;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getIniDate() {
		return iniDate;
	}
	public void setIniDate(String iniDate) {
		this.iniDate = iniDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getKpis() {
		return kpis;
	}
	public void setKpis(String kpis) {
		this.kpis = kpis;
	}
	public String getObjects() {
		return objects;
	}
	public void setObjects(String objects) {
		this.objects = objects;
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String toString() {
		return confHeader;
	}
}
