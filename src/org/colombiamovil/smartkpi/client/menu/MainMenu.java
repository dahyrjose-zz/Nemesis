package org.colombiamovil.smartkpi.client.menu;

import org.colombiamovil.smartkpi.client.SmartKpis;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Hand-written Main Menu
 * 
 * @deprecated Use MainMenuBar instead
 * */
@Deprecated
public class MainMenu extends Composite implements ClickHandler {

	private FlexTable menu;
	private HorizontalPanel menuPanel = new HorizontalPanel();
	private DialogBox start, reps, dlds;
	private static Image statImg = new Image();
	public static final int NORMAL = 0, LOADING = 1;
	public MainMenu() {
		menuPanel.setWidth("100%");
		menuPanel.setSpacing(0);
		menuPanel.addStyleName("menuBar");

		menu = new FlexTable();
		menu.setCellPadding(0);
		menu.setCellSpacing(0);
		menu.setWidth("330px");
		//menu.setAutoOpen(true);
		//menu.setWidth("100%");
		//menu.setAnimationEnabled(true);

		//buildStartSection();
		buildReportsSection();
		buildDownloadsSection();
		buildExitSection();

		statImg.setSize("128px", "15px");
		statImg.setStyleName("menuImage");
		setStatus(NORMAL);
		menuPanel.add(menu);
		menuPanel.add(statImg);
		initWidget(menuPanel);
	}

	public void buildStartSection() {
		Label label = new Label("Start");
		label.addStyleName("menuCategory");
		label.addClickHandler(this);
		menu.getCellFormatter().setWidth(0, 0, "55px");
		menu.setWidget(0, 0, label);
		start = new DialogBox();
		start.setStyleName("menuItem");
		start.setAutoHideEnabled(true);
		FlexTable dialog = new FlexTable();
		dialog.setStyleName("menuContent");
		dialog.setCellSpacing(5);
		label = new Label("Home");
		label.addClickHandler(this);
		dialog.add(label);
		label = new Label("News");
		label.addClickHandler(this);
		dialog.add(label);
		label = new Label("Help");
		label.addClickHandler(this);
		dialog.add(label);
		label = new Label("About");
		label.addClickHandler(this);
		dialog.add(label);
		start.add(dialog);
	}

	public void buildReportsSection() {
		Label label = new Label("Reports");
		label.addStyleName("menuCategory");
		label.addClickHandler(this);
		menu.getCellFormatter().setWidth(0, 1, "75px");
		menu.setWidget(0, 1, label);
		reps = new DialogBox();
		reps.setStyleName("menuItem");
		reps.setAutoHideEnabled(true);
		FlexTable dialog = new FlexTable();
		dialog.setStyleName("menuContent");
		dialog.setCellSpacing(5);
		label = new Label("Saved Reports");
		label.addClickHandler(this);
		dialog.setWidget(0, 0, label);
		label = new Label("Current Report");
		label.addClickHandler(this);
		dialog.setWidget(1, 0, label);
		label = new Label("New Report");
		label.addClickHandler(this);
		dialog.setWidget(2, 0, label);
		label = new Label("Kpi Screener");
		label.addClickHandler(this);
		dialog.setWidget(3, 0, label);
		label = new Label("Alarms View");
		label.addClickHandler(this);
		dialog.setWidget(4, 0, label);
		//label = new Label("Weekly Reports");
		//label.addClickHandler(this);
		//dialog.add(label);
		reps.add(dialog);
	}

	public void buildDownloadsSection() {
		final Label label = new Label("Export");
		label.addStyleName("menuCategory");
		label.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int left = label.getAbsoluteLeft() + 0;
				int top = label.getAbsoluteTop() + 20;
				dlds.setPopupPosition(left, top);
				dlds.show();
			}
		});
		menu.getCellFormatter().setWidth(0, 2, "100px");
		menu.setWidget(0, 2, label);
		dlds = new DialogBox();
		dlds.setStyleName("menuItem");
		dlds.setAutoHideEnabled(true);
		FlexTable dialog = new FlexTable();
		dialog.setStyleName("menuContent");
		dialog.setCellSpacing(5);
		final Label label1 = new Label("Nemesis Data");
		label1.addClickHandler(this);
		dialog.setWidget(0, 0, label1);
		final Label label2 = new Label("Collector Data");
		label2.addClickHandler(this);
		dialog.setWidget(1, 0, label2);
		dlds.add(dialog);
	}

	public void buildExitSection() {
		Label label = new Label("Exit");
		label.addStyleName("menuCategory");
		label.addClickHandler(this);
		menu.setWidget(0, 3, label);
	}

	public void onClick(ClickEvent event) {
		Label label = (Label)event.getSource();
		String clicked = label.getText();
		int left = label.getAbsoluteLeft() + 0;
		int top = label.getAbsoluteTop() + 20;
		//Window.alert(clicked);
		if(clicked.equals("Start")) {
			start.setPopupPosition(left, top);
			start.show();
		} else if(clicked.equals("Reports")) {
			reps.setPopupPosition(left, top);
			reps.show();
		} else if(clicked.equals("Export")) {
			//dlds.setPopupPosition(left, top);
			//dlds.show();
		} else if(clicked.equals("Saved Reports")) {
			SmartKpis.setScreen("savedreps");
			reps.hide();
		} else if(clicked.equals("Current Report")) {
			//SmartKpis.setScreen("chart");
			//ArrayList<String> list = new ArrayList<String>();
			//list.add("carried_traffic");
			//list.add("traffic_capacity");
			reps.hide();
			//SmartKpis.getVisualization().loadData("nms_route", "20090101", "current_date", "on_service_lines,traffic_capacity", "ACOME1");
			SmartKpis.setScreen("viz");
			//SmartKpis.getVisualization().loadData(list);
		} else if(clicked.equals("Kpi Screener")) {
			SmartKpis.setScreen("kpiscr");
			reps.hide();
		} else if(clicked.equals("Alarms View")) {
			SmartKpis.setScreen("alarms");
			reps.hide();
		} else if(clicked.equals("Weekly Reports")) {
			//SmartKpis.getMotionChart().loadTestData();
			SmartKpis.setScreen("weekly");
			reps.hide();
		} else if(clicked.equals("News")) {
			SmartKpis.setScreen("news");
			start.hide();
		} else if(clicked.equals("New Report")) {
			SmartKpis.setScreen("newRep");
			reps.hide();
		} else if(clicked.equals("Exit")) {
			Window.open("http://redtigo", "_self", "");
		}
	}

	public static void setStatus(int status) {
		switch (status) {
			case NORMAL :
				statImg.setUrl("images/loading/blank.gif");
				break;

			case LOADING :
				statImg.setUrl("images/loading/blue_bert.gif");
				break;

			default :
				break;
		}
	}
}
