package org.colombiamovil.smartkpi.client.screener;

import java.util.ArrayList;
import java.util.Date;

import org.colombiamovil.smartkpi.client.ui.KpiScreener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class KpiScreenerOptions extends Composite implements ClickHandler, FocusHandler {

	private FlexTable grid = new FlexTable();
	private Label optsLbl;
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	private SuggestBox objectTypes;
	private DateBox dateField = new DateBox();
	private Image reload = new Image("images/add.gif");
	private Label loadKpis = new Label("Load Indicators");
	private static ArrayList<String> types = new ArrayList<String>();
	private KpiScreener parent;

	@SuppressWarnings("deprecation")
	public KpiScreenerOptions(KpiScreener parent) {
		this.parent = parent;
		grid.setCellSpacing(5);

		types.add("nms_link_bh");
		types.add("nms_linkset_bh");
		types.add("nms_msc_bh");
		types.add("nms_route_bh");
		types.add("nms_sector_bh_voice");
		types.add("nms_sectoru_bh_voice");
		types.add("nms_site_bh");
		types.add("nms_siteu_bh");
		oracle.addAll(types);

		objectTypes = new SuggestBox(oracle);
		objectTypes.addStyleName("field_input");
		dateField.addStyleName("field_input");
		dateField.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat("yyyy-MM-dd")));
		Date today = new Date();
		//dateField.setValue(new Date(today.getDate() - 1));
		dateField.getTextBox().setText((1900 + today.getYear()) + "-" + fillZero(today.getMonth()) + "-" + fillZero(today.getDate() - 1));
		//dateField.getTextBox().setText("2009-08-03");
		reload.addStyleName("activelink");
		loadKpis.addClickHandler(this);
		loadKpis.addStyleName("activelink");
		objectTypes.setText("nms_route_bh");
		objectTypes.getTextBox().addFocusHandler(this);

		optsLbl = new Label("Select object type");
		grid.setWidget(0, 0, optsLbl);
		optsLbl = new Label("Enter date");
		grid.setWidget(0, 1, optsLbl);
		grid.setWidget(1, 0, objectTypes);
		grid.setWidget(1, 1, dateField);
		grid.setWidget(1, 2, loadKpis);

		initWidget(grid);
	}

	public String fillZero(int val) {
		return val == 0 ? "01" : val < 10 ? "0" + val : "" + val;
	}

	public String getObjType() {
		return objectTypes.getText();
	}

	public String getDate() {
		return dateField.getTextBox().getText();
	}

	public void onClick(ClickEvent event) {
		//objectTypes.showSuggestionList();
		if(types.contains(objectTypes.getText())) {
			//Window.alert("Selected: " + objectTypes.getText());
		} else {
			Window.alert("Unknown object type, please select one from the list");
			return;
		}
		if(dateField.getValue() != null) {
			//Window.alert("Selected: " + dateField.getTextBox().getText());
		} else {
			Window.alert("Invalid date format (correct format yyyy-MM-dd), please select the date from the drop down box");
			return;
		}
		parent.loadKpis(objectTypes.getText().split("_")[1], dateField.getTextBox().getText());
	}

	public void onFocus(FocusEvent event) {
		objectTypes.showSuggestionList();
	}
}
