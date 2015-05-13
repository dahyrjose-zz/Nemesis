package org.colombiamovil.smartkpi.client.ui.charts;

import java.util.ArrayList;
import java.util.Date;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.ui.widgets.InfoWidget;
import org.colombiamovil.smartkpi.client.ui.widgets.JsonDataTable;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.RangeChangeHandler;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.AnnotatedLegendPosition;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.Options;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.ScaleType;

/**
 * GWT's AnnotatedTimeLine.
 * 
 * Normally, a visualization's data is loaded at first in JSON format, but there is the possibility to add data using AJAX for a currently shown chart.
 * 
 * Developer can show the chart in two ways: MODE_COMPLETE: means the chart with all it's options. Normally shown in a screen where only resides this widget. MODE_MULTIPLE:
 * reduces the size of the widget by removing the options and descriptions panels. This is used when the widget needs to be embedded in another composite widget, normally to
 * show more than one chart at the time
 * */
public class LineChartVisualization extends Composite implements ClickHandler
{

    private VerticalPanel mainPanel;
    private VerticalPanel optionsPanel;
    private HorizontalPanel axisPanel;
    private VerticalPanel axisBoxesPanel;
    private HorizontalPanel chartPanel;
    private HorizontalPanel northPanel;
    private VerticalPanel centerPanel;
    private HorizontalPanel addTimePanel;
    private HorizontalPanel linksPanel;
    private HorizontalPanel addLinksPanel;
    private HorizontalPanel comparePanel;
    private Button showEventsButton;
    private Label closeReportLink, saveReportLink, newReportLink, drillModeLink, kpiScrLink, moreLink, splitAxis, unifyAxis, addAxis, removeAxis;
    private Label addDay, add3Days, addWeek, add2Week, addMonth, add3Month, add6Month, addYear, showEvents;
    private RadioButton addBefore, addAfter;
    private TextBox compareBox = new TextBox( );
    private ListBox firstAxisBox, secondAxisBox, thirdAxisBox;
    private InfoWidget unifyInfo;
    private InfoWidget splitInfo;
    private RangeChangeHandler changeHandler;
    private Date zoomStart, zoomEnd;

    private Widget chart;
    private Options opts, rebuildOpts;
    private JsonDataTable data;
    private ArrayList<Integer> jsIntegers = new ArrayList<Integer>( );
    private boolean locked = false, splitted = false;

    private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc( );

    public String objType = "", kpis = "", objects = "", iniDate = "", endDate = "", repType = "", repOpt = "";

    public int mode;
    public static int MODE_COMPLETE = 0, MODE_MULTIPLE = 1;

    /**
     * @wbp.parser.constructor
     */
    public LineChartVisualization( )
    {
        this( MODE_COMPLETE );
    }

