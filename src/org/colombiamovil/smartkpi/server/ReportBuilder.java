/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.colombiamovil.smartkpi.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Dahyr.Vergara
 */
public class ReportBuilder extends HttpServlet {

	private static final long serialVersionUID = 8152265393195112386L;
	//	private Calendar reportDate;
	private int colorIndex;
	private static final String[] colors = {"FF0000", "0000FF", "00FF00", "666699", "CCCC00", "CC9900", "336600", "000066", "CC0000"};
	/** 
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		//System.out.println(request);
		response.setContentType("application/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String chartType, kpi;
		try {
			chartType = request.getParameter("chartType");
			kpi = request.getParameter("kpi");
			colorIndex = Integer.parseInt(request.getParameter("colorIndex"));
			/*String query = "select x_date, sum(TCH_TRAFF_CARRIED_F) as TCH_Full, sum(TCH_TRAFF_CARRIED_H) as TCH_Half, sum(IT_LOST_TRAFF)*-1 as \"Lost Traffic\" " +
                    "from nms_sector_bhm_voice " +
                    "where x_date in (" +
                    " last_day(date_sub(current_date, interval 1 month)), " +
                    " last_day(date_sub(current_date, interval 2 month)), " +
                    " last_day(date_sub(current_date, interval 3 month)), " +
                    " last_day(date_sub(current_date, interval 4 month))) group by 1";*/
			/*String query = "select x_date, sum(TCH_TRAFF_CARRIED_F) as TCH_Full, sum(TCH_TRAFF_CARRIED_H) as TCH_Half, sum(IT_LOST_TRAFF)*-1 as \"Lost Traffic\" " +
                    "from nms_sector_bhw_voice " +
                    "where x_date in (" +
                    " date_sub(current_date, interval 1 week), " +
                    " date_sub(current_date, interval 2 week), " +
                    " date_sub(current_date, interval 3 week), " +
                    " date_sub(current_date, interval 4 week)) group by 1";*/
			String query = kpi.equals("TCH_Full") ? 
					"select x_date, sum(TCH_TRAFF_CARRIED_F) as TCH_Full from nms_sector_bh_voice " +
					"where x_date between date_sub(current_date, interval 10 day) and date_sub(current_date, interval 1 day) " +
					"group by 1" : kpi.equals("TCH_Half") ? 
							"select x_date, sum(TCH_TRAFF_CARRIED_H) as TCH_Half from nms_sector_bh_voice " +
							"where x_date between date_sub(current_date, interval 10 day) and date_sub(current_date, interval 1 day) " +
							"group by 1" : kpi.equals("TCH_Lost") ? 
									"select x_date, sum(IT_LOST_TRAFF) as TCH_Lost from nms_sector_bh_voice " +
									"where x_date between date_sub(current_date, interval 10 day) and date_sub(current_date, interval 1 day) " +
									"group by 1" : 
										"select x_date, sum(carried_traffic) as Traff_Carr from nms_route_bh " +
										"where x_date between date_sub(current_date, interval 10 day) and date_sub(current_date, interval 1 day) " +
										"group by 1";
			/*String query = "select hour(x_time) as x_date, sum(TCH_TRAFF_CARRIED_F) as TCH_Full, sum(TCH_TRAFF_CARRIED_H) as TCH_Half, sum(IT_LOST_TRAFF)*-1 as \"Lost Traffic\" " +
                    "from nms_sector_det " +
                    "where x_date = date_sub(current_date, interval 1 day) group by 1";*/
			/*String query = "select hour(x_time) as x_date, sum(carried_traffic) as Carr_Traff, sum(offered_traffic) as Off_Traff, sum(carried_calls)*-1 as \"Carried Calls\" " +
                    "from nms_route_det " +
                    "where x_date = date_sub(current_date, interval 1 day) group by 1";*/
			//String query = "select x_date, carried_traffic, offered_traffic, carried_calls " +
			//        "from nms_route_bh d where d.x_date >= 20090601 and id = 634 order by x_date";
			/*String query = "select x_date, traff_w23, traff_w22, traff_w21 from " +
					"(select date_format(x_date, '%a') x_date, carried_traffic as traff_w23 from nms_route inner join nms_route_bh f using(id) where x_date between 20090608 and 20090614 and route = 'DBOG01' order by f.x_date) t1 inner join " +
					"(select date_format(x_date, '%a') x_date, carried_traffic as traff_w22 from nms_route inner join nms_route_bh f using(id) where x_date between 20090601 and 20090607 and route = 'DBOG01' order by f.x_date) t2 using(x_date) inner join " +
					"(select date_format(x_date, '%a') x_date, carried_traffic as traff_w21 from nms_route inner join nms_route_bh f using(id) where x_date between 20090525 and 20090531 and route = 'DBOG01' order by f.x_date) t3 using(x_date)";*/
			/* TODO output your page here
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReportBuilder</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReportBuilder at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
			 */

