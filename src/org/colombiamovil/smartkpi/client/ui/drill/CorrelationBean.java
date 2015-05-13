package org.colombiamovil.smartkpi.client.ui.drill;

import java.io.Serializable;

public class CorrelationBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String xObject, yObject;
	private Double correlationRatio, weight, sumX, sumY, sumXY, sumX2, sumY2;

	public CorrelationBean() {
	}

	public CorrelationBean(String xObject, String yObject, Double correlationRatio, Double weight, 
			Double sumX, Double sumY, Double sumXY, Double sumX2, Double sumY2) {
		this.xObject = xObject;
		this.yObject = yObject;
		this.correlationRatio = correlationRatio;
		this.weight = weight;
		this.sumX = sumX;
		this.sumY = sumY;
		this.sumXY = sumXY;
		this.sumX2 = sumX2;
		this.sumY2 = sumY2;
	}

	public String getXObject() {
		return xObject;
	}

	public void setXObject(String object) {
		xObject = object;
	}

	public String getYObject() {
		return yObject;
	}

	public void setYObject(String object) {
		yObject = object;
	}

	public Double getCorrelationRatio() {
		return correlationRatio;
	}

	public void setCorrelationRatio(Double correlationRatio) {
		this.correlationRatio = correlationRatio;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getSumX() {
		return sumX;
	}

	public void setSumX(Double sumX) {
		this.sumX = sumX;
	}

	public Double getSumY() {
		return sumY;
	}

	public void setSumY(Double sumY) {
		this.sumY = sumY;
	}

	public Double getSumXY() {
		return sumXY;
	}

	public void setSumXY(Double sumXY) {
		this.sumXY = sumXY;
	}

	public Double getSumX2() {
		return sumX2;
	}

	public void setSumX2(Double sumX2) {
		this.sumX2 = sumX2;
	}

	public Double getSumY2() {
		return sumY2;
	}

	public void setSumY2(Double sumY2) {
		this.sumY2 = sumY2;
	}

	public String toString() {
		return "\nxObject: "+ xObject + " yObject: "+ yObject + " correlationRatio: "+ correlationRatio + " weight: "+ weight + 
				" sumX: "+ sumX + " sumY: "+ sumY + " sumXY: "+ sumXY + " sumX2: "+ sumX2 + " sumY2: "+ sumY2;
	}
}
