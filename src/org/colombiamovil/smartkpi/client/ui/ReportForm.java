package org.colombiamovil.smartkpi.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.ui.reportform.ReportCategory;
import org.colombiamovil.smartkpi.client.ui.reportform.ReportItem;
import org.colombiamovil.smartkpi.client.ui.reportform.ReportTreeCategory;
import org.colombiamovil.smartkpi.client.ui.reportform.ReportTreeItem;
import org.colombiamovil.smartkpi.client.ui.widgets.FixedTree;
import org.colombiamovil.smartkpi.client.ui.widgets.InfoWidget;
import org.colombiamovil.smartkpi.client.ui.widgets.MultipleStateBox;
import org.colombiamovil.smartkpi.client.ui.widgets.MultipleStateBoxClickHandler;
import org.colombiamovil.smartkpi.client.ui.widgets.OptimalListBox;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class ReportForm extends Composite implements SelectionHandler<TreeItem>, ClickHandler, ValueChangeHandler<String>, MouseOverHandler, OpenHandler<TreeItem> {

    private VerticalPanel mainPanel = new VerticalPanel( );
    private VerticalPanel northPanel = new VerticalPanel( );
    private VerticalPanel objPanel = new VerticalPanel( );
    private VerticalPanel kpiPanel = new VerticalPanel( );
    private DockPanel southPanelTest = new DockPanel( );
    private HorizontalPanel datePanel = new HorizontalPanel( );
    private HorizontalPanel dateBackPanel = new HorizontalPanel( );
    private HorizontalPanel objOptPanel = new HorizontalPanel( );
    private HorizontalPanel kpiOptPanel = new HorizontalPanel( );
    private HorizontalPanel repTypePanel = new HorizontalPanel( );
    private HorizontalPanel repTypeBackPanel = new HorizontalPanel( );
    private HorizontalPanel repOptPanel = new HorizontalPanel( );

    private FixedTree repTree = new FixedTree( ), kpiTree = new FixedTree( );
    private LinkedHashMap<String, ArrayList<String[]>> families;
//    private String[] objects;
    private String selectedObj;
    private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc( );

    private LinkedHashMap<String, String> reportTypes = new LinkedHashMap<String, String>( );
    private OptimalListBox objHtml;
    private OptimalListBox dates;
    private TextBox objBox = new TextBox( );
    private TextBox kpiBox = new TextBox( );
    private DateBox iniDate = new DateBox( );
    private DateBox endDate = new DateBox( );
    private Label addGraph = new Label( "Add Graph" );
    private Label buildGraph = new Label( "Clear & Graph" );
    private Label clearAll = new Label( "Clear All" );
    private Label showData = new Label( "Show Data" );
    private Label exportData = new Label( "Export Data" );
    private Label clear;
    private CheckBox groupObjects = new CheckBox( "Group Objects" );
    private MultipleStateBoxClickHandler msbHandler = new MultipleStateBoxClickHandler( );

    private HorizontalPanel listPanel = new HorizontalPanel( );
    private HorizontalPanel dateOptions = new HorizontalPanel( );
    private VerticalPanel listObjPanel = new VerticalPanel( );
    private VerticalPanel datesPanel = new VerticalPanel( );
    private TextBox dateBox = new TextBox( );

    public ReportForm( )
    {
        mainPanel.setSpacing( 5 );
        northPanel.setSpacing( 5 );
        mainPanel.setWidth( "100%" );
        northPanel.setWidth( "100%" );
        dateBackPanel.setWidth( "100%" );
        repTypeBackPanel.setWidth( "100%" );
        datePanel.setSpacing( 6 );
        repTypePanel.setSpacing( 6 );
        repOptPanel.setSpacing( 6 );

        objBox.addStyleName( "field_input" );
        objBox.addKeyUpHandler( new KeyUpHandler( )
        {
            public void onKeyUp( KeyUpEvent event )
            {
                if( event.getNativeKeyCode( ) == KeyCodes.KEY_ENTER )
                {
                    loadObjectsListBox( selectedObj, objBox.getText( ) );
                }
            }
        } );

        dateBox.addStyleName( "field_input" );
        dateBox.setWidth( "100%" );
        dateBox.addKeyUpHandler( new KeyUpHandler( )
        {
            public void onKeyUp( KeyUpEvent event )
            {
                if( event.getNativeKeyCode( ) == KeyCodes.KEY_ENTER )
                {
                    loadDates( dateBox.getText( ) );
                }
            }
        } );

        objHtml = new OptimalListBox( "" );
        objHtml.setWidth( "100%" );

        dates = new OptimalListBox( "" );
        dates.setWidth( "100%" );

        iniDate.addStyleName( "field_input" );
        iniDate.setFormat( new DateBox.DefaultFormat( DateTimeFormat.getFormat( "yyyy-MM-dd" ) ) );
        iniDate.setValue( new Date( ) );
        endDate.addStyleName( "field_input" );
        endDate.setFormat( new DateBox.DefaultFormat( DateTimeFormat.getFormat( "yyyy-MM-dd" ) ) );
        endDate.setValue( new Date( ) );

        addGraph.addClickHandler( this );
        addGraph.addStyleName( "activelink" );

        buildGraph.addClickHandler( this );
        buildGraph.addStyleName( "activelink" );

        clearAll.addClickHandler( this );
        clearAll.addStyleName( "activelink" );

        showData.addClickHandler( this );
        showData.addStyleName( "activelink" );

        exportData.addClickHandler( this );
        exportData.addStyleName( "activelink" );

        kpiBox.addStyleName( "field_input" );
        kpiBox.setWidth( "200px" );

        loadReportMenu( );
        repTree.addSelectionHandler( this );
        repTree.addOpenHandler( this );
        repTree.setWidth( "150px" );

        // DOM.setElementProperty(new MultipleStateBox().getElement(), "position", "relative");
        kpiTree.setWidth( "100px" );

        datePanel.add( new Label( "From:" ) );
        datePanel.add( iniDate );
        datePanel.add( new Label( "To:" ) );
        datePanel.add( endDate );
        datePanel.add( addGraph );
        datePanel.add( buildGraph );
        datePanel.add( clearAll );
        datePanel.add( showData );
        datePanel.add( exportData );
        dateBackPanel.setStyleName( "oddRow" );
        dateBackPanel.addStyleName( "silverBottom" );
        dateBackPanel.add( datePanel );
        repTypeBackPanel.setStyleName( "oddRow" );
        repTypeBackPanel.addStyleName( "silverBottom" );
        repTypeBackPanel.add( repTypePanel );

        buildReportTypes( );
        RadioButton r=new RadioButton( "Report Opt", "None" );
        r.setEnabled(true);
        repOptPanel.add(r);
        r=new RadioButton( "Report Opt", "SUM" );
        r.setEnabled(false);
        repOptPanel.add(r);
        r=new RadioButton("Report Opt", "AVG");
        r.setEnabled(false);
        repOptPanel.add(r);
        ((RadioButton)repOptPanel.getWidget( 0 )).setValue( true );
        // -------
        groupObjects.addValueChangeHandler( new ValueChangeHandler<Boolean>( )
        {
            public void onValueChange( ValueChangeEvent<Boolean> event )
            {
                if( ( ( CheckBox )event.getSource( ) ).getValue( ) )
                {
                    if( !objBox.getText( ).startsWith( "GROUP:" ) )
                        objBox.setText( "GROUP:" + objBox.getText( ) );
                }
                else
                {
                    if( objBox.getText( ).startsWith( "GROUP:" ) )
                        objBox.setText( objBox.getText( ).substring( 6 ) );
                }
            }
        } );
        // ------
        groupObjects.setTitle( "Check if you need to group the selected objects and aggregate data according to the functions in the kpi box" );
        objOptPanel.setSpacing( 2 );
        objOptPanel.setWidth( "100%" );
        objOptPanel.setHeight( "25px" );
        objOptPanel.setStyleName( "silverBottom" );
        clear = new Label( "Clear Box" );
        clear.addStyleName( "activelink" );
        clear.addClickHandler( new ClickHandler( )
        {
            public void onClick( ClickEvent event )
            {
                objBox.setText( "" );
                objBox.setFocus( true );
            }
        } );
        objOptPanel.add( new InfoWidget( PopupMessage.HELP_MESSAGE, InfoWidget.CLICK_ACTION, "<ul><li>Enter part of the object's name and press ENTER <br/>to find a specific object"
                + "<li>Use % as a wild card at the beggining or the middle <br/>of the string for more accurate search" + "<li>Use more than one search criteria using commas (,) " + "<li>Example 1: MSS will find all objects starting with MSS"
                + "<li>Example 2: %BAR will find any object that contains the substring BAR" + "<li>Example 3: BOG,CAL will find objects that start with BOG or CAL</ul>" ) );
        objOptPanel.add( clear );
        clear = new Label( "Clear List" );
        clear.addStyleName( "activelink" );
        clear.addClickHandler( new ClickHandler( )
        {
            public void onClick( ClickEvent event )
            {
                objHtml.clearSelection( );
                objBox.setFocus( true );
            }
        } );
        objOptPanel.add( clear );

        dateOptions.setSpacing( 2 );
        dateOptions.setWidth( "100%" );
        dateOptions.setHeight( "25px" );
        dateOptions.setStyleName( "silverBottom" );
        dateOptions.add( new InfoWidget( PopupMessage.HELP_MESSAGE, "TODO" ) );
        clear = new Label( "Clear Dates" );
        clear.addStyleName( "activelink" );
        clear.addClickHandler( new ClickHandler( )
        {
            public void onClick( ClickEvent event )
            {
                dateBox.setText( "" );
                dateBox.setFocus( true );
                dates.clearSelection( );
                loadDates( "" );
            }
        } );
        dateOptions.add( clear );

        datesPanel.add( dateOptions );
        datesPanel.add( dateBox );
        datesPanel.add( dates );

        objBox.setWidth( Window.getClientWidth( ) - 550 + "px" );// con "100%"
        listObjPanel.add( objOptPanel );
        listObjPanel.add( objBox );
        listObjPanel.add( objHtml );

//        listPanel.add( datesPanel );
        listPanel.add( listObjPanel );
        loadDates( "" );
        objPanel.setWidth( "100%" );
        objPanel.setSpacing( 5 );
        objPanel.add( dateBackPanel );
        objPanel.add( repTypeBackPanel );
        objPanel.add(repOptPanel);
        objPanel.add( listPanel );

        kpiOptPanel.setWidth( "200px" );
        kpiOptPanel.setHeight( "25px" );
        kpiOptPanel.setStyleName( "silverBottom" );
        clear = new Label( "Clear Box" );
        clear.addStyleName( "activelink" );
        clear.addClickHandler( new ClickHandler( )
        {
            public void onClick( ClickEvent event )
            {
                kpiBox.setText( "" );
            }
        } );
        kpiOptPanel.add( new InfoWidget( PopupMessage.HELP_MESSAGE, InfoWidget.CLICK_ACTION, "<ul><li>It is possible to write aritmetical operations <br/>over indicators and constants"
                + "<li>Example: AssSucc/AssAtt*100,AssSuccRate. <br/>This will generate two time lines, one for the " + "<br/>operation and another for the raw indicator" +
                // "<li>You can write down the indicator's name if you know it <br/> and do not want to search it at the tree" +
                "<li>Indicators must be separated by comma <br/>without blank spaces</ul>" ) );
        // kpiOptPanel.add(clear);
        clear = new Label( "Clear Selection" );
        clear.addStyleName( "activelink" );
        clear.addClickHandler( new ClickHandler( )
        {
            public void onClick( ClickEvent event )
            {
                kpiBox.setText( "" );
                kpiTree.clearSelection( );
            }
        } );
        kpiOptPanel.add( clear );
        /*
         * clear = new Label("Rebuild Tree"); clear.addStyleName("activelink"); clear.addClickHandler(new ClickHandler() { public void onClick(ClickEvent event) {
         * PopupMessage.showError("Unimplemented"); } }); kpiOptPanel.add(clear);
         */

        kpiPanel.setWidth( "250px" );
        kpiPanel.setSpacing( 5 );        
        kpiPanel.add( kpiOptPanel );
        kpiPanel.add( repOptPanel );
        kpiPanel.add( kpiBox );
        kpiPanel.add( kpiTree );

        northPanel.add( new HTML( "" ) );

        southPanelTest.setSpacing( 2 );
        southPanelTest.setWidth( "100%" );

        southPanelTest.add( repTree, DockPanel.WEST );
        VerticalPanel vp = new VerticalPanel();
        vp.add(objPanel);
        //southPanelTest.add( objPanel, DockPanel.CENTER );
        southPanelTest.add( kpiPanel, DockPanel.EAST );
        //southPanelTest.add( SmartKpis.getVisualizationContainer( ), DockPanel.SOUTH );
        vp.add(SmartKpis.getVisualizationContainer());
        southPanelTest.add(vp, DockPanel.CENTER);
        // mainPanel.add( SmartKpis.getVisualizationContainer( ) );
        mainPanel.add( northPanel );
        mainPanel.add( southPanelTest );
        initWidget( mainPanel );
    }

    public void updateGroupingFunction( )
    {
        System.out.println("grouping");
        
    }
    
    private void loadDates( final String filter )
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
                PopupMessage.showError( "ERROR: " + caught.getMessage( ) );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }
            public void onSuccess( String result )
            {
                dates.setHTML( result );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
                if( filter != null )
                {
                    if( filter.length( ) == 0 )
                        dateBox.setText( "" );
                }
                else
                    dateBox.setText( filter );
            }
        };
        dataSvc.getDatesListBox( filter, callback );
    }

    private void buildReportTypes( )
    {
        repTypePanel.clear( );
        repTypePanel.add( new Label( "Report Type: " ) );
        for( String rep : reportTypes.keySet( ) )
        {
            // System.out.println("Building Radio: " + rep);
            repTypePanel.add( new RadioButton( "Report Type", rep ) );
        }
        if( reportTypes.size( ) > 0 )
        {
            ( ( RadioButton )repTypePanel.getWidget( 1 ) ).setValue( true );
        }
    }

    /**
     * Builds the report tree with data gathered from the remote service
     * 
     * @param categories The categories list that delivers the remote service
     * */
    public void buildReportTree( List<ReportCategory> categories ) {
        for( ReportCategory category : categories ) {
            repTree.addItem( buildCategory( category ) );
        }
    }

    /**
     * Recursively build the reports tree's categories
     * 
     * @param category The category to build
     * */
    public TreeItem buildCategory( ReportCategory category ) {
    	ReportTreeCategory newCat = new ReportTreeCategory(category);
        if(category.getSubCategories() != null) {
            for(ReportCategory subCat : category.getSubCategories()) {
                newCat.addItem(buildCategory(subCat));
            }
        }
        if(category.getItems() != null) {
            for(ReportItem item : category.getItems()) {
                newCat.addItem(new ReportTreeItem(item));
            }
        }
        return newCat;
    }

    /**
     * Manually build the ReportsTree. Only used when the Automatic KPI Tree fails
     * */
    @SuppressWarnings("unused")
    private void buildTestRepTree( )
    {
        TreeItem child;

        //TreeItem roots = new TreeItem( "Radio 2G" );
    	TreeItem roots = new TreeItem();
    	roots.setText("Radio 2G");
        roots.setTitle( "Radio Reports" );
        child = new TreeItem( );
        child.setText( "Sector" );
        child.setTitle( "sector" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Site" );
        child.setTitle( "site" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Prefix" );
        child.setTitle( "prefix" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Region" );
        child.setTitle( "region" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Network" );
        child.setTitle( "network" );
        roots.addItem( child );
        repTree.addItem( roots );

        //roots = new TreeItem( "Radio 3G" );
        roots = new TreeItem();
    	roots.setText("Radio 3G");
        roots.setTitle( "Radio 3G Reports" );
        child = new TreeItem( );
        child.setText( "Sector" );
        child.setTitle( "sectoru" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Site" );
        child.setTitle( "siteu" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Prefix" );
        child.setTitle( "prefixu" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Region" );
        child.setTitle( "regionu" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Network" );
        child.setTitle( "networku" );
        roots.addItem( child );
        repTree.addItem( roots );

        //roots = new TreeItem( "Core" );
        roots = new TreeItem();
        roots.setText("Core");
        roots.setTitle( "Core Reports" );
        child = new TreeItem( );
        child.setText( "Route" );
        child.setTitle( "route" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "MSXOFFICEDIR" );
        child.setTitle( "msxofficedir" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "MSC" );
        child.setTitle( "msc" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "MSX" );
        child.setTitle( "msx" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "MSS" );
        child.setTitle( "mss" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Linkset" );
        child.setTitle( "linkset" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "Link" );
        child.setTitle( "link" );
        roots.addItem( child );
        repTree.addItem( roots );
        child = new TreeItem( );
        child.setText( "LAC" );
        child.setTitle( "lac" );
        roots.addItem( child );

        //roots = new TreeItem( "PS CORE" );
        roots = new TreeItem();
        roots.setText("PS CORE");
        roots.setTitle( "PS CORE Reports" );
        child = new TreeItem( );
        child.setText( "SGSN 3G" );
        child.setTitle( "sgsn3g" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "GGSN 3G" );
        child.setTitle( "ggsn3g" );
        roots.addItem( child );
        child = new TreeItem( );
        child.setText( "BSSGP HUAWEI" );
        child.setTitle( "bssgphuawei" );
        roots.addItem( child );
        repTree.addItem( roots );

        //roots = new TreeItem( "PPAY" );
        roots = new TreeItem();
        roots.setText("PPAY");
        roots.setTitle( "Prepaid Reports" );
        child = new TreeItem( );
        child.setText( "Bonos MG" );
        child.setTitle( "bonosmg" );
        roots.addItem( child );
        repTree.addItem( roots );
    }

    /**
     * Fills the KPI Tree taken from the service
     * */
    private void buildKpiTree( ) {
        kpiTree.clear( );
        if( families == null ) {
            return;
        }
        if( families.size( ) == 0 ) {
            return;
        }
        TreeItem family;
        MultipleStateBox item;
        for( String fam : families.keySet( ) ) {
            //family = new TreeItem( fam );
        	family = new TreeItem();
        	family.setText(fam);
            for( String[] k : families.get( fam ) ) {
                item = new MultipleStateBox( k[ 0 ] );
                item.addClickHandler( msbHandler );
                item.addValueChangeHandler( this );
                // item.addMouseOverHandler(this);
                item.setDescription( k[ 1 ] );
                HorizontalPanel panel = new HorizontalPanel( );
                
                
                if( k[1].length( )>0 ) {
                    InfoWidget iw = new InfoWidget( PopupMessage.HELP_MESSAGE, InfoWidget.CLICK_ACTION, k[1] );
                    panel.add( iw );
                }
                else {
                	panel.add(new HTML());
                }
                panel.add( item );
                
                family.addItem( panel );
            }
            kpiTree.addItem( family );
        }
        MainMenuBar.setStatus( MainMenuBar.NORMAL );
    }

    /**
     * Loads the ReportsMenu Tree
     * */
    private void loadReportMenu( ) {
        // System.out.println(new Date() + " Requesting tree");
        if( dataSvc == null ) {
            dataSvc = SmartKpis.getDataSvc( );
        }
        AsyncCallback<List<ReportCategory>> callback = new AsyncCallback<List<ReportCategory>>( ) {
            public void onFailure( Throwable caught ) {
                PopupMessage.showError( "ERROR Getting Report Menu: " + caught.getMessage( ) );
            }
            public void onSuccess( List<ReportCategory> result ) {
                buildReportTree( result );
            }
        };
        dataSvc.getReportMenu( callback );
    }

    /**
     * Loads the Indicators for a given objectType
     * 
     * @param The object type selected from the ReportsTree
     * */
    private void loadKpis( String objectType ) {
        // System.out.println(new Date() + " Requesting tree");
        if( dataSvc == null ) {
            dataSvc = SmartKpis.getDataSvc( );
        }
        AsyncCallback<LinkedHashMap<String, ArrayList<String[]>>> callback = new AsyncCallback<LinkedHashMap<String, ArrayList<String[]>>>( ) {
            public void onFailure( Throwable caught ) {
                PopupMessage.showError( "ERROR Getting Indicators: " + caught.getMessage( ) );
            }
            public void onSuccess( LinkedHashMap<String, ArrayList<String[]>> result ) {
                families = result;
                buildKpiTree( );
                kpiBox.setText( "" );
            }
        };
        dataSvc.getKpis( objectType, callback );
    }

    /**
     * Gets the HTML text that fills the objects list within the OptimalListBox object
     * 
     * @param objectType The object type selected from the ReportsTree
     * */
    private void loadObjectsListBox( String objectType, final String filter ) {
        MainMenuBar.setStatus( MainMenuBar.LOADING );
        objHtml.setHTML( "<em>Loading... </em>" + objectType );
        // System.out.println(new Date() + " Requested objects from: " + objectType);
        if( dataSvc == null ) {
            dataSvc = SmartKpis.getDataSvc( );
        }
        AsyncCallback<String> callback = new AsyncCallback<String>( ) {
            public void onFailure( Throwable caught ) {
                PopupMessage.showError( "ERROR: " + caught.getMessage( ) );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }
            public void onSuccess( String result ) {
                // System.out.println(new Date() + " Building the list");
                objHtml.setHTML( "<em>Building the list... </em>" );
                objHtml.setHTML( result );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
                if( filter != null ) {
                    if( filter.length( ) == 0 )
                        objBox.setText( "" );
                }
                else
                    objBox.setText( "" );
                // System.out.println(new Date() + " List built");
            }
        };
        dataSvc.getObjectsListBox( objectType, filter, callback );
    }

    /**
     * Loads the available granularities for a selected report
     * 
     * @param objectType The object type selected from the ReportsTree
     * */
    private void loadReportTypes( String objectType ) {
        reportTypes.clear( );
        // System.out.println(new Date() + " Requested report types for: " + objectType);
        if( dataSvc == null ) {
            dataSvc = SmartKpis.getDataSvc( );
        }

        // Set up the callback object.
        AsyncCallback<String[]> callback = new AsyncCallback<String[]>( ) {
            public void onFailure( Throwable caught ) {
                PopupMessage.showError( "ERROR: " + caught.getMessage( ) );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }

            public void onSuccess( String[] result ) {
                for( String string : result ) {
                    // System.out.println(string);
                    reportTypes.put( string.split( "@@" )[ 1 ], string.split( "@@" )[ 0 ] );
                }
                buildReportTypes( );
            }
        };

        dataSvc.getReportTypes( objectType, callback );
    }

    public void reportExport( String objectType, String iniDate, String endDate, String kpis, String objects, String tableType ) {
        MainMenuBar.setStatus( MainMenuBar.LOADING );
        if( dataSvc == null ) {
            dataSvc = SmartKpis.getDataSvc( );
        }

        AsyncCallback<Map<String, String[]>> callback = new AsyncCallback<Map<String, String[]>>( ) {
            public void onFailure( Throwable caught ) {
                PopupMessage.showWarning( "An error occurred while trying to export the report" );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }

            public void onSuccess( Map<String, String[]> result ) {
                SmartKpis.getProcessQueueView( ).updateFilejobsTable( result );
                PopupMessage.showInfo( "Queued report, please visit <a href='#processQueue' target=\"_self\">Reports -> Process Queue</a> to view your files."
                        + "<br/>Or visit <a href='http://10.65.136.11/pfn-2.3.3/choice.php' target=\"_blank\">PHPFile Manager</a>. " + "<br/>Navigate to /export/home/sharepoint/vp_network/MG-Quality/Downloads/Nemesis/Generados/. "
                        + "<br/>The load delay will depend on the amount of data you requested." );
                MainMenuBar.setStatus( MainMenuBar.NORMAL );
            }
        };

        dataSvc.reportExport( objectType, iniDate, endDate, kpis, objects, tableType, SmartKpis.getLoginForm( ).getUserBean( ), callback );
    }

    /**
     * Selection Handler for the ReportsTree
     * 
     * @param event The handled event
     * */
    public void onSelection( SelectionEvent<TreeItem> event ) {
        if( event.getSelectedItem().getTree( ).equals( repTree ) ) {
            if(event.getSelectedItem().getChildCount() == 0) {
                selectedObj = ((ReportTreeItem)event.getSelectedItem()).getCode();
                if(selectedObj.equals("@wait")) {
                	PopupMessage.showHelp("Please wait, categories loading...");
                } else {
//                	PopupMessage.showHelp("Let's gather information: " + selectedObj);
                	loadKpis( selectedObj );
                	loadReportTypes( selectedObj );
                	loadObjectsListBox( selectedObj, "" );
                }
            } else {
            	PopupMessage.showHelp("Category: " + ((ReportTreeItem)event.getSelectedItem()).getCode());
            }
        }
    }

    /**
     * The Click Handler for the report generation events
     * 
     * @param event The click event
     * */
    public void onClick( ClickEvent event ) {
        if( SmartKpis.getVisualization( ).isLocked( ) ) {
            PopupMessage.showWarning( "Please wait for the previous report to finish or reload the application" );
            return;
        }
        if( selectedObj == null ) {
            PopupMessage.showError( "Please select a report first" );
            return;
        }
        String objStr = "", kpiStr = "";
        if( iniDate.getValue( ) == null || endDate.getValue( ) == null ) {
            PopupMessage.showError( "Invalid date format (correct format yyyy-MM-dd), please select the date from the drop down box" );
            return;
        }
        if( kpiStr.length( ) > 0 && kpiBox.getText( ).length( ) > 0 ) {
            kpiStr = kpiBox.getText( ) + kpiStr;
        }
        else if( kpiBox.getText( ).length( ) > 0 ) {
            kpiStr = kpiBox.getText( );
        }
        else if( kpiStr.length( ) > 0 ) {
            kpiStr = kpiStr.substring( 1 );
        }
        else {
            PopupMessage.showError( "At least one measurement from the tree should be selected or specified in the box" );
            return;
        }

        objStr = event.getSource( ).equals( showData ) || event.getSource( ).equals( exportData ) ? objHtml.getSelectedItemsForXml( ) : objHtml.getSelectedItems( );
        boolean buildLineChart = false;
        String dateStr = dates.getSelectedIds( );
        if( dateStr.length( ) > 0 ) {
            dateStr = dateStr.substring( 1 );
            buildLineChart = true;
        }

        if( objStr.length( ) > 0 ) {
            objStr = objStr.substring( 1 );
            //System.out.println(objStr);
        } else {
            PopupMessage.showError( "At least one object from the list should be selected at the list" );
            return;
        }
        String repType = "det";
        for( int i = 1; i < repTypePanel.getWidgetCount( ); i++ ) {
            if( repTypePanel.getWidget( i ) instanceof RadioButton ) {
                if( ( ( RadioButton )repTypePanel.getWidget( i ) ).getValue( ) ) {
                    repType = reportTypes.get( ( ( RadioButton )repTypePanel.getWidget( i ) ).getText( ) );
                    break;
                }
            }
        }
//        String repOpt = "None";
        for( int i = 1; i < repOptPanel.getWidgetCount( ); i++ ) {
            if( repOptPanel.getWidget( i ) instanceof RadioButton ) {
                if( ( ( RadioButton )repOptPanel.getWidget( i ) ).getValue( ) ) {
//                    repOpt = ( ( RadioButton )repOptPanel.getWidget( i ) ).getText( );
                    break;
                }
            }
        }
        if( event.getSource( ).equals( showData ) ) {
            Window.open( "/smartkpis/SmartWidgets/index_flash.html?objectType=" + selectedObj + "&iniDate=" + iniDate.getTextBox( ).getText( ).replace( "-", "" ) + "&endDate=" + endDate.getTextBox( ).getText( ).replace( "-", "" ) + "&kpis=" + kpiStr
                    + "&objects=" + objStr + "&tableType=" + repType + "&format=rawxml", "dataVw", "" );
            return;
        }
        if( event.getSource( ).equals( exportData ) ) {
            // Window.open("/smartkpis/xmldatasup?objectType="+selectedObj+"&iniDate="+iniDate.getTextBox().getText().replace("-", "")+
            // "&endDate="+endDate.getTextBox().getText().replace("-", "")+"&kpis="+kpiStr+"&objects="+objStr+"&tableType="+repType+"&format=xlsxml", "dataVw", "");
            reportExport( selectedObj, iniDate.getTextBox( ).getText( ).replace( "-", "" ), endDate.getTextBox( ).getText( ).replace( "-", "" ), kpiStr, objStr, repType );
            return;
        }
        if( event.getSource( ).equals( addGraph ) ) {
            if( buildLineChart ) {
                SmartKpis.getVisualizationContainer( ).loadJsonData( selectedObj, dateStr, kpiStr, objStr, repType );
            }
            else {
                SmartKpis.getVisualizationContainer( ).loadJsonData( selectedObj, iniDate.getTextBox( ).getText( ), endDate.getTextBox( ).getText( ), kpiStr, objStr, repType );
            }
            return;
        }
        if( event.getSource( ).equals( buildGraph ) ) {
            SmartKpis.setDeleteVisualizationContainer( );
            if( buildLineChart ) {
                SmartKpis.getVisualizationContainer( ).loadJsonData( selectedObj, dateStr, kpiStr, objStr, repType );
            } else {
                SmartKpis.getVisualizationContainer( ).loadJsonData( selectedObj, iniDate.getTextBox( ).getText( ), endDate.getTextBox( ).getText( ), kpiStr, objStr, repType );
            }
            return;
        }
        if( event.getSource( ).equals( clearAll ) ) {
            SmartKpis.setDeleteVisualizationContainer( );
            return;
        }

    }

    /**
     * Change Handler for the KPI Tree
     * 
     * @param The event generated from the KPI Tree
     * */
    public void onValueChange( ValueChangeEvent<String> event ) {
        MultipleStateBox src = ( MultipleStateBox )event.getSource( );
        if( groupObjects.getValue( ) )
            src.setAggValues( );
        else
            src.setBooleanValues( );
        String toAppend = "";
        if( src.getValue( ).equals( MultipleStateBox.CHECKED ) ) {
            toAppend = src.getText( );
            if( src.getValue( ).equals( MultipleStateBox.AVG ) || ((RadioButton)repOptPanel.getWidget( 2 )).getValue( ))
                toAppend = "avg(" + src.getText( ) + ")";
            else if( src.getValue( ).equals( MultipleStateBox.SUM ) || ((RadioButton)repOptPanel.getWidget( 1 )).getValue( ))
                toAppend = "sum(" + src.getText( ) + ")";
            else if( src.getValue( ).equals( MultipleStateBox.MAX ) )
                toAppend = "max(" + src.getText( ) + ")";
            else if( src.getValue( ).equals( MultipleStateBox.MIN ) )
                toAppend = "min(" + src.getText( ) + ")";
            else if( src.getValue( ).equals( MultipleStateBox.UNCHECKED ) )
                toAppend = "";
        }
        String[] kpis = kpiBox.getText( ).split( "," );
        boolean edited = false;
        for( int i = 0; i < kpis.length; i++ ) {
            if( kpis[ i ].replace( "avg(", "" ).replace( "sum(", "" ).replace( "max(", "" ).replace( ")", "" ).equalsIgnoreCase( src.getText( ) ) && !edited ) {
                //System.out.println(kpis[i]);
                kpis[ i ] = toAppend;
                edited = true;
            }
        }
        String kpiStr = "";
        for( String kpi : kpis ) {
            kpiStr += kpi != null ? ( kpi.length( ) > 0 ? "," + kpi : "" ) : "";
        }
        if( !edited && toAppend.length( ) > 0 )
            kpiStr += "," + toAppend;
        kpiBox.setText( kpiStr.length( ) > 0 ? kpiStr.substring( 1 ) : "" );
    }

    public void onMouseOver( MouseOverEvent event ) {
        if( event.getSource( ) instanceof MultipleStateBox ) {
            PopupMessage.showHelp( ( ( MultipleStateBox )event.getSource( ) ).getTitle( ) );
        }
    }

	public void onOpen(OpenEvent<TreeItem> event) {
		if(((ReportTreeCategory)event.getTarget()).getCategory().isReactive()) {
	        MainMenuBar.setStatus( MainMenuBar.LOADING );
	        if(dataSvc == null) {
	            dataSvc = SmartKpis.getDataSvc();
	        }
	        AsyncCallback<ReportCategory> callback = new AsyncCallback<ReportCategory>() {
	            public void onFailure(Throwable caught) {
	                PopupMessage.showError("ERROR: " + caught.getMessage());
	                MainMenuBar.setStatus(MainMenuBar.NORMAL);
	            }
	            public void onSuccess(ReportCategory result) {
	            	boolean found = false;
	            	TreeItem ti, sti;
	            	ReportTreeCategory cat, subCat;
	            	for(int i = 0; i < repTree.getItemCount(); i++) {
	            		ti = repTree.getItem(i);
	            		if(ti.getChildCount() == 0) continue;
	            		cat = ((ReportTreeCategory)ti);
	            		for(int j = 0; j < cat.getChildCount(); j++) {
	            			sti = cat.getChild(j);
	            			if(sti.getChildCount() == 0) continue;
	            			subCat = ((ReportTreeCategory)sti);
		            		if(subCat.getCategory().getCode().equals(result.getCode())) {
		            			subCat.removeItems();
		            			for(ReportItem item : result.getItems()) {
		            				subCat.addItem(new ReportTreeItem(item));
		            			}
		            			found = true;
		            			break;
		            		}
	            		}
	            		if(found) break;
	            	}
	                MainMenuBar.setStatus(MainMenuBar.NORMAL);
//	                PopupMessage.showInfo(trace);
	            }
	        };
	        ((ReportTreeCategory)event.getTarget()).getCategory().setReactive(false);
	        dataSvc.getReactiveCategory(((ReportTreeCategory)event.getTarget()).getCategory(), callback);
		}
	}
}
