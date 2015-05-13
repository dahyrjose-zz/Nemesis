package org.colombiamovil.smartkpi.server.chart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class ChartDataElement {
	private TreeMap<Date, LinkedHashMap<String, ChartTimeLine>> map;
	private List<String> mappedTimeLines = new LinkedList<String>();
	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat sdfDtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdfAdf = new SimpleDateFormat("EEE MMM dd z yyyy");

	public ChartDataElement() {
		map = new TreeMap<Date, LinkedHashMap<String, ChartTimeLine>>();
	}

	/**
	 * Puts a new chart time element and builds the structure for that item according to the mapped objects
	 * */
	public void put(Date date, ChartTimeLine object) {
		if (getMap().get(date) == null) {
			LinkedHashMap<String, ChartTimeLine> objMap = new LinkedHashMap<String, ChartTimeLine>();
			for (String obj : mappedTimeLines) {
				objMap.put(obj, null);
			}
			getMap().put(date, objMap);
		}
		getMap().get(date).put(object.getLineName(), object);
	}

	/**
	 * @return the map
	 */
	public TreeMap<Date, LinkedHashMap<String, ChartTimeLine>> getMap() {
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(TreeMap<Date, LinkedHashMap<String, ChartTimeLine>> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return buildChartJsonData();
	}

	public void setMappedTimeLines(List<String> mappedObjects) {
		this.mappedTimeLines = mappedObjects;
	}

	public List<String> getMappedTimeLines() {
		return mappedTimeLines;
	}

	public String buildChartJsonData() {
		StringBuffer cols = new StringBuffer();
		StringBuffer rows = new StringBuffer();
		rows.append("rows:[");
		cols.append("cols:[{label:'Date',type:'datetime'},");
		LinkedHashMap<String, ChartTimeLine> hm;
		ChartTimeLine tl;
		int i = 1;
		for (String line : mappedTimeLines) {
			cols.append("{label:'" + line + "',type:'number'},{label:'h" + i + "',type:'string'},{label:'b" + i + "',type:'string'},");
			i++;
		}
		//cols = cols.substring(0, cols.length() - 1) + "]";
		cols.replace(cols.length() - 1, cols.length(), "]");
		for (Date key : getMap().keySet()) {
			hm = getMap().get(key);
			cal.setTime(key);
			rows.append("{c:[{v: new Date("+cal.get(Calendar.YEAR)+", "+cal.get(Calendar.MONTH)+", "+cal.get(Calendar.DAY_OF_MONTH)+", "+
				cal.get(Calendar.HOUR_OF_DAY)+", "+cal.get(Calendar.MINUTE)+", "+cal.get(Calendar.SECOND)+")}");
			for (String valKey : hm.keySet()) {
				tl = hm.get(valKey);
				if(tl == null) {
					rows.append(",,,");
				} else {
					rows.append("," + tl.getJsonArray());
				}
			}
			rows.append("]},");
		}
		rows.replace(rows.length() - 1, rows.length(), "]");
		return "{"+cols.toString()+","+rows.toString()+"}";
	}
	/**
	 * Este metodo se contruye puesto que se necesito que no tenga {label:'h"/b,
	 * No se porque el creador de la aplicacion lo puso.
	 * (este se usa para el LineChart (del listado de fechas))
	 * @return
	 */
    public String buildChartJsonDataComparative() {
        StringBuffer cols = new StringBuffer();
        StringBuffer rows = new StringBuffer();
        rows.append("rows:[");
        cols.append("cols:[{label:'Date',type:'datetime'},");
        LinkedHashMap<String, ChartTimeLine> hm;
        ChartTimeLine tl;
        int i = 1;
        for (String line : mappedTimeLines) {
            cols.append("{label:'" + line + "',type:'number'},");
            i++;
        }
        //cols = cols.substring(0, cols.length() - 1) + "]";
        cols.replace(cols.length() - 1, cols.length(), "]");
        for (Date key : getMap().keySet()) {
            hm = getMap().get(key);
            cal.setTime(key);
            rows.append("{c:[{v: new Date("+cal.get(Calendar.YEAR)+", "+cal.get(Calendar.MONTH)+", "+cal.get(Calendar.DAY_OF_MONTH)+", "+
                cal.get(Calendar.HOUR_OF_DAY)+", "+cal.get(Calendar.MINUTE)+", "+cal.get(Calendar.SECOND)+")}");
            for (String valKey : hm.keySet()) {
                tl = hm.get(valKey);
                if(tl == null) {
                    rows.append(",");
                } else {
                    rows.append(",{v:" + tl.getValue( )+"}");
                }
            }
            rows.append("]},");
        }
        rows.replace(rows.length() - 1, rows.length(), "]");
        return "{"+cols.toString()+","+rows.toString()+"}";
    }

	public String buildChartJsonDataToAppend() {
		StringBuffer rows = new StringBuffer();
		rows.append("[");
		LinkedHashMap<String, ChartTimeLine> hm;
		ChartTimeLine tl;
		for (Date key : getMap().keySet()) {
			hm = getMap().get(key);
			cal.setTime(key);
			//rows += "[new Date("+cal.get(Calendar.YEAR)+", "+cal.get(Calendar.MONTH)+", "+cal.get(Calendar.DAY_OF_MONTH)+", "+
			//	cal.get(Calendar.HOUR_OF_DAY)+", "+cal.get(Calendar.MINUTE)+", "+cal.get(Calendar.SECOND)+")";
			//rows += "[new Date("+cal.get(Calendar.YEAR)+", "+cal.get(Calendar.MONTH)+", "+cal.get(Calendar.DAY_OF_MONTH)+", "+
			//	cal.get(Calendar.HOUR_OF_DAY)+", "+cal.get(Calendar.MINUTE)+", "+cal.get(Calendar.SECOND)+")";
			rows.append("[" + sdfAdf.format(key));
			for (String valKey : hm.keySet()) {
				tl = hm.get(valKey);
				if(tl == null) {
					rows.append(",null,null,null");
				} else {
					rows.append("," + tl.getJsonArrayToAppend());
				}
			}
			rows.append("],");
		}
		rows.replace(rows.length() - 1, rows.length(), "]");
//		System.out.println(rows);
		return rows.toString();
	}

	public String[][] buildChartArrayData() {
		List<String[]> data = new ArrayList<String[]>();
		data.add(0, new String[] {"-1", getTimeLinesText(), Integer.toString(getMap().size())});
		//String rows = "[";
		LinkedHashMap<String, ChartTimeLine> hm;
		ChartTimeLine tl;
		int i = 0;
		for (Date key : getMap().keySet()) {
			hm = getMap().get(key);
			//cal.setTime(key);
			//rows += "[" + sdfAdf.format(key);
			data.add(new String[] {Integer.toString(i), "0", sdfDtf.format(key)});
			int j = 1;
			for (String valKey : hm.keySet()) {
				tl = hm.get(valKey);
				if(tl == null) {
					//rows += ",null,null,null";
				} else {
					//rows += "," + tl.getJsonArrayToAppend();
					data.add(new String[] {Integer.toString(i), Integer.toString(j), Double.toString(tl.getValue())});
					if(tl.getAnnotTitle()!=null) data.add(new String[] {Integer.toString(i), Integer.toString(j+1), Double.toString(tl.getValue())});
					if(tl.getAnnotBody()!=null) data.add(new String[] {Integer.toString(i), Integer.toString(j+2), Double.toString(tl.getValue())});
				}
				j += 3;
			}
			//rows += "],";
			i++;
		}
		return data.toArray(new String[data.size()][]);
	}

	public String buildChartEmptyJsonData() {
		return "null";
	}

	public String getTimeLinesText() {
		String linesText = "";
		for (String line : getMappedTimeLines()) {
			linesText += "," + line;
		}
		return linesText.substring(1);
	}
}