    public LineChartVisualization( int mode )
    {
        this.mode = mode;
        mainPanel = new VerticalPanel( );
        // showAxisPanel = new HorizontalPanel();
        chartPanel = new HorizontalPanel( );
        centerPanel = new VerticalPanel( );
        if( mode == MODE_COMPLETE )
        {
            optionsPanel = new VerticalPanel( );
            axisPanel = new HorizontalPanel( );
            axisBoxesPanel = new VerticalPanel( );
            northPanel = new HorizontalPanel( );
            addTimePanel = new HorizontalPanel( );
            linksPanel = new HorizontalPanel( );
            addLinksPanel = new HorizontalPanel( );
            comparePanel = new HorizontalPanel( );

            compareBox = new TextBox( );
            firstAxisBox = new ListBox( );
            secondAxisBox = new ListBox( );
            thirdAxisBox = new ListBox( );
            unifyInfo = new InfoWidget( PopupMessage.HELP_MESSAGE, "Fixes all time lines to only one Y-Axis at the \n  right side" );
            splitInfo = new InfoWidget( PopupMessage.HELP_MESSAGE, "<ul><li>Use this if you need to separate each time line's  Y-Axis scale. " + "<li>Use the shown boxes at the right of this link to pick up to three scales to display at the chart. "
                    + "<li>Note that it is not possible to show a fourth scale " + "and you can not associate two or more charts to one " + "scale in this mode, thus, you have to look well at those chart's scale.</ul>" );
            closeReportLink = new Label( "Close Report" );
            saveReportLink = new Label( "Save Report" );
            newReportLink = new Label( "Back to Report Configuration" );
            drillModeLink = new Label( "Drill Mode" );
            kpiScrLink = new Label( "Kpi Screener" );
            showEventsButton = new Button( "Hide events" );
            moreLink = new Label( "More" );
            showEvents = new Label("Hide Events");
            unifyAxis = new Label( "Unify Scale" );
            splitAxis = new Label( "Split Scales" );
            addAxis = new Label( "Add Scale" );
            removeAxis = new Label( "Remove Scale" );

            addDay = new Label( "1 day" );
            add3Days = new Label( "3 days" );
            addWeek = new Label( "1 week" );
            add2Week = new Label( "2 weeks" );
            addMonth = new Label( "1 month" );
            add3Month = new Label( "3 months" );
            add6Month = new Label( "6 months" );
            addYear = new Label( "1 year" );

            addDay.addClickHandler( this );
            add3Days.addClickHandler( this );
            addWeek.addClickHandler( this );
            add2Week.addClickHandler( this );
            addMonth.addClickHandler( this );
            add3Month.addClickHandler( this );
            add6Month.addClickHandler( this );
            addYear.addClickHandler( this );

            addDay.addStyleName( "activelink" );
            add3Days.addStyleName( "activelink" );
            addWeek.addStyleName( "activelink" );
            add2Week.addStyleName( "activelink" );
            addMonth.addStyleName( "activelink" );
            add3Month.addStyleName( "activelink" );
            add6Month.addStyleName( "activelink" );
            addYear.addStyleName( "activelink" );

            addBefore = new RadioButton( "addOri", "Before" );
            addAfter = new RadioButton( "addOri", "After" );
            addBefore.setValue( true );

            closeReportLink.addClickHandler( this );
            saveReportLink.addClickHandler( this );
            newReportLink.addClickHandler( this );
            // drillModeLink.addClickHandler(this);
            kpiScrLink.addClickHandler( this );
            showEventsButton.addClickHandler( this );
            moreLink.addClickHandler( this );
            showEvents.addClickHandler(this);
            unifyAxis.addClickHandler( this );
            splitAxis.addClickHandler( this );
            addAxis.addClickHandler( this );
            removeAxis.addClickHandler( this );

            closeReportLink.addStyleName( "activelink" );
            saveReportLink.addStyleName( "activelink" );
            newReportLink.addStyleName( "activelink" );
            // drillModeLink.addStyleName("activelink");
            kpiScrLink.addStyleName( "activelink" );
            moreLink.addStyleName( "activelink" );
            showEvents.addStyleName("activeLink");
            unifyAxis.setStyleName( "activelink" );
            splitAxis.setStyleName( "activelink" );
            addAxis.setStyleName( "activelink" );
            removeAxis.setStyleName( "activelink" );

            linksPanel.setSpacing( 6 );
            linksPanel.add( closeReportLink );
            linksPanel.add( saveReportLink );
            // linksPanel.add(newReportLink);

            northPanel.setWidth( "100%" );
            northPanel.setStyleName( "oddRow" );
            northPanel.addStyleName( "silverBottom" );
            northPanel.add( linksPanel );

            compareBox.setStyleName( "field_input" );
            comparePanel.setSpacing( 5 );
            comparePanel.add( new Label( "Compare with: " ) );
            comparePanel.add( compareBox );

            firstAxisBox.addStyleName( "field_input" );
            secondAxisBox.addStyleName( "field_input" );
            thirdAxisBox.addStyleName( "field_input" );
            DOM.setElementAttribute( firstAxisBox.getElement( ), "border", "0" );

            axisPanel.setSpacing( 5 );
            axisBoxesPanel.setSpacing( 2 );
            axisPanel.add( unifyInfo );
            axisPanel.add( unifyAxis );
            axisPanel.add( splitInfo );
            axisPanel.add( splitAxis );

            axisPanel.add( axisBoxesPanel );
            axisBoxesPanel.add( firstAxisBox );
            axisBoxesPanel.add( secondAxisBox );
            axisBoxesPanel.add( thirdAxisBox );

            addLinksPanel.setSpacing( 5 );
            addLinksPanel.add( new Label( "Add: " ) );
            addLinksPanel.add( addDay );
            addLinksPanel.add( add3Days );
            addLinksPanel.add( addWeek );
            addLinksPanel.add( add2Week );
            addLinksPanel.add( addMonth );
            addLinksPanel.add( add3Month );
            addLinksPanel.add( add6Month );
            addLinksPanel.add( addYear );
            addLinksPanel.add( addBefore );
            addLinksPanel.add( addAfter );

            axisPanel.setVisible( false );
            addTimePanel.setWidth( "100%" );
            addTimePanel.add( addLinksPanel );
            addTimePanel.setVisible( false );
            comparePanel.setVisible( false );

            optionsPanel.setWidth( "100%" );
            optionsPanel.addStyleName( "silverTopLeft" );
            //optionsPanel.add( showEventsButton );
            HorizontalPanel hhz = new HorizontalPanel();
            hhz.add(moreLink);	
            hhz.add(showEvents);
            showEvents.setWidth("100%");
            showEvents.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            hhz.setWidth("100%");
            //optionsPanel.add( moreLink );
            optionsPanel.add(hhz);
            optionsPanel.add( axisPanel );
            optionsPanel.add( addTimePanel );

            centerPanel.add( optionsPanel );
            mainPanel.add( northPanel );
        }

        mainPanel.setSpacing( 5 );
        mainPanel.setWidth( "100%" );
        mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        getChart( );

        chartPanel.setWidth( "100%" );
        centerPanel.setWidth( "100%" );
        centerPanel.add( chartPanel );
        mainPanel.add( centerPanel );
        initWidget( mainPanel );

        changeHandler = new RangeChangeHandler( )
        {
            @Override
            public void onRangeChange( RangeChangeEvent event )
            {
                // System.out.println("Range changed " + event.getStart() + "  -  " + event.getEnd());
                zoomStart = event.getStart( );
                zoomEnd = event.getEnd( );
            }
        };
    }

