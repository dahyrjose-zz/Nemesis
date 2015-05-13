package org.colombiamovil.smartkpi.client.screener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.colombiamovil.smartkpi.client.NemesisService;
import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.ui.KpiScreener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KpiChooser extends Composite implements ClickHandler {

	private HorizontalPanel container, containerHead;
	private VerticalPanel mainContainer, famPanel, kpiPanel;
	private Label addKpisLink, buildGraphLink;
	private KpiScreener parent;
	private int selectedFam = 0;
	private LinkedHashMap<String, ArrayList<String[]>> families;
	private String selectedFamily;
	private NemesisServiceAsync objectsSvc = GWT.create(NemesisService.class);

	public KpiChooser(KpiScreener parent) {
		this.parent = parent;
		mainContainer = new VerticalPanel();
		container = new HorizontalPanel();
		containerHead = new HorizontalPanel();
		famPanel = new VerticalPanel();
		kpiPanel = new VerticalPanel();
		addKpisLink = new Label("Add Kpis");
		addKpisLink.addStyleName("activelink");
		buildGraphLink = new Label("Build Graph");
		buildGraphLink.addStyleName("activelink");

		//famPanel.setWidth("200px");
		//kpiPanel.setWidth("300px");

		//fillExampleFamilies();
		//fillExampleKpis();

		container.add(famPanel);
		container.add(kpiPanel);
		container.setVisible(false);
		addKpisLink.addClickHandler(this);
		buildGraphLink.addClickHandler(this);

		containerHead.add(addKpisLink);
		containerHead.add(new Label("			|			"));
		containerHead.add(buildGraphLink);

		mainContainer.setStyleName("containerCollapsed");
		mainContainer.setSpacing(10);
		//containerHead.setSpacing(10);

		mainContainer.add(containerHead);
		mainContainer.add(container);
		initWidget(mainContainer);
	}
/*
	public void fillExampleFamilies() {
		Label f = new Label("Fam 1");
		f.setStyleName("familySelected");
		f.addClickHandler(this);
		famPanel.add(f);
		f = new Label("Fam 2");
		f.setStyleName("familyNormal");
		f.addClickHandler(this);
		famPanel.add(f);
		f = new Label("Fam 3");
		f.setStyleName("familyNormal");
		f.addClickHandler(this);
		famPanel.add(f);
		f = new Label("Fam 4");
		f.setStyleName("familyNormal");
		f.addClickHandler(this);
		famPanel.add(f);
		f = new Label("Fam 5");
		f.setStyleName("familyNormal");
		f.addClickHandler(this);
		famPanel.add(f);
		f = new Label("Fam 6");
		f.setStyleName("familyNormal");
		f.addClickHandler(this);
		famPanel.add(f);
	}

	public void fillExampleKpis() {
		Label k = new Label("connected_lines");
		k.setStyleName("kpiFirstSelected");
		k.addClickHandler(this);
		kpiPanel.add(k);
		k = new Label("carried_calls_with_answer");
		k.setStyleName("kpiMedium");
		k.addClickHandler(this);
		kpiPanel.add(k);
		k = new Label("complete_call_rate_asr");
		k.setStyleName("kpiMedium");
		k.addClickHandler(this);
		kpiPanel.add(k);
		k = new Label("gos_blocking");
		k.setStyleName("kpiMedium");
		k.addClickHandler(this);
		kpiPanel.add(k);
		k = new Label("offered_traffic");
		k.setStyleName("kpiMedium");
		k.addClickHandler(this);
		kpiPanel.add(k);
		k = new Label("ic_carried_calls");
		k.setStyleName("kpiMedium");
		k.addClickHandler(this);
		kpiPanel.add(k);
		k = new Label("ic_traffic_volume_with_answer");
		k.setStyleName("kpiMedium");
		k.addClickHandler(this);
		kpiPanel.add(k);
		k = new Label("avg_call_time_mht");
		k.setStyleName("kpiLast");
		k.addClickHandler(this);
		kpiPanel.add(k);
	}
*/
	public void fillFamilies() {
		if(families == null) return;
		if(families.size() == 0) return;
		famPanel.clear();
		Label label;
		int i = 0;
		for (String fam : families.keySet()) {
			label = new Label(fam);
			if(fam.length() > 20) {
				label.setText(fam.substring(0, 20) + " ...");
				label.setTitle(fam);
			}
			label.addClickHandler(this);
			if(i == 0) {
				label.setStyleName("familySelected");
				selectedFamily = fam;
			} else {
				label.setStyleName("familyNormal");
			}
			famPanel.add(label);
			i++;
		}
		fillKpis();
	}

	public void fillKpis() {
		if(families == null) return;
		if(families.size() == 0) return;
		kpiPanel.clear();
		Label label;
		int i = 0;
		for (String[] kpi : families.get(selectedFamily)) {
			label = new Label(kpi[0]);
			label.setTitle(kpi[1]);
			label.addClickHandler(this);
			if(kpi[0].length() > 25) {
				label.setText(kpi[0].substring(0, 25) + " ...");
				label.setTitle(kpi[0] + ": " + kpi[1]);
			}
			if(i == 0) {
				label.setStyleName("kpiFirstSelected");
			} else if(i == families.get(selectedFamily).size() - 1) {
				label.setStyleName("kpiLast");
			} else {
				label.setStyleName("kpiMedium");
			}
			kpiPanel.add(label);
			i++;
		}
	}

	public void onClick(ClickEvent event) {
		if(event.getSource().equals(addKpisLink)) {
			container.setVisible(!container.isVisible());
			mainContainer.setStyleName(container.isVisible() ? "containerExpanded" : "containerCollapsed");
		} else if(event.getSource().equals(buildGraphLink)) {
			parent.onClick(null);
		} else {
			Label source = (Label)event.getSource();
			if(source.getParent().equals(famPanel)) {
				selectedFamily = source.getTitle().length() > 0 ? source.getTitle() : source.getText();
				fillKpis();
				famPanel.getWidget(selectedFam).setStyleName("familyNormal");
				if(selectedFam == 0) {
					kpiPanel.getWidget(selectedFam).setStyleName("kpiFirst");
				} else if(selectedFam == kpiPanel.getWidgetCount() - 1) {
					kpiPanel.getWidget(selectedFam).setStyleName("kpiLast");
				} else {
					kpiPanel.getWidget(selectedFam).setStyleName("kpiMedium");
				}
				selectedFam = famPanel.getWidgetIndex(source);
				source.setStyleName("familySelected");
				if(selectedFam == 0) {
					kpiPanel.getWidget(selectedFam).setStyleName("kpiFirstSelected");
				} else if(selectedFam == kpiPanel.getWidgetCount() - 1) {
					kpiPanel.getWidget(selectedFam).setStyleName("kpiLastSelected");
				} else {
					kpiPanel.getWidget(selectedFam).setStyleName("kpiMediumSelected");
				}
			} else {
				parent.addKpi(source.getText());
				parent.refreshWatchList();
			}
		}
	}

	public void loadKpis(String objectType) {
		if (objectsSvc == null) {
			objectsSvc = GWT.create(NemesisService.class);
		}

		// Set up the callback object.
		AsyncCallback<LinkedHashMap<String, ArrayList<String[]>>> callback = new AsyncCallback<LinkedHashMap<String, ArrayList<String[]>>>() {
			public void onFailure(Throwable caught) {
				// TODO: Do something with errors.
			}

			public void onSuccess(LinkedHashMap<String, ArrayList<String[]>> result) {
				families = result;
				fillFamilies();
			}
		};

		// Make the call to the kpi screener service.
		objectsSvc.getKpis(objectType, callback);
	}
}
