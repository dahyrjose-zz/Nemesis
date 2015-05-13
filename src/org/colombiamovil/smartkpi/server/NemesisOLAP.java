package org.colombiamovil.smartkpi.server;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.layout.CellSetFormatter;
import org.olap4j.layout.RectangularCellSetFormatter;


public class NemesisOLAP {

	private static Context ctx;
	private static BasicDataSource ds;
	private static boolean enabled;

	static {
		try {
			ctx = new InitialContext();
			if (ctx == null) throw new Exception("ERROR: No Initial Contex ");

			Properties olapProperties = (Properties) ctx.lookup("NemesisOLAP");

			Class.forName(olapProperties.getProperty("driver"));
			ds = new BasicDataSource();
			ds.setDriverClassName(olapProperties.getProperty("driver"));
			ds.setUrl(olapProperties.getProperty("url"));
			ds.setUsername(olapProperties.getProperty("user"));
			ds.setPassword(olapProperties.getProperty("password"));
			try {
				ds.setMaxActive(Integer.parseInt(olapProperties.getProperty("max_active")));
				ds.setMaxIdle(Integer.parseInt(olapProperties.getProperty("max_idle")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("INIT Data source Using JDNI file OLAP connection ");
			System.out.println("PROPERTIES OK ");

			if (ds == null) throw new Exception("ERROR: No Data Source ");

			System.out.println("Pool enabled OLAP OK");
			setEnabled(true);
		} catch (Exception e) {
			try {
				Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			ds = new BasicDataSource();
			ds.setDriverClassName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
			ds.setUrl("jdbc:xmla:Server=http://desanalytics01/OLAP/msmdpump.dll;Catalog=NEMESYS");
			ds.setUsername("DESANALYTICS01\\appssrs");
			ds.setPassword("Tigo.2012");
			ds.setMaxActive(5);

			if (ds == null) {
				System.out.println("There was an error initiating the pool OLAP: turning it disabled");
				e.printStackTrace();
				setEnabled(false);
			} else {
				System.out.println("Container's pool disabled, using standalone OLAP pool");
				setEnabled(true);
			}
		}
		try {
			System.out.println(new Date() + " Starting Connection Test");
			Connection con = getConnection();
			con.close();
			System.out.println(new Date() + " Finalized Connection Test");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized Connection getConnection() throws SQLException {
		System.out.println(new Date() + " Getting Connection from Pool");
		return ds.getConnection();
	}
	
	public NemesisOLAP() throws ClassNotFoundException, SQLException {
		long start = System.currentTimeMillis();
		System.out.println((System.currentTimeMillis() - start) + ": " + "Started");
		/*try {
		Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");

	} catch (Exception e) {
		e.printStackTrace();
		// TODO: handle exception
	}*/
//	OlapConnection con = (OlapConnection)DriverManager.getConnection("jdbc:xmla:Server=http://medanalytics01/OLAP/msmdpump.dll;Catalog=NEMESYS", 
//			"MEDANALYTICS01\\appssrs", "Tigo.2012");
	

	
      Connection connection = getConnection();
      OlapConnection oc = connection.unwrap(OlapConnection.class);

	
	System.out.println("Connetion  pool  glassfish ");

	
	
	System.out.println((System.currentTimeMillis() - start) + ": " + "Connected");
//	Cube cube = oc.getOlapSchema().getCubes().get("RanDetailed");
//	System.out.println((System.currentTimeMillis() - start) + ": " + "Got Cube");
//	System.out.println("Detail Dimensions:");
//	for(Dimension d : cube.getDimensions()) {
//		System.out.print(d.getCaption() + "\t");
//	}
//	System.out.println();
//	System.out.println("Geography Hierarchies:");
//	for(Hierarchy h : cube.getDimensions().get("Geography").getHierarchies()) {
//		System.out.print(h.getCaption() + "\t");
//	}
//	System.out.println();
//	System.out.println((System.currentTimeMillis() - start) + "[Cell].[Vendor].Members:");
//	for(Member m : cube.getDimensions().get("Cell").getHierarchies().get("Vendor").getDefaultMember().getChildMembers()) {
//		System.out.print(m.getCaption() + "\t");
//	}
	System.out.println();
	System.out.println((System.currentTimeMillis() - start) + ": " + "Getting Statement");
	OlapStatement stmt = oc.createStatement();
	System.out.println((System.currentTimeMillis() - start) + ": " + "Running Query");
	CellSet results = stmt.executeOlapQuery(
			"SELECT { } ON 0, "
			+ "TOPCOUNT([Cell].[Cell].Children, 10) ON 1 "
			+ "FROM [Ran] " 
			+ "WHERE  [Geography].[Territory].&[COSTA] " 
//			"SELECT [Measures].[Cell Value] ON 0, "
//			+ "NONEMPTY ( CrossJoin ( "
//			+ "{ [Indicator].[Indicator Agg].&[AVAIL_TRAFF_CHANN_FULL], [Indicator].[Indicator Agg].&[AVAIL_TRAFF_CHANN_HALF] }, "
//			+ "{ [Cell].[Cell].[CELL:BSCBAR09/BAR0036/0], [Cell].[Cell].[CELL:BSCBAR09/BAR0036/1], [Cell].[Cell].[CELL:BSCBAR09/BAR0036/2] }, "
//			+ "{ [Date].[Date].&[2015-02-03T00:00:00] : [Date].[Date].&[2015-02-18T00:00:00] }, "
//			+ "{ [Hour].[Hour].Children } "
//			+ "),  [Measures].[Cell Value] ) ON 1 "
//			+ "FROM [RanDetailed] "
			);
	System.out.println((System.currentTimeMillis() - start) + ": " + "Runned Query");
	CellSetFormatter formatter = new RectangularCellSetFormatter(false);
	formatter.format(results, new PrintWriter(System.out, true));
	System.out.println("Fethed with cell formatter: " + (System.currentTimeMillis() - start));


	System.out.println((System.currentTimeMillis() - start) + ": " + "Running Query");
	results = stmt.executeOlapQuery(
			"SELECT { } ON 0, "
			+ "TOPCOUNT([Cell].[Cell].Children, 10) ON 1 "
			+ "FROM [Ran] " 
			+ "WHERE  [Geography].[Territory].[COSTA] " 
//			"SELECT [Measures].[Cell Value] ON 0, "
//			+ "NONEMPTY ( CrossJoin ( "
//			+ "{ [Indicator].[Indicator Agg].&[AVAIL_TRAFF_CHANN_FULL], [Indicator].[Indicator Agg].&[AVAIL_TRAFF_CHANN_HALF] }, "
//			+ "{ [Cell].[Cell].[CELL:BSCBAR09/BAR0036/0], [Cell].[Cell].[CELL:BSCBAR09/BAR0036/1], [Cell].[Cell].[CELL:BSCBAR09/BAR0036/2] }, "
//			+ "{ [Date].[Date].&[2015-02-03T00:00:00] : [Date].[Date].&[2015-02-18T00:00:00] }, "
//			+ "{ [Hour].[Hour].Children } "
//			+ "),  [Measures].[Cell Value] ) ON 1 "
//			+ "FROM [RanDetailed] "
			);
//	System.out.println((System.currentTimeMillis() - start) + ": " + "Runned Query");
//	formatter = new RectangularCellSetFormatter(false);
//	formatter.format(results, new PrintWriter(System.out, true));
//	System.out.println("Fethed with cell formatter: " + (System.currentTimeMillis() - start));

	
//	results = stmt.executeOlapQuery(
//			"SELECT [Measures].[Cell Value] ON 0, "
//			+ "NONEMPTY ( CrossJoin ( "
//			+ "{ [Indicator].[Indicator Agg].&[AVAIL_TRAFF_CHANN_FULL], [Indicator].[Indicator Agg].&[AVAIL_TRAFF_CHANN_HALF] }, "
//			+ "{ [Cell].[Cell].[CELL:BSCBAR09/BAR0036/0], [Cell].[Cell].[CELL:BSCBAR09/BAR0036/1], [Cell].[Cell].[CELL:BSCBAR09/BAR0036/2] }, "
//			+ "{ [Date].[Date].&[2015-02-03T00:00:00] : [Date].[Date].&[2015-02-18T00:00:00] }, "
//			+ "{ [Hour].[Hour].Children } "
//			+ "),  [Measures].[Cell Value] ) ON 1 "
//			+ "FROM [RanDetailed] "
//			);
//	System.out.println((System.currentTimeMillis() - start) + ": " + "Runned Query");
		Cell currentCell;
		for (Position axis1 : results.getAxes().get(1).getPositions()) {
//			System.out.println("Value: " + axis1.getMembers().get(0).getCaption());
			System.out.println("Value: " + axis1.getMembers().get(0).getHierarchy().getCaption());
			for (Position axis0 : results.getAxes().get(0).getPositions()) {
				currentCell = results.getCell(axis1, axis0);
				Object value = currentCell.getValue();
				System.out.println("\t: " + axis0.getMembers().get(0).getCaption() + " | " + 
						axis0.getMembers().get(1).getCaption() + " | " + 
						axis0.getMembers().get(2).getCaption() + ": " + 
						(value == null ? "null" : value.toString()));
			}
		}
		System.out.println("Fethed with for - loop: " + (System.currentTimeMillis() - start));
		System.out.println("END: " + (System.currentTimeMillis() - start));

	}

	public static void main(String args[]) {
		try {
			new NemesisOLAP();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection newConnection() throws SQLException {
		return getConnection();
	}
	
	public static void setEnabled(boolean enabled) {
		NemesisOLAP.enabled = enabled;
	}

	/**
	 * Getting the pool's state
	 * */
	public static boolean isEnabled() {
		return enabled;
	}
}