    private void buildJsonALC( String jsonData )
    {
        System.out.println( new Date( ) + " BUILDING JSON" );
        System.out.println( jsonData );
        opts = Options.create( );
        opts.setDisplayExactValues( true );
        opts.setDisplayAnnotations( true );
        opts.setDisplayAnnotationsFilter( true );
        opts.setOption( "displayRangeSelector", mode == MODE_COMPLETE );
        opts.setDisplayZoomButtons( mode == MODE_COMPLETE );
        opts.setLegendPosition( AnnotatedLegendPosition.NEW_ROW );
        // opts.setScaleColumns(0,1,2);
        opts.setScaleType( ScaleType.MAXIMIZE );

        data = JsonDataTable.create( jsonData );
        System.out.println( new Date( ) + " Data Table Created" );
        ( ( AnnotatedTimeLine )getChart( ) ).draw( data, opts );
        System.out.println( new Date( ) + " Chart Painted" );
        MainMenuBar.setStatus( MainMenuBar.NORMAL );
        rebuildAxisBoxes( );
    }

    private void addJsonALC( String jsonData ) throws Exception
    {
        System.out.println( new Date( ) + " Actual Rows: " + data.getNumberOfRows( ) );
        data.addRows( jsonData );
        System.out.println( new Date( ) + " Adding Rows: " + data.getNumberOfRows( ) );
        ( ( AnnotatedTimeLine )getChart( ) ).draw( data, opts );
        System.out.println( new Date( ) + " Painted" );
        MainMenuBar.setStatus( MainMenuBar.NORMAL );
    }

