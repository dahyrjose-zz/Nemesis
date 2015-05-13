package org.colombiamovil.smartkpi.client.repots;

import org.colombiamovil.smartkpi.client.ui.charts.LineChartVisualization;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class IpCoreReport extends Composite
{
    VerticalPanel mainPanel = new VerticalPanel( );
    public IpCoreReport( )
    {
        mainPanel.setWidth( "90%" );
        //TIGO
        mainPanel.add( new HTML( "<p class=MsoNormal align=center style='text-align:center'><b style='mso-bidi-font-weight:normal'><span style='font-size:14.0pt;line-height:115%'>Salida internet TIGO<o:p></o:p></span></b></p>" ) );
        LineChartVisualization l0 = new LineChartVisualization( );
        l0.loadJsonData( "routerinterface", "SUBDATE(CURRENT_DATE, INTERVAL 1 DAY)", "CURRENT_DATE", "sum(ifHCInOctets),sum(ifHCOutOctets)","BRMED | Gi2/12 | INTERNET_UNE_1_800Mbps!BRMED | Gi2/24 | INTERNET_UNE_1_800Mbps!BorderRouterBOG3 | Gi2/3 | Internet_UNE2_900M!BorderRouterBOG3 | Gi2/8 | Internet_ETB1_800M!BorderRouterBOG3 | Gi3/8 | Internet_UNE1_900M!BorderRouterBAQ | Gi1/42.107 | ETB2_300_Mbps_GPRS!BorderRouterBAQ | Gi1/45.101 | ETB_300_Mbps_GPRS!BorderRouterBAQ | Gi2/8.155 | INTERNET_UNE_1_600Mbps!BorderRouterBAQ | Gi3/8 | INTERNET_UNE_2_600Mbps", "det" );
        mainPanel.add( l0 );
        
        //Barranquilla
        mainPanel.add( new HTML( "<p class=MsoNormal align=center style='text-align:center'><b style='mso-bidi-font-weight:normal'><span style='font-size:14.0pt;line-height:115%'>Salida internet BAQ<o:p></o:p></span></b></p>" ) );        
        LineChartVisualization l = new LineChartVisualization( );
        l.loadJsonData( "routerinterface", "SUBDATE(CURRENT_DATE, INTERVAL 1 DAY)", "CURRENT_DATE", "sum(ifHCInOctets),sum(ifHCOutOctets)","BorderRouterBAQ | Gi1/42.107 | ETB2_300_Mbps_GPRS!BorderRouterBAQ | Gi1/45.101 | ETB_300_Mbps_GPRS!BorderRouterBAQ | Gi2/8.155 | INTERNET_UNE_1_600Mbps!BorderRouterBAQ | Gi3/8 | INTERNET_UNE_2_600Mbps", "det" );
        mainPanel.add( l );
        
        //Bogota
        mainPanel.add( new HTML( "<p class=MsoNormal align=center style='text-align:center'><b style='mso-bidi-font-weight:normal'><span style='font-size:14.0pt;line-height:115%'>Salida internet BOG3<o:p></o:p></span></b></p>" ) );        
        LineChartVisualization l2 = new LineChartVisualization( );
        l2.loadJsonData( "routerinterface", "SUBDATE(CURRENT_DATE, INTERVAL 1 DAY)", "CURRENT_DATE", "sum(ifHCInOctets),sum(ifHCOutOctets)","BorderRouterBOG3 | Gi2/3 | Internet_UNE2_900M!BorderRouterBOG3 | Gi2/8 | Internet_ETB1_800M!BorderRouterBOG3 | Gi3/8 | Internet_UNE1_900M", "det" );
        mainPanel.add( l2 );
                
        //MED 1400
        mainPanel.add( new HTML( "<p class=MsoNormal align=center style='text-align:center'><b style='mso-bidi-font-weight:normal'><span style='font-size:14.0pt;line-height:115%'>Salida internet MED<o:p></o:p></span></b></p>" ) );
        LineChartVisualization l3 = new LineChartVisualization( );
        l3.loadJsonData( "routerinterface", "SUBDATE(CURRENT_DATE, INTERVAL 1 DAY)", "CURRENT_DATE", "sum(ifHCInOctets),sum(ifHCOutOctets)","BRMED | Gi2/12 | INTERNET_UNE_1_800Mbps!BRMED | Gi2/24 | INTERNET_UNE_1_800Mbps", "det" );
        mainPanel.add( l3 );
        
        
        initWidget( mainPanel );
    }
}
