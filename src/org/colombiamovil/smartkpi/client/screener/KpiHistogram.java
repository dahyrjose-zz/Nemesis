package org.colombiamovil.smartkpi.client.screener;

import java.util.ArrayList;

import org.colombiamovil.smartkpi.client.ui.KpiScreener;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class KpiHistogram extends Composite implements ChangeHandler {

	private FlexTable grid = new FlexTable();
	private Label kpiName;
	private Image kpiDesc;
	private TextBox kpiMinVal;
	private TextBox kpiMaxVal;
	private Image kpiDelBtn, kpiUpBtn, kpiDownBtn;
	private HTML kpiHistgm; //Actual KpiHistogram
	private ArrayList<String> kpis;
	private KpiScreener parent;

	public KpiHistogram(KpiScreener parent) {
		this.parent = parent;
		grid.setCellSpacing(10);
		initWidget(grid);
	}

	public void setKpis(ArrayList<String> kpis) {
		this.kpis = kpis;
		grid.clear();
		for (int i = 0; i < kpis.size(); i++) {
			final String kpi = kpis.get(i);
			kpiName = new Label(kpi);
			kpiDesc = new Image("images/question.gif");
			kpiDesc.addStyleName("activelink");
			kpiMinVal = new TextBox();
			kpiMinVal.setSize("5em", "20px");
			kpiMinVal.addStyleName("field_input");
			kpiHistgm = new HTML("<b>range</b>");
			kpiMaxVal = new TextBox();
			kpiMaxVal.setSize("5em", "20px");
			kpiMaxVal.addStyleName("field_input");
			kpiDelBtn = new Image("images/delete.gif");
			kpiDelBtn.addStyleName("activelink");
			kpiUpBtn = new Image("images/up.gif");
			kpiUpBtn.addStyleName("activelink");
			kpiDownBtn = new Image("images/down.gif");
			kpiDownBtn.addStyleName("activelink");

			kpiMinVal.addChangeHandler(this);
			kpiMaxVal.addChangeHandler(this);

			grid.setWidget(i, 0, kpiDesc);
			grid.setWidget(i, 1, kpiName);
			grid.setWidget(i, 2, kpiMinVal);
			grid.setWidget(i, 3, kpiHistgm);
			grid.setWidget(i, 4, kpiMaxVal);
			grid.setWidget(i, 5, kpiDelBtn);
			grid.setWidget(i, 6, kpiUpBtn);
			grid.setWidget(i, 7, kpiDownBtn);

			kpiDelBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					int index = KpiHistogram.this.kpis.indexOf(kpi);
					KpiHistogram.this.kpis.remove(index);
					grid.removeRow(index);
					arrangeArrows();
					//System.out.println(kpis);
					parent.refreshWatchList();
				}
			});
			kpiUpBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					int index = KpiHistogram.this.kpis.indexOf(kpi);
					arrangeArrows();
					KpiHistogram.this.kpis.set(index, KpiHistogram.this.kpis.get(index - 1));
					KpiHistogram.this.kpis.set(index - 1, kpi);
//					setKpis(KpiHistogram.this.kpis);
					//System.out.println(kpis);
					parent.refreshWatchList();
				}
			});
			kpiDownBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					int index = KpiHistogram.this.kpis.indexOf(kpi);
					arrangeArrows();
					KpiHistogram.this.kpis.set(index, KpiHistogram.this.kpis.get(index + 1));
					KpiHistogram.this.kpis.set(index + 1, kpi);