    private void buildALC( String[][] remoteData ) throws Exception
    {
        opts = Options.create( );
        opts.setDisplayExactValues( true );
        opts.setDisplayAnnotations( true );
        opts.setDisplayAnnotationsFilter( true );
        opts.setOption( "displayRangeSelector", mode == MODE_COMPLETE );
        opts.setDisplayZoomButtons( mode == MODE_COMPLETE );
        opts.setLegendPosition( AnnotatedLegendPosition.NEW_ROW );
        // opts.setScaleColumns(0,1,2);
        opts.setScaleType( ScaleType.MAXIMIZE );

        String[] objectArray = remoteData[ 0 ][ 1 ].split( "," );
        String[] kpisArray = kpis.split( "," );

        data = JsonDataTable.create( );
        data.addColumn( ColumnType.DATETIME, "Date" );
        for( String kpi : kpisArray )
        {
            for( String obj : objectArray )
            {
                data.addColumn( ColumnType.NUMBER, kpi + "(" + obj + ")" );
                data.addColumn( ColumnType.STRING, kpi + "(" + obj + ")" );
                data.addColumn( ColumnType.STRING, kpi + "(" + obj + ")" );
                System.out.println( "Column: " + kpi + "(" + obj + ")" );
            }
        }
        // data.addRows(remoteData.length/((kpisArray.length+1)*objectArray.length));
        data.addRows( Integer.parseInt( remoteData[ 0 ][ 2 ] ) );
        System.out.println( new Date( ) + " Rows: " + data.getNumberOfRows( ) );
        for( int i = 1; i < remoteData.length; i++ )
        {
            String[] row = remoteData[ i ];
            // System.out.println(row[0] + "  --  " + row[1] + "  --  " + row[2]);
            if( remoteData[ i ][ 1 ].equals( "0" ) )
                data.setValue( Integer.parseInt( row[ 0 ] ), Integer.parseInt( row[ 1 ] ), DateTimeFormat.getFormat( "yyyy-MM-dd HH:mm:ss" ).parse( row[ 2 ] ) );
            else
            {
                try
                {
                    data.setValue( Integer.parseInt( row[ 0 ] ), Integer.parseInt( row[ 1 ] ), Double.parseDouble( row[ 2 ] ) );
                }
                catch( NumberFormatException nfe )
                {
                    data.setValue( Integer.parseInt( row[ 0 ] ), Integer.parseInt( row[ 1 ] ), row[ 2 ] );
                }
            }
        }
        System.out.println( new Date( ) + " Built" );
        ( ( AnnotatedTimeLine )getChart( ) ).draw( data, opts );
        System.out.println( new Date( ) + " Painted" );
        MainMenuBar.setStatus( MainMenuBar.NORMAL );
        rebuildAxisBoxes( );
    }

    private void addDataALC( String[][] remoteData ) throws Exception
    {
        // String[] objectArray = objects.split(",");
        // String[] kpisArray = kpis.split(",");

        int oldRowCount = data.getNumberOfRows( );
        // data.addRows(remoteData.length/((kpisArray.length+1)*objectArray.length));
        data.addRows( Integer.parseInt( remoteData[ 0 ][ 2 ] ) );
        System.out.println( new Date( ) + " Adding Rows: " + data.getNumberOfRows( ) );
        // System.out.println(new Date() + " Old Number of Rows: " + oldRowCount);
        for( int i = 1; i < remoteData.length; i++ )
        {
            String[] row = remoteData[ i ];
            // System.out.println(row[0] + "  --  " + row[1] + "  --  " + row[2]);
            if( remoteData[ i ][ 1 ].equals( "0" ) )
                data.setValue( Integer.parseInt( row[ 0 ] ) + oldRowCount, Integer.parseInt( row[ 1 ] ), DateTimeFormat.getFormat( "yyyy-MM-dd HH:mm:ss" ).parse( row[ 2 ] ) );
            else
            {
                try
                {
                    data.setValue( Integer.parseInt( row[ 0 ] ) + oldRowCount, Integer.parseInt( row[ 1 ] ), Double.parseDouble( row[ 2 ] ) );
                }
                catch( Exception nfe )
                {
                    data.setValue( Integer.parseInt( row[ 0 ] ) + oldRowCount, Integer.parseInt( row[ 1 ] ), row[ 2 ] );
                }
            }
        }
        System.out.println( new Date( ) + " Built" );
        ( ( AnnotatedTimeLine )getChart( ) ).draw( data, opts );
        System.out.println( new Date( ) + " Painted" );
        MainMenuBar.setStatus( MainMenuBar.NORMAL );
        // rebuildAxisBoxes();
    }

    public void setLocked( boolean locked )
    {
        this.locked = locked;
    }

    public boolean isLocked( )
    {
        return locked;
    }

    public void loadData( String objectType, String iniDate, String endDate, String kpis, String objects )
    {
        loadData( objectType, iniDate, endDate, kpis, objects, "bh", false, 0 );
    }

    public void loadData( String objectType, String iniDate, String endDate, String kpis, String objects, String repType )
    {
        loadData( objectType, iniDate, endDate, kpis, objects, repType, false, 0 );
    }

