package org.colombiamovil.smartkpi.client.ui.savedreports;

import java.io.Serializable;

public class SavedReportBean implements Serializable {

	private static final long serialVersionUID = 4662364849679597351L;
	private String itemLabel, typeDescription, repOwner, objectType, tableType, iniDate, endDate, objects, kpis, creationDate, iniFixDate, endFixDate;
	private boolean relative;

	public SavedReportBean() {
	}

	public SavedReportBean(String itemLabel, String typeDescription, String repOwner, String objectType, String tableType, 
			String iniDate, String endDate, String objects, String kpis, String creationDate, String iniFixDate, String endFixDate) {
		setItemLabel(itemLabel);
		setTypeDescription(typeDescription);
		setRepOwner(repOwner);
		setObjectType(objectType);
		setTableType(tableType);
		setIniDate(iniDate);
		setEndDate(endDate);
		setObjects(objects);
		setKpis(kpis);
		setCreationDate(creationDate);
		setIniFixDate(iniFixDate);
		setEndFixDate(endFixDate);
	}

	public String getItemLabel() {
		return itemLabel;
	}

	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public String getRepOwner() {
		return repOwner;
	}

	public void setRepOwner(String repOwner) {
		this.repOwner = repOwner;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String dataTable) {
		this.objectType = dataTable;
	}

	public void setTableType(String reportType) {
		this.tableType = reportType;
	}

	public String getTableType() {
		return tableType;
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

	public String getObjects() {
		return objects;
	}

	public void setObjects(String objects) {
		this.objects = objects;
	}

	public String getKpis() {
		return kpis;
	}

	public void setKpis(String kpis) {
		this.kpis = kpis;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @param iniFixDate the iniFixDate to set
	 */
	public void setIniFixDate(String iniFixDate) {
		this.iniFixDate = iniFixDate;
	}

	/**
	 * @return the iniFixDate
	 */
	public String getIniFixDate() {
		return iniFixDate;
	}

	/**
	 * @param endFixDate the endFixDate to set
	 */
	public void setEndFixDate(String endFixDate) {
		this.endFixDate = endFixDate;
	}

	/**
	 * @return the endFixDate
	 */
	public String getEndFixDate() {
		return endFixDate;
	}

	/**
	 * @param relative the relative to set
	 */
	public void setRelative(boolean relative) {
		this.relative = relative;
	}

	/**
	 * @return the relative
	 */
	public boolean isRelative() {
		return relative;
	}
}