//					setKpis(KpiHistogram.this.kpis);
					//System.out.println(kpis);
					parent.refreshWatchList();
				}
			});
			/*kpiDelBtn.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					int removedIndex = KpiHistogram.this.kpis.indexOf(kpi);
					KpiHistogram.this.kpis.remove(removedIndex);
					grid.removeRow(removedIndex);
					//System.out.println(KpiHistogram.this.kpis);
					parent.refreshWatchList();
				}
			});*/

		}
		arrangeArrows();
	}

	public void addKpi(final String kpi) {
		kpis = parent.kpis;
		kpis.add(kpi);
		kpiName = new Label(kpi);
		kpiDesc = new Image("images/question.gif");
		kpiDesc.addStyleName("activelink");
		kpiMinVal = new TextBox();
		kpiMinVal.setSize("5em", "20px");
		kpiMinVal.addStyleName("field_input");
		kpiHistgm = new HTML("<b>range</b>");
		kpiMaxVal = new TextBox();
		kpiMaxVal.setSize("5em", "20px");
		kpiMaxVal.addStyleName("field_input");
		kpiDelBtn = new Image("images/delete.gif");
		kpiDelBtn.addStyleName("activelink");
		kpiUpBtn = new Image("images/up.gif");
		kpiUpBtn.addStyleName("activelink");
		kpiDownBtn = new Image("images/down.gif");
		kpiDownBtn.addStyleName("activelink");

		int i = grid.getRowCount();

		kpiMinVal.addChangeHandler(this);
		kpiMaxVal.addChangeHandler(this);
		grid.setWidget(i, 0, kpiDesc);
		grid.setWidget(i, 1, kpiName);
		grid.setWidget(i, 2, kpiMinVal);
		grid.setWidget(i, 3, kpiHistgm);
		grid.setWidget(i, 4, kpiMaxVal);
		grid.setWidget(i, 5, kpiDelBtn);
		grid.setWidget(i, 6, kpiUpBtn);
		grid.setWidget(i, 7, kpiDownBtn);
		arrangeArrows();

		kpiDelBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int index = kpis.indexOf(kpi);
				kpis.remove(index);
				grid.removeRow(index);
				arrangeArrows();
				//System.out.println(kpis);
				parent.refreshWatchList();
			}
		});
		kpiUpBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int index = kpis.indexOf(kpi);
				swapRows(index, -1);
				kpis.set(index, kpis.get(index - 1));
				kpis.set(index - 1, kpi);
				arrangeArrows();
//				setKpis(kpis);
				//System.out.println(kpis);
				parent.refreshWatchList();
			}
		});
		kpiDownBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int index = kpis.indexOf(kpi);
				swapRows(index, 1);
				kpis.set(index, kpis.get(index + 1));
				kpis.set(index + 1, kpi);
				arrangeArrows();
//				setKpis(kpis);
				//System.out.println(kpis);
				parent.refreshWatchList();
			}
		});
	}

	private void swapRows(int index, int direction) {
		int max = grid.getRowCount();
		for (int i = 0; i < 8; i++) {
			grid.setWidget(max, i, grid.getWidget(index, i));
			grid.setWidget(index, i, grid.getWidget(index + direction, i));
			grid.setWidget(index + direction, i, grid.getWidget(max, i));
		}
		grid.removeRow(max);
	}
	public String[] getConstraints() {
		ArrayList<String> cons = new ArrayList<String>();
		for (int i = 0; i < kpis.size(); i++) {
			String kpi = kpis.get(i);
			String minVal = ((TextBox)grid.getWidget(i, 2)).getText();
			String maxVal = ((TextBox)grid.getWidget(i, 4)).getText();
			try {
				if(minVal.length() > 0 && maxVal.length() > 0) {
					cons.add(kpi + " between " + Double.parseDouble(minVal) + " and " + Double.parseDouble(maxVal));
				} else if(minVal.length() > 0) {
					cons.add(kpi + " >= " + Double.parseDouble(minVal));
				} else if(maxVal.length() > 0) {
					cons.add(kpi + " <= " + Double.parseDouble(maxVal));
				}
			} catch(NumberFormatException nfe) {
				//TODO Handle wrong numbers
				//System.out.println("Wrong numbers");
			}
		}
		return cons.toArray(new String[0]);
	}

	private void arrangeArrows() {
		for (int i = 0; i < grid.getRowCount(); i++) {
			//System.out.println(i + " of " + grid.getRowCount());
			grid.getWidget(i, 6).setVisible(i != 0);
			grid.getWidget(i, 7).setVisible(i != (grid.getRowCount() - 1));
		}
		//System.out.println();
	}

	public void clear() {
		parent.kpis.clear();
		int count = grid.getRowCount() - 1;
		while(count>=0) {
			grid.removeRow(count--);
		}
	}

	public void onChange(ChangeEvent event) {
		parent.refreshWatchList();
	}
}
