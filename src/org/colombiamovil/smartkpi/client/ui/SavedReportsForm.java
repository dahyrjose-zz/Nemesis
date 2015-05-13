package org.colombiamovil.smartkpi.client.ui;

import java.util.Date;
import java.util.List;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.ui.savedreports.SavedReportBean;
import org.colombiamovil.smartkpi.client.ui.savedreports.SavedReportLink;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SavedReportsForm extends Composite implements ClickHandler {

	private VerticalPanel mainPanel, northPanel;
	private HorizontalPanel menuPanel;
	private FlexTable savedReportsTable;
	private FlexTable histAlarmsTable;
	private Label refreshReports, resultSummary;

	private NemesisServiceAsync svc = SmartKpis.getDataSvc();

	public SavedReportsForm() {
		mainPanel = new VerticalPanel();
		mainPanel.setSpacing(10);
		mainPanel.setWidth("100%");

		northPanel = new VerticalPanel();
		northPanel.setWidth("100%");
		northPanel.addStyleName("watchList");

		refreshReports = new Label("Refresh List");
		refreshReports.addClickHandler(this);
		refreshReports.addStyleName("activelink");
		resultSummary = new Label("");
		resultSummary.addClickHandler(this);
		menuPanel = new HorizontalPanel();
		menuPanel.setSpacing(10);
		menuPanel.add(refreshReports);
		menuPanel.add(resultSummary);

		savedReportsTable = new FlexTable();
		savedReportsTable.setWidth("100%");
		savedReportsTable.setCellPadding(2);
		savedReportsTable.setCellSpacing(0);
		//cfgAlarmsTable.setWidth("100%");
		savedReportsTable.addStyleName("watchList");
		savedReportsTable.getRowFormatter().addStyleName(0, "watchListHeader");
		//savedReportsTable.setText(0, 0, "Report Owner");
		savedReportsTable.setText(0, 0, "Data table");
		savedReportsTable.setText(0, 1, "Relative");
		savedReportsTable.setText(0, 2, "Ini Date");
		savedReportsTable.setText(0, 3, "End Date");
		savedReportsTable.setText(0, 4, "Objects");
		savedReportsTable.setText(0, 5, "Kpis");
		savedReportsTable.setText(0, 6, "Creation Date");

		histAlarmsTable = new FlexTable();
		histAlarmsTable.setCellPadding(0);
		histAlarmsTable.setCellSpacing(0);
		histAlarmsTable.addStyleName("watchList");
		histAlarmsTable.getRowFormatter().addStyleName(0, "watchListHeader");
		histAlarmsTable.setText(0, 0, "Alarm Code");
		histAlarmsTable.setText(0, 1, "Alarm Title");
		//histAlarmsTable.setText(0, 2, "Time");
		histAlarmsTable.setText(0, 2, "Alarmed Elements");
		//histAlarmsTable.setText(0, 4, "Value");
		//histAlarmsTable.setVisible(false);

		northPanel.add(menuPanel);
		mainPanel.add(northPanel);
		mainPanel.add(savedReportsTable);
		//mainPanel.add(histAlarmsTable);
		loadSavedReports();
		initWidget(mainPanel);
	}

	public void loadSavedReports() {
		if(svc == null) {
			svc = SmartKpis.getDataSvc();
		}
		AsyncCallback<List<SavedReportBean>> callback = new AsyncCallback<List<SavedReportBean>>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError(caught.getMessage());
			}

			public void onSuccess(List<SavedReportBean> result) {
				updateReportsTable(result);
			}
		};
		svc.getSavedReports(SmartKpis.getLoginForm().getUserBean(), callback);
	}

	private void updateReportsTable(List<SavedReportBean> savedReports) {
		clearTable(savedReportsTable);
		String strTemp;
		int i = 0;
		while(savedReportsTable.getRowCount() > 1) {
			savedReportsTable.removeRow(savedReportsTable.getRowCount() - 1);
		}
		for (final SavedReportBean rep : savedReports) {
			final Label report, iniDateLabel, endDateLabel, objects, kpis, creationDate;
			final CheckBox relativeBox;
			savedReportsTable.getRowFormatter().addStyleName(i + 1, (i % 2 != 0 ? "evenRow" : "oddRow"));
			report = new SavedReportLink(rep.getItemLabel() + "("+rep.getTypeDescription()+")", rep);
			report.addStyleName("activelink");
			report.addClickHandler(this);
			iniDateLabel = new Label(rep.getIniFixDate());
			iniDateLabel.setWidth("100px");
			endDateLabel = new Label(rep.getEndFixDate());
			endDateLabel.setWidth("100px");
			strTemp = rep.getObjects();
			if(strTemp.length() > 30) {
				objects = new Label(strTemp.substring(0, 30) + " ... ");
			} else {
				objects = new Label(strTemp);
			}
			objects.setTitle(strTemp);
			strTemp = rep.getKpis();
			if(strTemp.length() > 30) {
				kpis = new Label(strTemp.substring(0, 30) + " ... ");
			} else {
				kpis = new Label(strTemp);
			}
			kpis.setTitle(strTemp);
			creationDate = new Label(rep.getCreationDate().substring(0, 19));
			relativeBox = new CheckBox();
			relativeBox.setValue(true);
			rep.setRelative(true);
			relativeBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(event.getValue()) {
						iniDateLabel.setText(rep.getIniFixDate());
						endDateLabel.setText(rep.getEndFixDate());
					} else {
						iniDateLabel.setText(rep.getIniDate());
						endDateLabel.setText(rep.getEndDate());
					}
					rep.setRelative(event.getValue());
				}
			});
			savedReportsTable.getCellFormatter().setAlignment(i + 1, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
			savedReportsTable.setWidget(i + 1, 0, report);
			savedReportsTable.setWidget(i + 1, 1, relativeBox);
			savedReportsTable.setWidget(i + 1, 2, iniDateLabel);
			savedReportsTable.setWidget(i + 1, 3, endDateLabel);
			savedReportsTable.setWidget(i + 1, 4, objects);
			savedReportsTable.setWidget(i + 1, 5, kpis);
			savedReportsTable.setWidget(i + 1, 6, creationDate);
			i++;
		}
		resultSummary.setText("Loaded " + savedReports.size() + " saved reports (Last update: " + 
				DateTimeFormat.getMediumDateTimeFormat().format(new Date()) + ")");
	}

	public void clearTable(FlexTable table) {
		int rows = table.getRowCount();
		rows--;
		while (rows >= 1) {
			table.removeRow(rows--);
		}
	}

	public void onClick(ClickEvent event) {
		if(event.getSource().equals(refreshReports)) {
			loadSavedReports();
		} else {
			if(event.getSource() instanceof SavedReportLink) {
				SavedReportLink source = (SavedReportLink) event.getSource();
				if(source.getBean().isRelative())
					SmartKpis.getVisualizationContainer( ).loadJsonData(source.getBean().getObjectType(), source.getBean().getIniFixDate().replace("-", ""), 
						source.getBean().getEndFixDate().replace("-", ""), source.getBean().getKpis(), source.getBean().getObjects(), 
						source.getBean().getTableType());
				else
					SmartKpis.getVisualizationContainer( ).loadJsonData(source.getBean().getObjectType(), source.getBean().getIniDate().replace("-", ""), 
							source.getBean().getEndDate().replace("-", ""), source.getBean().getKpis(), source.getBean().getObjects(), 
							source.getBean().getTableType());
				SmartKpis.setScreen("viz");
			} else {
				//PopupMessage.showWarning("Unhandled component clicked: " + event.getSource().toString());
			}
		}
	}
}
