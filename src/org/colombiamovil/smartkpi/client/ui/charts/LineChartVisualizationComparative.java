package org.colombiamovil.smartkpi.client.ui.charts;

import java.util.ArrayList;
import java.util.Date;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.ui.widgets.JsonDataTable;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;

/**
 * GWT's AnnotatedTimeLine.
 * 
 * Normally, a visualization's data is loaded at first in JSON format, but there is the possibility to add data using AJAX for a currently shown chart.
 * 
 * Developer can show the chart in two ways: MODE_COMPLETE: means the chart with all it's options. Normally shown in a screen where only resides this widget. MODE_MULTIPLE:
 * reduces the size of the widget by removing the options and descriptions panels. This is used when the widget needs to be embedded in another composite widget, normally to
 * show more than one chart at the time
 * */
public class LineChartVisualizationComparative extends Composite implements ClickHandler
{

    private VerticalPanel mainPanel;
    private HorizontalPanel chartPanel;
    private HorizontalPanel northPanel;
    private VerticalPanel centerPanel;
    private HorizontalPanel linksPanel;

    private Label closeReportLink, splitAxis, unifyAxis;
    private RadioButton addBefore, addAfter;

    private Widget chart;
    private Options opts;
    private JsonDataTable data;
    private ArrayList<Integer> jsIntegers = new ArrayList<Integer>( );

    private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc( );

    public String objType = "", kpis = "", objects = "", dates = "", repType = "";

    public LineChartVisualizationComparative( )
    {
        mainPanel = new VerticalPanel( );
        chartPanel = new HorizontalPanel( );
        centerPanel = new VerticalPanel( );

        northPanel = new HorizontalPanel( );
        linksPanel = new HorizontalPanel( );

        closeReportLink = new Label( "Close Report" );
        unifyAxis = new Label( "Unify Scale" );
        splitAxis = new Label( "Split Scales" );

        addBefore = new RadioButton( "addOri", "Before" );
        addAfter = new RadioButton( "addOri", "After" );
        addBefore.setValue( true );

        closeReportLink.addClickHandler( this );
        unifyAxis.addClickHandler( this );
        splitAxis.addClickHandler( this );

        closeReportLink.addStyleName( "activelink" );
        unifyAxis.setStyleName( "activelink" );
        splitAxis.setStyleName( "activelink" );

        linksPanel.setSpacing( 6 );
        linksPanel.add( closeReportLink );

        northPanel.setWidth( "100%" );
        northPanel.setStyleName( "oddRow" );
        northPanel.addStyleName( "silverBottom" );
        northPanel.add( linksPanel );

        mainPanel.add( northPanel );

        mainPanel.setSpacing( 5 );
        mainPanel.setWidth( "100%" );

        getChart( );

        chartPanel.setWidth( "100%" );
        centerPanel.setWidth( "100%" );
        centerPanel.add( chartPanel );
        mainPanel.add( centerPanel );
        mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        initWidget( mainPanel );
    }

    private void buildJsonALC( String jsonData )
    {
        System.out.println( new Date( ) + " BUILDING JSON" );
        opts = Options.create( );
        opts.setLegend( LegendPosition.TOP );
        opts.setPointSize( 3 );
        Options hAxis = Options.create();
        hAxis.setOption( "slantedText", true) ;
        hAxis.setOption("slantedTextAngle", 90.0);
        opts.setOption("hAxis", hAxis);

        data = JsonDataTable.create( jsonData );
        System.out.println( new Date( ) + " Data Table Created" );
        ( ( LineChart )getChart( ) ).draw( data, opts );
        System.out.println( new Date( ) + " Chart Painted" );
        MainMenuBar.setStatus( MainMenuBar.NORMAL );
    }

    /**
     * 
     * @param objectType
     * @param dates
     * @param kpis
     * @param objects
     * @param repType
     * @param orientation
     */
    public void loadJsonData( String objectType, String dates, String kpis, String objects, String repType )
    {
        MainMenuBar.setStatus( MainMenuBar.LOADING );
        this.objType = objectType;
        this.kpis = kpis;
        this.objects = objects;
        this.repType = repType;

        if( dataSvc == null )
        {
            dataSvc = SmartKpis.getDataSvc( );
        }
        AsyncCallback<String> callback = new AsyncCallback<String>( )
        {
            public void onFailure( Throwable caught )
            {
                PopupMessage.showWarning( "No data found for the given report configuration" );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }

            public void onSuccess( String result )
            {
                try
                {
                    System.out.println( new Date( ) + " Answered" );
                    buildJsonALC( result );

                }
                catch( Exception e )
                {
                    remove( );
                    PopupMessage.showWarning( "No data found for the given report configuration" );
                    MainMenuBar.setStatus( MainMenuBar.NORMAL );
                    e.printStackTrace( );
                }
            }
        };

        dataSvc.getChartJson( objectType, dates, kpis, objects, repType, false, SmartKpis.getLoginForm( ).getUserBean( ), callback );
    }
    public void remove( )
    {
        SmartKpis.getVisualizationContainer( ).remove( this );
    }

    private Widget getChart( )
    {
        if( chart != null )
            chartPanel.remove( chart );
        else
        {
            chart = new LineChart( );
            chart.setWidth( "100%" );
            chart.setHeight( "450px" );
        }
        chartPanel.add( chart );
        return chart;
    }

    /*
     * private void buildAddAxisOracle() { addOracle.clear(); int colNum = data.getNumberOfColumns(); for(int i=1; i<colNum; i=i+3) { addOracle.add("Add: " +
     * data.getColumnLabel(i)); } addAxisBox.setText("Add: "); }
     */

    public void onClick( ClickEvent event )
    {
        if( event.getSource( ).equals( closeReportLink ) )
        {
            SmartKpis.getVisualizationContainer( ).remove( this );
        }
        else if( event.getSource( ).equals( splitAxis ) )
        {

        }
        else if( event.getSource( ).equals( unifyAxis ) )
        {

        }

    }

}