    public void loadData( String objectType, String iniDate, String endDate, String kpis, String objects, String repType, final boolean append, int orientation )
    {
        MainMenuBar.setStatus( MainMenuBar.LOADING );
        setLocked( true );
        // Window.alert(iniDate + " - " + endDate);
        // if(append) return;
        this.objType = objectType;
        this.kpis = kpis;
        this.objects = objects;
        this.repType = repType;
        if( orientation == -1 || orientation == 0 )
            this.iniDate = iniDate;
        if( orientation == 1 || orientation == 0 )
            this.endDate = endDate;
        if( dataSvc == null )
        {
            dataSvc = SmartKpis.getDataSvc( );
        }

        AsyncCallback<String[][]> callback = new AsyncCallback<String[][]>( )
        {
            public void onFailure( Throwable caught )
            {
                History.back( );
                if( mode == MODE_COMPLETE )
                    PopupMessage.showWarning( "No data found for the given report configuration" );
                // PopupMessage.showError("ERROR: " + caught.getMessage());
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
                setLocked( false );
            }

            public void onSuccess( String[][] result )
            {
                try
                {
                    System.out.println( new Date( ) + " Answered" );
                    if( !append )
                        buildALC( result );
                    else
                        addDataALC( result );
                }
                catch( Exception e )
                {
                    History.back( );
                    if( mode == MODE_COMPLETE )
                        PopupMessage.showWarning( "No data found for the given report configuration" );
                    // PopupMessage.showError("ERROR: " + e.getMessage());
                    MainMenuBar.setStatus( MainMenuBar.NORMAL );
                    e.printStackTrace( );
                }
                setLocked( false );
            }
        };

        dataSvc.getChartData( objectType, iniDate, endDate, kpis, objects, repType, SmartKpis.getLoginForm( ).getUserBean( ), callback );
        System.out.println( new Date( ) + " Requested" );
    }

    public void loadJsonData( String objectType, String iniDate, String endDate, String kpis, String objects )
    {
        loadJsonData( objectType, iniDate, endDate, kpis, objects, "bh", false, 0 );
    }

    public void loadJsonData( String objectType, String iniDate, String endDate, String kpis, String objects, String repType )
    {
        loadJsonData( objectType, iniDate, endDate, kpis, objects, repType, false, 0 );
    }

    public void loadJsonData( String objectType, String iniDate, String endDate, String kpis, String objects, String repType, final boolean append, int orientation )
    {
        MainMenuBar.setStatus( MainMenuBar.LOADING );
        setLocked( true );
        this.objType = objectType;
        this.kpis = kpis;
        this.objects = objects;
        this.repType = repType;
        if( orientation == -1 || orientation == 0 )
            this.iniDate = iniDate;
        if( orientation == 1 || orientation == 0 )
            this.endDate = endDate;
        if( dataSvc == null )
        {
            dataSvc = SmartKpis.getDataSvc( );
        }
        AsyncCallback<String> callback = new AsyncCallback<String>( )
        {
            public void onFailure( Throwable caught )
            {
                if( mode == MODE_COMPLETE )
                    PopupMessage.showWarning( "No data found for the given report configuration" );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
                setLocked( false );
            }

            public void onSuccess( String result )
            {
                try
                {
                    System.out.println( new Date( ) + " Answered" );
                    if( !append )
                        buildJsonALC( result );
                    else
                        addJsonALC( result );

                }
                catch( Exception e )
                {
                    // History.back();
                    remove( );
                    if( mode == MODE_COMPLETE )
                        PopupMessage.showWarning( "No data found for the given report configuration" );
                    /*
                     * if(e.getMessage().contains("-2146827286") || e.getMessage().length() == 0) { History.back();
                     * PopupMessage.showWarning("No data found for the given report configuration"); } else { History.back(); PopupMessage.showError("ERROR: " +
                     * e.getMessage()); }
                     */
                    MainMenuBar.setStatus( MainMenuBar.NORMAL );
                    e.printStackTrace( );
                }
                setLocked( false );
            }
        };

        dataSvc.getChartJson( objectType, iniDate, endDate, kpis, objects, repType, append, SmartKpis.getLoginForm( ).getUserBean( ), callback );
    }
    public void remove( )
    {
        SmartKpis.getVisualizationContainer( ).remove( this );
    }
    public void saveReport( )
    {
        MainMenuBar.setStatus( MainMenuBar.LOADING );
        if( dataSvc == null )
        {
            dataSvc = SmartKpis.getDataSvc( );
        }

        AsyncCallback<String> callback = new AsyncCallback<String>( )
        {
            public void onFailure( Throwable caught )
            {
                SmartKpis.setScreen( "savedReps" );
                PopupMessage.showError( "Error when trying to save the report: " + caught.getMessage( ) );
                // Window.alert("ERROR: " + caught.getMessage());
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }

            public void onSuccess( String result )
            {
                SmartKpis.getSavedReportsForm( ).loadSavedReports( );
                SmartKpis.setScreen( "savedReps" );
                PopupMessage.showInfo( "Successfully saved [" + objType + " - " + repType + "; " + iniDate + " - " + endDate + "; " + objects + "; " + kpis + "]" );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }
        };

        dataSvc.saveChart( objType, repType, iniDate, endDate, objects, kpis, SmartKpis.getLoginForm( ).getUserBean( ).getUserLogin( ), callback );
    }

