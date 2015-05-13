package org.colombiamovil.smartkpi.client.ui.charts.fusion;

import com.google.gwt.core.client.JavaScriptObject;

public final class FusionChartJS extends JavaScriptObject {

	protected FusionChartJS() {

	}

	public static native FusionChartJS getAreaInstance(String width, String height) /*-{
		var chart = new $wnd.FusionCharts( "Charts/FCF_Area2D.swf", "totalChart", width, height, "0", "0" );
		return chart;
	}-*/;

	public static native FusionChartJS getMSLineInstance(String width, String height) /*-{
		var chart = new $wnd.FusionCharts( "Charts/FCF_MSLine.swf", "totalChart", width, height, "0", "0" );
		return chart;
	}-*/;

	public static native FusionChartJS getStackedColumn2DInstance(String width, String height) /*-{
		var chart = new $wnd.FusionCharts( "Charts/FCF_StackedColumn2D.swf", "totalChart", width, height, "0", "0" );
		return chart;
	}-*/;

	public native void setDataUrl(String url) /*-{
		this.setDataURL( escape(url) );
	}-*/;

	public native void render(String divId) /*-{
		this.render( divId );
	}-*/;
}