			//System.out.println(query);
			//            reportDate = Calendar.getInstance();
			if(chartType.equals("stack")) buildStack(buildCategories(query), kpi, out);
			else if(chartType.equals("line")) buildLine(buildCategories(query), kpi, out);
		} finally {
			out.close();
		}
	}

	private void buildLine(LinkedHashMap<String, Vector<String>> categories, String kpi, PrintWriter out) {
		int colorId = colorIndex;
		//System.out.println(categories.get("@MINVAL@"));
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		//out.println("<graph xAxisName='Month' yAxisName='Percentage (%)' hovercapbg='FFECAA' hovercapborder='F47E00' formatNumberScale='0' numberSuffix='%25' decimalPrecision='2' showvalues='1' animation='1' yaxisminvalue='99.65' yaxismaxvalue='99.95' numdivlines='3' numVdivlines='0' lineThickness='3' rotateNames='1'>");
		//out.println("<graph bgColor='F1f1f1' caption='Costa' subcaption='Month EV' xaxisname='Month' yaxisname='Value' canvasbgcolor='F1F1F1' formatNumberScale='0' animation='1' numdivlines='3' divLineColor='333333' decimalPrecision='2' showLegend='1' showColumnShadow='1' yAxisMaxValue='100'>");
		//out.println("<graph bgColor='F1f1f1' caption='DBOG01' subcaption='Carried Traffic' xaxisname='Day' yaxisname='Value' canvasbgcolor='F1F1F1' animation='1' numdivlines='3' divLineColor='333333' showvalues='0' decimalPrecision='2' showColumnShadow='1' rotateNames='1'>");
		out.println("<graph bgColor='FFFFFF' yaxisname='"+kpi.replace("_", " ")+"' yAxisMinValue='"+categories.get("@MINVAL@").get(0)+"' yAxisMaxValue='"+categories.get("@MINVAL@").get(1)+"' numdivlines='0' canvasBorderColor='FFFFFF' canvasbgcolor='FFFFFF' animation='1' showValues='1' decimalPrecision='2' showColumnShadow='1' showNames='0' showYAxisValues='0' showLegend='0' canvasBorderThickness='0'>");
		categories.remove("@MINVAL@");
		for (String category : categories.keySet()) {
			if(category.equalsIgnoreCase("x_date")) {
				out.println("<categories>");
				for(String value : categories.get(category)) {
					out.println("   <category name='"+value+"' />"); //TODO convert to month, week ...
				}
				out.println("</categories>");
			} else {
				out.println("<dataset seriesname='"+category+"' color='"+colors[colorId]+"' alpha='100' anchorAlpha='0' lineThickness='1'>");
				for(String value : categories.get(category)) {
					out.println("   <set value='"+value+"' />");
				}
				out.println("</dataset>");
				colorId++;
			}
		}
		out.println("</graph>");

		/* For testing purposes only
		out.println("<categories>");
        out.println("   <category name='Marzo' />");
        out.println("   <category name='Abril' />");
        out.println("   <category name='Mayo' />");
        out.println("   <category name='Junio' />");
        out.println("</categories>");
        out.println("<dataset seriesname='Disponibilidad' color='0099FF' showValue='1' alpha='100' anchorAlpha='0' lineThickness='2'>");
        out.println("   <set value='99.91' />");
        out.println("   <set value='99.83' />");
        out.println("   <set value='99.90' />");
        out.println("   <set value='99.90' />");
        out.println("</dataset>");
        out.println("<dataset seriesname='Confiabilidad' color='FF8000' showValue='1' alpha='80' anchorAlpha='0' lineThickness='2'>");
        out.println("   <set value='99.76' />");
        out.println("   <set value='99.77' />");
        out.println("   <set value='99.88' />");
        out.println("   <set value='99.87' />");
        out.println("</dataset>");
        out.println("</graph>");*/
	}

	private void buildStack(LinkedHashMap<String, Vector<String>> categories, String kpi, PrintWriter out) {
		int colorId = 0;
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<graph bgColor='F1f1f1' caption='Costa' subcaption='Month EV' xaxisname='Month' yaxisname='Value' canvasbgcolor='F1F1F1' formatNumberScale='0' animation='1' numdivlines='3' divLineColor='333333' decimalPrecision='2' showLegend='1' showColumnShadow='1' yAxisMaxValue='100'>");
		categories.remove("@MINVAL@");
		for (String category : categories.keySet()) {
			if(category.equalsIgnoreCase("x_date")) {
				out.println("   <categories font='Arial' fontSize='10' fontColor='000000'>");
				for(String value : categories.get(category)) {
					out.println("      <category name='"+value+"' />"); //TODO convert to month, week ...
				}
				out.println("   </categories>");
			} else {
				out.println("   <dataset seriesname='"+category+"' color='"+colors[colorId]+"' showValues='0' alpha='100'>");
				for(String value : categories.get(category)) {
					out.println("      <set value='"+value+"' />");
				}
				out.println("   </dataset>");
				colorId++;
			}
		}
		out.println("</graph>");


		/*out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<graph bgColor='F1f1f1' caption='Costa' subcaption='Month EV' xaxisname='Month' yaxisname='Value' canvasbgcolor='F1F1F1' formatNumberScale='0' animation='1' numdivlines='5' divLineColor='333333' decimalPrecision='2' showLegend='1' showColumnShadow='1' yAxisMaxValue='100'>");
        out.println("   <categories font='Arial' fontSize='10' fontColor='000000'>");
        out.println("      <category name='Marzo' hoverText='Marzo'/>");
        out.println("      <category name='Abril' hoverText='Abril'/>");
        out.println("      <category name='Mayo' />");
        out.println("      <category name='Junio' />");
        out.println("   </categories>");
        out.println("   <dataset seriesname='TCH_Full' color='772277' showValues='1' alpha='100'>");
        out.println("      <set value='6757.87' />");
        out.println("      <set value='5839.88' />");
        out.println("      <set value='6696.5' />");
        out.println("      <set value='6713.2' />");
        out.println("   </dataset>");
        out.println("   <dataset seriesname='TCH_Half' color='8888FF' showValues='1' alpha='100'>");
        out.println("      <set value='19005.26' />");
        out.println("      <set value='13915.01' />");
        out.println("      <set value='16999.15' />");
        out.println("      <set value='13759.32' />");
        out.println("   </dataset>");
        out.println("   <dataset seriesname='Lost Traffic' color='FF1111' showValues='1' alpha='100'>");
        out.println("      <set value='-5220' />");
        out.println("      <set value='-213' />");
        out.println("      <set value='-1073' />");
        out.println("      <set value='-282' />");
        out.println("   </dataset>");
//            out.println("   <trendLines>");
//            out.println("      <line startValue='16' endValue='23' color='123456' thickness='1' alpha='30' showOnTop='1' displayValue='Trend 1'/>");
//            out.println("      <line startValue='45' endValue='40' color='0372AB' thickness='1' alpha='30' showOnTop='0' displayValue='Trend 2'/>");
//            out.println("   </trendLines>");
        out.println("</graph>");*/
	}

	private LinkedHashMap<String, Vector<String>> buildCategories(String query) {
		LinkedHashMap<String, Vector<String>> categories = new LinkedHashMap<String, Vector<String>>();
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = NemesisConnector.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

			/*System.out.println("data.setValue(0, "+(3*objcount-1)+", " +
            "'<input type=CHECKBOX id=\""+(3*objcount-2)+"\" name=\""+object+"\" " +
            "onClick=\"javascript:switchChart(this);\">carried_traffic("+object+")');");
            System.out.println("data.setValue(0, "+(3*objcount)+", 'Description for carried_traffic("+object+")');");*/
			for (int i=1; i<=rs.getMetaData().getColumnCount(); i++) {
				categories.put(rs.getMetaData().getColumnName(i), new Vector<String>());
			}
			while (rs.next()) {
				for (int i=1; i<=rs.getMetaData().getColumnCount(); i++) {
					categories.get(rs.getMetaData().getColumnName(i)).add(rs.getString(i));
					if(i > 1) {
						if((int)rs.getDouble(i) < minValue) minValue = (int)rs.getDouble(i);
						else if((int)rs.getDouble(i) > maxValue) maxValue = (int)rs.getDouble(i);
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
        } finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
		if(minValue == Integer.MAX_VALUE) minValue = 0;
		if(maxValue == Integer.MIN_VALUE) maxValue = 0;
		int sub = maxValue - minValue, power = 1;
		while(sub/power > 1) power = power * 10;
		minValue = (int)(minValue / power) * power;
		maxValue = ((int)(maxValue / power) + 1) * power;
		categories.put("@MINVAL@", new Vector<String>());
		categories.get("@MINVAL@").add(Integer.toString(minValue));
		categories.get("@MINVAL@").add(Integer.toString(maxValue));
		return categories;
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/** 
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		processRequest(request, response);
	} 

	/** 
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		processRequest(request, response);
	}

	/** 
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
