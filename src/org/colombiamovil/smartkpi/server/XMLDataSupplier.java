package org.colombiamovil.smartkpi.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.colombiamovil.smartkpi.server.util.QueryUtils;

/**
 * Servlet that handles the generation of data for smart table generation
 * as well as Excel exporting.
 * 
 * @author Dahyr.Vergara
 * */
public class XMLDataSupplier extends HttpServlet {

	private static final long serialVersionUID = 4355956803774079078L;
	private int colorIndex;
    private static final String[] colors = {"FF0000", "0000FF", "00FF00", "666699", "CCCC00", "CC9900", "336600", "000066", "CC0000"};
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		String dataTable = null;
		String objectTable = null;
		String objectIdField = null;
		String dataIdField = null;
		String nameField = null;
		String nameAlias = null;

		String objectType = request.getParameter("objectType");
		String iniDate = request.getParameter("iniDate");
		String endDate = request.getParameter("endDate");
		String kpis = request.getParameter("kpis");
		String objects = request.getParameter("objects");
		String tableType = request.getParameter("tableType");
		String dataFormat = request.getParameter("format") == null ? "rawxml" : request.getParameter("format");
		String groupBy = request.getParameter("groupBy");
		if(groupBy != null) {
			if(groupBy.equalsIgnoreCase("null") || groupBy.equals("-1") || groupBy.equals("")) groupBy = null;
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = NemesisConnector.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(QueryUtils.getReportParametersQuery(objectType, tableType));
			if(rs.next()) {
				objectTable = rs.getString("object_table");
				objectIdField = rs.getString("object_id_field");
				dataIdField = rs.getString("data_id_field");
				nameField = groupBy != null ? groupBy : rs.getString("name_field");
				nameAlias = groupBy != null ? groupBy : rs.getString("name_alias");
				dataTable = rs.getString("data_table");
			}
			// Build query to get objects ids
			//System.out.println(new Date() + " Objects Query: " + QueryUtils.getObjectsQuery(objectIdField, objectTable, nameField, objects));
			String objectTableIds = "";
			rs = stmt.executeQuery(QueryUtils.getObjectsQuery(objectIdField, objectTable, nameField, objects));
			if(rs.next()) {
				objectTableIds = rs.getString("ids");
			}

			// Build query to get chart data crossing information with primary key between object and data tables
			//System.out.println(new Date() + " Data Query: " + QueryUtils.getSimpleReportQuery(dataIdField, nameField, nameAlias, kpis, objectTable, dataTable, objectIdField, iniDate, endDate, objectTableIds));
			rs = stmt.executeQuery(QueryUtils.getSimpleReportQuery(dataIdField, nameField, nameAlias, kpis, objectTable, dataTable, objectIdField, iniDate, endDate, objectTableIds, groupBy));
			//System.out.println(new Date() + " Fetching ResultSet for Data Table");

			ResultSetMetaData meta = rs.getMetaData();

			if(dataFormat.equals("rawxml")) {
				response.setContentType("application/xml;charset=UTF-8");
				printSmartDataXML(out, rs, meta);
			} else if(dataFormat.equals("xlsxml")) {
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment; filename=\""+objectType+"data-"+tableType+"_"+iniDate+"_"+endDate+".xls\"");
				printExcelXML(out, rs, meta);
			} else if(dataFormat.equals("fusionxml")) {
				response.setContentType("application/xml;charset=UTF-8");
				buildLine(buildCategories(rs, meta), objectType, out);
			}
		} catch(SQLException sqle) {
			sqle.printStackTrace();
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			out.close();
		}
	}

	/**
	 * Prints data in proprietary XML format, with <MetaData> tags for meta data and 
	 * <Data> tags for data sets.
	 * 
	 * @param out The print writer to generate the document
	 * @param rs The SQL result set to get the data from
	 * @param meta The meta data
	 * */
	private void printSmartDataXML(PrintWriter out, ResultSet rs, ResultSetMetaData meta) throws SQLException {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>");
		out.println("<TableData version=\"1\">");

		for (int i = 1; i <= meta.getColumnCount(); i++) {
			out.println("<MetaData><Name>"+meta.getColumnName(i)+"</Name><Head>"+meta.getColumnName(i)+"</Head></MetaData>");
		}
		while(rs.next()) {
			out.print("<Data>");
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				out.println("<"+meta.getColumnName(i)+">"+rs.getString(i)+"</"+meta.getColumnName(i)+">");
			}
			out.println("</Data>");
		}

		out.println("</TableData>");
	}

	/**
	 * Prints data in Excel XML format.
	 * 
	 * @param out The print writer to generate the document
	 * @param rs The SQL result set to get the data from
	 * @param meta The meta data
	 * */
	private void printExcelXML(PrintWriter out, ResultSet rs, ResultSetMetaData meta) throws SQLException {
		out.println("<?xml version=\"1.0\"?>");
		out.println("<?mso-application progid=\"Excel.Sheet\"?>");
		out.println("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:html=\"http://www.w3.org/TR/REC-html40\">");
		out.println(" <DocumentProperties xmlns=\"urn:schemas-microsoft-com:office:office\">");
		out.println("  <Author>Dahyr Vergara</Author>");
		out.println("  <LastAuthor>Dahyr Vergara</LastAuthor>");
//		out.println("  <Created>2007-03-15T23:04:04Z</Created>");
		out.println("  <Company>Colombiamovil S.A. E.S.P.</Company>");
//		out.println("  <Version>11.8036</Version>");
		out.println(" </DocumentProperties>");
//		out.println("<ExcelWorkbook xmlns=\"urn:schemas-microsoft-com:office:excel\"> <WindowHeight>6795</WindowHeight> <WindowWidth>8460</WindowWidth> <WindowTopX>120</WindowTopX> <WindowTopY>15</WindowTopY> <ProtectStructure>False</ProtectStructure> <ProtectWindows>False</ProtectWindows> </ExcelWorkbook>");

		out.println(" <Styles>");
		out.println("  <Style ss:ID=\"Default\" ss:Name=\"Normal\">");
		out.println("   <Alignment ss:Vertical=\"Bottom\" />");
		out.println("   <Borders />");
		out.println("   <Font />");
		out.println("   <Interior />");
		out.println("   <NumberFormat />");
		out.println("   <Protection />");
		out.println("  </Style>");
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			out.println("  <Style ss:ID=\""+meta.getColumnName(i)+"\">");
			out.println("   <Font x:Family=\"Swiss\" ss:Bold=\"1\" />");
			out.println("  </Style>");
		}
		out.println(" </Styles>");

		out.println("<Worksheet ss:Name=\"SmartData\">");
		out.println(" <Table ss:ExpandedColumnCount=\""+meta.getColumnCount()+"\" ss:ExpandedRowCount=\"65535\" x:FullColumns=\"1\" x:FullRows=\"1\">");

		out.println("  <Row>");
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			out.println("   <Cell ss:StyleID=\""+meta.getColumnName(i)+"\"><Data ss:Type=\"String\">"+meta.getColumnName(i)+"</Data></Cell>");
		}
		out.println("  </Row>");
		while(rs.next()) {
			out.println("  <Row>");
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				out.println("   <Cell><Data ss:Type=\""+(i<4 ? "String" : "Number")+"\">"+rs.getString(i)+"</Data></Cell>");
			}
			out.println("  </Row>");
		}

		out.println(" </Table>");
		out.println("</Worksheet>");
		out.println("</Workbook>");
	}

    private void buildLine(LinkedHashMap<String, Vector<String>> categories, String objectType, PrintWriter out) {
        int colorId = colorIndex;
        //System.out.println(categories.get("@MINVAL@"));
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        //out.println("<graph xAxisName='Month' yAxisName='Percentage (%)' hovercapbg='FFECAA' hovercapborder='F47E00' formatNumberScale='0' numberSuffix='%25' decimalPrecision='2' showvalues='1' animation='1' yaxisminvalue='99.65' yaxismaxvalue='99.95' numdivlines='3' numVdivlines='0' lineThickness='3' rotateNames='1'>");
        //out.println("<graph bgColor='F1f1f1' caption='Costa' subcaption='Month EV' xaxisname='Month' yaxisname='Value' canvasbgcolor='F1F1F1' formatNumberScale='0' animation='1' numdivlines='3' divLineColor='333333' decimalPrecision='2' showLegend='1' showColumnShadow='1' yAxisMaxValue='100'>");
		//out.println("<graph bgColor='F1f1f1' caption='DBOG01' subcaption='Carried Traffic' xaxisname='Day' yaxisname='Value' canvasbgcolor='F1F1F1' animation='1' numdivlines='3' divLineColor='333333' showvalues='0' decimalPrecision='2' showColumnShadow='1' rotateNames='1'>");
        out.println("<graph bgColor='FFFFFF' yaxisname='"+objectType+"' numdivlines='0' canvasBorderColor='FFFFFF' canvasbgcolor='FFFFFF' animation='1' showValues='0' decimalPrecision='2' showNames='0' rotateNames='1' showYAxisValues='0' showLegend='1' canvasBorderThickness='0' showShadow='0' lineThickness='1'>");
        categories.remove("@MINVAL@");
        for (String category : categories.keySet()) {
            if(category.equalsIgnoreCase("Date - Time")) {
                out.println("<categories>");
                for(String value : categories.get(category)) {
                    out.println("   <category name='"+value+"' />"); //TODO convert to month, week ...
                }
                out.println("</categories>");
            } else {
                out.println("<dataset seriesname='"+category+"' color='"+colors[colorId]+"' alpha='100' anchorAlpha='0' lineThickness='1'>");
                for(String value : categories.get(category)) {
//                    out.println("   <set value='"+value+"' link=\"JavaScript:alert('Setting, "+value+"');\" />");
                    out.println("   <set value='"+value+"' />");
                }
                out.println("</dataset>");
                colorId++;
            }
        }
        out.println("</graph>");
    }

    private LinkedHashMap<String, Vector<String>> buildCategories(ResultSet rs, ResultSetMetaData meta) throws SQLException {
		LinkedHashMap<String, Vector<String>> categories = new LinkedHashMap<String, Vector<String>>();
		int minValue = Integer.MAX_VALUE, maxValue = Integer.MIN_VALUE;
		categories.put("Date - Time", new Vector<String>());
//		for (int i = 4; i <= meta.getColumnCount(); i++) {
//			categories.put(meta.getColumnName(3) + "_" + meta.getColumnName(i), new Vector<String>());
//		}
		while (rs.next()) {
			categories.get("Date - Time").add(rs.getString(1) + " " + rs.getString(2));
			for (int i = 4; i <= meta.getColumnCount(); i++) {
				if(categories.get(meta.getColumnName(i) + "_" + rs.getString(3)) == null) {
					categories.put(meta.getColumnName(i) + "_" + rs.getString(3), new Vector<String>());
				}
				categories.get(meta.getColumnName(i) + "_" + rs.getString(3)).add(rs.getString(i));
				if ((int) rs.getDouble(i) < minValue) minValue = (int) rs.getDouble(i);
				else if ((int) rs.getDouble(i) > maxValue) maxValue = (int) rs.getDouble(i);
			}
		}
		if (minValue == Integer.MAX_VALUE) minValue = 0;
		if (maxValue == Integer.MIN_VALUE) maxValue = 0;
		int sub = maxValue - minValue, power = 1;
		while (sub / power > 1) power = power * 10;
		minValue = (int) (minValue / power) * power;
		maxValue = ((int) (maxValue / power) + 1) * power;
		categories.put("@MINVAL@", new Vector<String>());
		categories.get("@MINVAL@").add(Integer.toString(minValue));
		categories.get("@MINVAL@").add(Integer.toString(maxValue));
		return categories;
	}

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
		return "Handles the generation of XML data for Flex table";
	}
}
