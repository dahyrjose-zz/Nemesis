package org.colombiamovil.smartkpi.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;

public class QuickChart extends Frame {

	public QuickChart() {
		super("http://10.65.35.209:8080/InfoSeeCharts/ChartBuilder?ini=20090101&end=20090201&elem=route&dataidfld=id&objtbl=route&objidfld=id&" +
				"elemName=concat%28route%2C%27%7C%27%2Cmsc%29&elemAlias=route_msc&objs=DBOG01%7CCAL%2CYMED01%7CMSXMED1&kpis=carried_traffic&" +
				"rtype=bh&annot=false&submit=Report%21");
		DOM.setElementAttribute(getElement(), "frameBorder", "0");
		DOM.setElementAttribute(getElement(), "marginWidth", "10");
		DOM.setElementAttribute(getElement(), "marginHeight", "10");
		DOM.setElementAttribute(getElement(), "scrolling", "no");
	}

	public void alterChart(String iniDate, String elem, String objs, String kpis) {
		String url = "http://10.65.35.209:8080/InfoSeeCharts/ChartBuilder?ini=date_sub("+iniDate+", interval 3 month)&end="+iniDate+
			"&elem="+elem.split("_")[1]+"&dataidfld=id&objtbl="+elem.split("_")[1]+"&objidfld=id&elemName="+elem.split("_")[1]+
			"&elemAlias="+elem.split("_")[1]+"&objs="+objs+"&kpis="+kpis+"" +
			"&rtype=bh&annot=false";
		setUrl(url);
	}

	public void alterChart(String iniDate, String endDate, String elem, String objs, String kpis) {
		String url = "http://10.65.35.209:8080/InfoSeeCharts/ChartBuilder?ini="+iniDate+"&end="+endDate+
			"&elem="+elem.split("_")[1]+"&dataidfld=id&objtbl="+elem.split("_")[1]+"&objidfld=id&elemName="+elem.split("_")[1]+
			"&elemAlias="+elem.split("_")[1]+"&objs="+objs+"&kpis="+kpis+"" +
			"&rtype=bh&annot=false";
		setUrl(url);
	}
}
