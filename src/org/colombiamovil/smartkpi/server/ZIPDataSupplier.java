package org.colombiamovil.smartkpi.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles the generation of data for smart table generation
 * as well as Excel exporting.
 * 
 * @author Dahyr.Vergara
 * */
public class ZIPDataSupplier extends HttpServlet {

	private static final long serialVersionUID = 2668128677080100841L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();

		String requestType = request.getParameter("requestType");
		String dataFormat = request.getParameter("format") == null ? "tsvzip" : request.getParameter("format");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String query = "";
		try {
			if(requestType.equals("collectorDownload") || requestType.equals("nemesisDownload")) {
				String dbName = request.getParameter("dbName");
				String tableName = request.getParameter("tableName");
				String date = request.getParameter("date");
				String zipName = dbName+"-"+tableName+"_"+date+".zip";
				String dbTableName = tableName;
				String whereClause = "";
				if(dbName.equals("siemensCollector")) {
					dbTableName = tableName + "_" + date;
				} else {
					whereClause = " where date(EndTime) = " + date;
				}
				
				conn = NemesisConnector.getConnection(dbName);
				stmt = conn.createStatement();

				query = "select * from " + dbName + "." + dbTableName + whereClause;

				response.setContentType("application/zip;charset=UTF-8");
				response.setHeader("Content-Disposition", "inline; filename=" + zipName);
				//System.out.println(new Date() + " Data Query: " + query);
				rs = stmt.executeQuery(query);
				//System.out.println(new Date() + " Fetching ResultSet for " + zipName);
				ResultSetMetaData meta = rs.getMetaData();

				if(dataFormat.equals("tsvzip")) {
					printCsvZip("\t", zipName, out, rs, meta);
				} else if(dataFormat.equals("csvzip")) {
					printCsvZip(",", zipName, out, rs, meta);
				} else if(dataFormat.equals("ssvzip")) {
					printCsvZip(";", zipName, out, rs, meta);
				}
			}
		} catch(SQLException sqle) {
			sqle.printStackTrace();
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			
				try {rs.close();}catch(Exception e) {}
				try {stmt.close();}catch(Exception e) {}
				try {conn.close();}catch(Exception e) {}
				try {out.close();}catch(Exception e) {}
				
			
			
		}
	}

	/**
	 * Prints CSV Format with custom separator.
	 * 
	 * @param out The print writer to generate the document
	 * @param rs The SQL result set to get the data from
	 * @param meta The meta data
	 * @throws IOException
	 * */
	private void printCsvZip(String separator, String zipName, ServletOutputStream out, ResultSet rs, ResultSetMetaData meta) throws SQLException, IOException {
//		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ZipOutputStream zout = new ZipOutputStream(out);
		//StringBuffer sb = new StringBuffer();
//		String fileName = "tempfile_" + Calendar.getInstance().getTimeInMillis();
//		File file = new File(fileName);
//		PrintWriter fout = new PrintWriter(file);
//		byte[] buf = new byte[1024];

		zout.putNextEntry(new ZipEntry(zipName.replaceAll(".zip", ".txt")));
		String line = "";

		for (int i = 1; i <= meta.getColumnCount(); i++) {
			if(i > 1) line += separator;//sb.append(separator);//zout.write(separator.getBytes());
//			zout.write((meta.getColumnName(i)).getBytes());
			//sb.append(meta.getColumnName(i));
			line += meta.getColumnName(i);
//			fout.print(meta.getColumnName(i));
		}

		zout.write(line.getBytes(), 0, line.getBytes().length);

		while(rs.next()) {
//			zout.write("\n".getBytes());
			//sb.append("\r\n");
			line = "\r\n";
//			fout.println();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				if(i > 1) line += separator;//sb.append(separator);
//				zout.write((rs.getString(i)).getBytes());
				//sb.append(rs.getString(i));
				line += rs.getString(i);
//				fout.print(rs.getString(i));
			}
			zout.write(line.getBytes(), 0, line.getBytes().length);
		}
//		fout.flush();
//		fout.close();
//		FileInputStream fin = new FileInputStream(file);

//		int len;
//		while ((len = fin.read(buf)) > 0) {
//			zout.write(buf, 0, len);
//		}

//		zout.write(sb.toString().getBytes(), 0, sb.toString().getBytes().length);
		zout.closeEntry();
		zout.finish();

//		out.println(out.toString());
		out.flush();
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
