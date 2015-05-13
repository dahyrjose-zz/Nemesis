package org.colombiamovil.smartkpi.client.ui.charts;

import com.google.gwt.user.client.ui.FlexTable;

public class LineChartVisualizationContainer extends FlexTable
{
    public LineChartVisualizationContainer( )
    {
    	setStyleName("chartContainer");
        setWidth( "100%" );
    }

    public void loadJsonData( String objectType, String iniDate, String endDate, String kpis, String objects, String repType )
    {
        LineChartVisualization l = new LineChartVisualization( );
        l.loadJsonData( objectType, iniDate, endDate, kpis, objects, repType );
        insertRow( 0 );
        setWidget( 0, 0, l );
    }
    
    public void loadJsonData( String objectType, String dateStr, String kpis, String objects, String repType )
    {
        LineChartVisualizationComparative l = new LineChartVisualizationComparative( );
        l.loadJsonData( objectType, dateStr, kpis, objects, repType );
        insertRow( 0 );
        setWidget( 0, 0, l );
    }
}
