package org.colombiamovil.smartkpi.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

public class News extends Composite {

	private VerticalPanel panel = new VerticalPanel();
	private FlexTable form = new FlexTable();
	private FlexTable newsList = new FlexTable();

	private ListBox type = new ListBox();
	private DateBox date = new DateBox();
	private TextBox shortTag = new TextBox();
	private TextArea desc = new TextArea();
	public News() {
		panel.setWidth("100%");
		panel.setHeight("100%");

		type.addItem("nms_route", "route");
		type.addItem("nms_linkset", "linkset");
		type.addItem("nms_sector", "sector");

		type.addStyleName("field_input");
		date.addStyleName("field_input");
		shortTag.addStyleName("field_input");
		desc.addStyleName("field_input");
		form.setCellPadding(6);
		form.setWidget(0, 0, new Label("Type"));
		form.setWidget(1, 0, new Label("Date"));
		form.setWidget(2, 0, new Label("Short Tag"));
		form.setWidget(3, 0, new Label("Description"));
		form.setWidget(0, 1, type);
		form.setWidget(1, 1, date);
		form.setWidget(2, 1, shortTag);
		form.setWidget(3, 1, desc);
		panel.add(form);

		newsList.setWidget(0, 0, new Label("Date"));
		newsList.setWidget(0, 1, new Label("Type"));
		newsList.setWidget(0, 2, new Label("Tag"));
		//TODO change for color behavior
		newsList.setWidget(0, 3, new Label("Importance"));
		newsList.setWidget(0, 4, new Label("Remove"));
		panel.add(newsList);
		initWidget(panel);
	}
}
