package org.colombiamovil.smartkpi.client.menu;

import org.colombiamovil.smartkpi.client.SmartKpis;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Main Menu Widget.
 * Handles the loading status icon through the setStatus method.
 * 
 * Each menu item should load a different screen, through the method 
 * SmartKpis.setScreen(String screen), where screen is the history 
 * token associated to the desired view. See the body of the indicated 
 * method to find the history token.
 * */
public class MainMenuBar extends Composite {

	// The main menu bar
	private MenuBar menu;
	// All the menu popups
	private MenuBar reps, dlds, exit;
	private MenuItem ctrlNext, ctrlPrev, ctrlFirst, ctrlLast, ctrlPlay, ctrlPause;
	private HorizontalPanel menuPanel = new HorizontalPanel();
	private HorizontalPanel statPanel = new HorizontalPanel();
	private static Image statImg = new Image();
	public static final int NORMAL = 0, LOADING = 1;

	public MainMenuBar() {
		menuPanel.setWidth("100%");
		menuPanel.setSpacing(0);
		menuPanel.addStyleName("menuBar");
		menuPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		//statPanel.setWidth("10%");
		//statPanel.setHeight("15px");
		statPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		menu = new MenuBar();
		menu.setWidth("100%");
		menu.setAutoOpen(false);
		//menu.setAnimationEnabled(false);		
		menu.setStyleName("smart-MenuItem");

		//buildStartSection();
		buildReportsSection();
		buildDownloadsSection();
		buildControllerSection();
		buildExitSection();

		buildStandardMenu();

		statImg.setSize("16px", "16px");
		statImg.setStyleName("menuImage");
		setStatus(NORMAL);
		statPanel.add(statImg);
		menuPanel.add(menu);
		menuPanel.add(statPanel);
		initWidget(menuPanel);
	}

	/**
	 * Standard menu bar with the default menu items
	 * */
	public void buildStandardMenu() {
		menu.clearItems();
		buildReportsSection();
		buildDownloadsSection();
		buildExitSection();
	}

	/**
	 * A media player - like menu bar, usable when ScreenController is 
	 * called and MultipleChartHandler is shown
	 * */
	public void buildControllerMenu() {
		menu.clearItems();
		menu.addItem(ctrlPlay);
		menu.addItem(ctrlPause);
		menu.addItem(ctrlFirst);
		menu.addItem(ctrlPrev);
		menu.addItem(ctrlNext);
		menu.addItem(ctrlLast);
		buildExitSection();
	}

	/**
	 * Main reports options section
	 * */
	public void buildReportsSection() {
		reps = new MenuBar(true);
		reps.addStyleName("smart-MenuItem");
		reps.addItem("Saved Reports", new Command() {
			public void execute() {
				SmartKpis.setScreen("savedreps");
			}
		});
		reps.addItem("Current Report", new Command() {
			public void execute() {
				SmartKpis.setScreen("viz");
			}
		});
		reps.addItem("New Report", new Command() {
			public void execute() {
				SmartKpis.setScreen("newRep");
			}
		});
		reps.addItem("Process Queue", new Command() {
			public void execute() {
				SmartKpis.setScreen("processQueue");
			}
		});
//      reps.addItem("Ip Core Report", new Command() {
//            public void execute() {
//                SmartKpis.setScreen("ipCoreReport");
//            }
//        });
		
/*		reps.addItem("Weekly Reports", new Command() {
			public void execute() {
				SmartKpis.setScreen("weekly");
			}
		});
		reps.addItem("Kpi Screener", new Command() {
			public void execute() {
				SmartKpis.setScreen("kpiscr");
			}
		});
		reps.addItem("Alarms View", new Command() {
			public void execute() {
				SmartKpis.setScreen("alarms");
			}
		});
*/
		menu.addItem("Reports", reps);
	}

	public void buildDownloadsSection() {
		dlds = new MenuBar(true);
		dlds.addStyleName("smart-MenuItem");
		dlds.addItem("Alarms Data", new Command() {
			public void execute() {
				SmartKpis.setScreen("alarms");
			}
		});
		dlds.addItem("Collector Data", new Command() {
			public void execute() {
				SmartKpis.setScreen("colDlds");
			}
		});
		menu.addItem("Export", dlds);
	}

	public void buildControllerSection() {
		ctrlPlay = new MenuItem("Play", new Command() {
			public void execute() {
				SmartKpis.getScreenController().setActive(true);
			}
		});
		ctrlPause = new MenuItem("Pause", new Command() {
			public void execute() {
				SmartKpis.getScreenController().setActive(false);
			}
		});
		ctrlPrev = new MenuItem("< Previous", new Command() {
			public void execute() {
				SmartKpis.getScreenController().showPrevious();
			}
		});
		ctrlNext = new MenuItem("Next >", new Command() {
			public void execute() {
				SmartKpis.getScreenController().showNext();
			}
		});
		ctrlFirst = new MenuItem("<< First", new Command() {
			public void execute() {
				SmartKpis.getScreenController().showFirst();
			}
		});
		ctrlLast = new MenuItem("Last >>", new Command() {
			public void execute() {
				SmartKpis.getScreenController().showLast();
			}
		});
	}

	public void buildExitSection() {
		exit = new MenuBar(true);
		exit.addStyleName("smart-MenuItem");
		exit.addItem("Show login screen", new Command() {
			public void execute() {
				SmartKpis.getLoginForm().setAuthenticated(false);
				SmartKpis.setScreen("loginForm");
			}
		});
		exit.addItem("Go to redtigo main page", new Command() {
			public void execute() {
				SmartKpis.getLoginForm().setAuthenticated(false);
				Window.open("http://redtigo", "_self", "");
			}
		});
		menu.addItem("End session and...", exit);
	}

	/**
	 * Sets the current loading status. Use the static variables to set the status.
	 * 
	 * @param status The current status
	 * */
	public static void setStatus(int status) {
		switch (status) {
			case NORMAL :
				statImg.setUrl("images/loading/blank.gif");
				break;

			case LOADING :
				statImg.setUrl("images/loading/blue_roller.gif");
				break;

			default :
				break;
		}
	}
}