    public void loadMoreData( String dateUnits, int ammount, int orientation )
    {
        if( isLocked( ) )
        {
            PopupMessage.showHelp( "Still loading data, please wait" );
            return;
        }
        if( orientation == 1 )
        {
            String queryDate = "date_add(" + endDate + ", interval " + orientation + " day)";
            String newDate = "date_add(" + endDate + ", interval " + ammount * orientation + " " + dateUnits + ")";
            loadData( objType, queryDate, newDate, kpis, objects, repType, true, orientation );
        }
        else
        {
            String queryDate = "date_add(" + iniDate + ", interval " + orientation + " day)";
            String newDate = "date_add(" + iniDate + ", interval " + ammount * orientation + " " + dateUnits + ")";
            loadData( objType, newDate, queryDate, kpis, objects, repType, true, orientation );
        }
    }

    private Widget getChart( )
    {
        if( chart != null )
            chartPanel.remove( chart );
        if( mode == MODE_COMPLETE )
            chart = new AnnotatedTimeLine( "100%", "400px" );
        else
            chart = new AnnotatedTimeLine( "95%", "300px" );
        chartPanel.add( chart );
        ( ( AnnotatedTimeLine )chart ).addRangeChangeHandler( changeHandler );
        return chart;
    }

    private void rebuildAxisBoxes( )
    {
        if( mode == MODE_MULTIPLE )
            return;
        int colNum = data.getNumberOfColumns( );
        firstAxisBox.clear( );
        secondAxisBox.clear( );
        thirdAxisBox.clear( );
        firstAxisBox.addItem( "Select axis to show on graph", "-1" );
        if( colNum > 4 )
            secondAxisBox.addItem( "Select axis to show on graph", "-1" );
        if( colNum > 7 )
            thirdAxisBox.addItem( "Select axis to show on graph", "-1" );
        for( int i = 1; i < colNum; i = i + 3 )
        {
            firstAxisBox.addItem( data.getColumnLabel( i ), Integer.toString( i ) );
            if( colNum > 4 )
                secondAxisBox.addItem( data.getColumnLabel( i ), Integer.toString( i ) );
            if( colNum > 7 )
                thirdAxisBox.addItem( data.getColumnLabel( i ), Integer.toString( i ) );
        }
        firstAxisBox.setVisible( true );
        secondAxisBox.setVisible( colNum > 4 );
        thirdAxisBox.setVisible( colNum > 7 );
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
        else if( event.getSource( ).equals( saveReportLink ) )
        {
            saveReport( );
        }
        else if( event.getSource( ).equals( newReportLink ) )
        {
            SmartKpis.setScreen( "newRep" );
        }
        else if( event.getSource( ).equals( kpiScrLink ) )
        {
            SmartKpis.setScreen( "kpiscr" );
        }
        else if( event.getSource( ).equals( drillModeLink ) )
        {
            SmartKpis.getDrillDownReport( ).setParameters( objType, iniDate, endDate, kpis, objects, repType );
            SmartKpis.getDrillDownReport( ).setAnalysisRange( zoomStart, zoomEnd );
            SmartKpis.setScreen( "drill" );
            SmartKpis.getDrillDownReport( ).loadMainChart( );
        }
        else if( event.getSource( ).equals( showEvents ) )
        {
            if( showEvents.getText( ).equals( "Show events" ) )
            {
            	showEvents.setText( "Hide events" );
                opts.setDisplayAnnotations( true );
                ( ( AnnotatedTimeLine )getChart( ) ).draw( data, opts );
            }
            else
            {
            	showEvents.setText( "Show events" );
                opts.setDisplayAnnotations( false );
                ( ( AnnotatedTimeLine )getChart( ) ).draw( data, opts );
            }
        }
        else if( event.getSource( ).equals( moreLink ) )
        {
            boolean sw = moreLink.getText( ).equals( "More" );
            axisPanel.setVisible( sw );
            comparePanel.setVisible( sw );
            addTimePanel.setVisible( sw );
            if( sw )
                moreLink.setText( "Less" );
            else
                moreLink.setText( "More" );
        }
        else if( event.getSource( ).equals( splitAxis ) )
        {
            // rebuildAxisBoxes();
            jsIntegers.clear( );
            if( firstAxisBox.getSelectedIndex( ) > 0 )
                jsIntegers.add( firstAxisBox.getSelectedIndex( ) - 1 );
            if( secondAxisBox.getSelectedIndex( ) > 0 )
                jsIntegers.add( secondAxisBox.getSelectedIndex( ) - 1 );
            if( thirdAxisBox.getSelectedIndex( ) > 0 )
                jsIntegers.add( thirdAxisBox.getSelectedIndex( ) - 1 );
            // Window.alert(jsIntegers.toString());
            if( jsIntegers.size( ) == 0 )
                opts.setScaleColumns( );
            if( jsIntegers.size( ) == 1 )
                opts.setScaleColumns( jsIntegers.get( 0 ) );
            if( jsIntegers.size( ) == 2 )
                opts.setScaleColumns( jsIntegers.get( 0 ), jsIntegers.get( 1 ) );
            if( jsIntegers.size( ) == 3 )
                opts.setScaleColumns( jsIntegers.get( 0 ), jsIntegers.get( 1 ), jsIntegers.get( 2 ) );
            // opts.setScaleColumns(getNativeArray(jsIntegers.toArray(new Integer[0])));
            opts.setScaleType( ScaleType.ALLMAXIMIZE );
            ( ( AnnotatedTimeLine )getChart( ) ).draw( data, opts );
            // splitAxis.setText("Unify Scale");
            splitted = true;
            // showAxisPanel.setVisible(splitted);
        }
        else if( event.getSource( ).equals( unifyAxis ) )
        {
            if( splitted )
            {
                opts = Options.create( );
                opts.setOption( "displayRangeSelector", mode == MODE_COMPLETE );
                opts.setDisplayExactValues( true );
                opts.setDisplayAnnotations( true );
                opts.setDisplayAnnotationsFilter( true );
                opts.setDisplayZoomButtons( mode == MODE_COMPLETE );
                opts.setLegendPosition( AnnotatedLegendPosition.NEW_ROW );
                opts.setScaleType( ScaleType.MAXIMIZE );
                ( ( AnnotatedTimeLine )getChart( ) ).draw( data, rebuildOpts );
            }
            splitted = false;
        }
        else if( event.getSource( ).equals( addDay ) )
        {
            loadMoreData( "day", 1, addBefore.getValue( ) ? -1 : 1 );
        }
        else if( event.getSource( ).equals( add3Days ) )
        {
            loadMoreData( "day", 3, addBefore.getValue( ) ? -1 : 1 );
        }
        else if( event.getSource( ).equals( addWeek ) )
        {
            loadMoreData( "week", 1, addBefore.getValue( ) ? -1 : 1 );
        }
        else if( event.getSource( ).equals( add2Week ) )
        {
            loadMoreData( "week", 2, addBefore.getValue( ) ? -1 : 1 );
        }
        else if( event.getSource( ).equals( addMonth ) )
        {
            loadMoreData( "month", 1, addBefore.getValue( ) ? -1 : 1 );
        }
        else if( event.getSource( ).equals( add3Month ) )
        {
            loadMoreData( "month", 3, addBefore.getValue( ) ? -1 : 1 );
        }
        else if( event.getSource( ).equals( add6Month ) )
        {
            loadMoreData( "month", 6, addBefore.getValue( ) ? -1 : 1 );
        }
        else if( event.getSource( ).equals( addYear ) )
        {
            loadMoreData( "year", 1, addBefore.getValue( ) ? -1 : 1 );
        }
    }

}
