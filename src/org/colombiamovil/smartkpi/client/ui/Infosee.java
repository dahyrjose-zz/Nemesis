package org.colombiamovil.smartkpi.client.ui;

import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.GeoMap;
import com.google.gwt.visualization.client.visualizations.GeoMap.DataMode;


public class Infosee extends VerticalPanel {
	public Infosee() {
		this.setSize("100%", "100%");
//		Maps.loadMapsApi(
//				"ABQIAAAAuha8Z5nTjBHF02AxxmHH6xT2yXp_ZAY8_ufC3CFXhHIE1NvwkxSeGs18fsZU9MDw5K2Zdupxs5JDxg",
//				"2", false, new Runnable() {
//					public void run() {
//						buildUi();
//					}
//				});
//	 buildUi();
		//this.add();
		//new JavaScriptObject
	}

	private void buildUi() {
		GeoMap.Options options = GeoMap.Options.create();
		options.setDataMode(DataMode.REGIONS);
		options.setHeight(600);
		options.setWidth(450);
		options.setShowLegend(true);
		options.setRegion("world");

		DataTable data = createDataTable();
		while(!Maps.isLoaded())
		{
			System.out.println("loading maps ... ");
		}
        GeoMap geoMap = new GeoMap(data, options);
        this.add(geoMap);
        
	}

	private DataTable createDataTable() {
		// Create a simple data table
		DataTable dataTable = DataTable.create();
		dataTable.addRows(7);
		dataTable.addColumn(ColumnType.STRING, "ADDRESS", "address");
		dataTable.addColumn(ColumnType.NUMBER, "SITES", "color");
		dataTable.setValue(0, 0, "Israel");
		dataTable.setValue(0, 1, 1);
		dataTable.setValue(1, 0, "United States");
		dataTable.setValue(0, 1, 2);
		dataTable.setValue(2, 0, "CH");
		dataTable.setValue(0, 1, 3);
		return dataTable;
	}
}
