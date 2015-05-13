package org.colombiamovil.smartkpi.client.screener;

import java.io.Serializable;

public class KpiObjectValues implements Serializable {

	private static final long serialVersionUID = 8704768717874032570L;
	private String symbol;
	private String parentElement;
	private double[] kpiValues;

	public KpiObjectValues() {
	}

	public KpiObjectValues(String symbol, double[] kpiValues) {
		this.symbol = symbol;
		this.kpiValues = kpiValues;
	}

	public String getParentElement() {
		return parentElement;
	}

	public void setParentElement(String parentElement) {
		this.parentElement = parentElement;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public double[] getKpiValues() {
		return this.kpiValues;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setKpiValues(double[] kpiValues) {
		this.kpiValues = kpiValues;
	}
}