package org.colombiamovil.smartkpi.client.ui;

import java.util.Date;

import org.colombiamovil.smartkpi.client.NemesisServiceAsync;
import org.colombiamovil.smartkpi.client.SmartKpis;
import org.colombiamovil.smartkpi.client.menu.MainMenuBar;
import org.colombiamovil.smartkpi.client.security.UserBean;
import org.colombiamovil.smartkpi.client.ui.widgets.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Form for user authentication. Requires user name and password that is passed to the server
 * for validation and saves the returned hash key for session management.
 * 
 * @author Dahyr.Vergara
 * */
public class LoginForm extends Composite implements ClickHandler {

	private Label msgBox;
	private TextBox userNameField;
	private PasswordTextBox passwordField;
	private Button submit, forgot;
	private CheckBox useDomain = new CheckBox("Use domain account");
	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel northPanel = new HorizontalPanel();
	private VerticalPanel credentialsPanel = new VerticalPanel();
	private VerticalPanel infoNemesisPanel = new VerticalPanel();
	private VerticalPanel southPanel = new VerticalPanel();
	private VerticalPanel hintsPanel = new VerticalPanel();
	private FlexTable credentialsTable;
	private boolean authenticated = false;
	private UserBean user = new UserBean();

	public static String SESSION_ID = "SMARTKPIS_SESSID";
	public static String SESSION_DATA = "SMARTKPIS_SESSDATA";

	private NemesisServiceAsync dataSvc = SmartKpis.getDataSvc();

	public LoginForm() {
		//mainPanel.setWidth("100%");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		northPanel.setSpacing(20);
		southPanel.setSpacing(20);
		credentialsPanel.setWidth("450px");
		credentialsPanel.setSpacing(10);
		credentialsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		infoNemesisPanel.setSpacing(10);
		//rightPanel.setWidth("50%");

		credentialsTable = new FlexTable();
		credentialsTable.setCellSpacing(12);
		credentialsTable.addStyleName("oddRow");
		credentialsTable.addStyleName("silverBottom");

		msgBox = new Label();
		msgBox.addStyleName("negativeChange");
		userNameField = new TextBox();
		userNameField.addStyleName("field_input");
		userNameField.setWidth("150px");
		//userNameField.setText(userName);
		passwordField = new PasswordTextBox();
		passwordField.addStyleName("field_input");
		passwordField.setWidth("150px");
		submit = new Button("Enter");
		submit.setStyleName("button");
		submit.setWidth("100px");
		submit.addClickHandler(this);
		forgot = new Button("Forgot Password");
		forgot.setStyleName("button");
		forgot.setWidth("120px");
		forgot.addClickHandler(this);

		credentialsTable.setWidget(0, 0, new Label("User name"));
		credentialsTable.setWidget(0, 1, new Label("Password"));
		credentialsTable.setWidget(1, 0, userNameField);
		credentialsTable.setWidget(1, 1, passwordField);
		credentialsTable.setWidget(2, 0, submit);
		//credentialsTable.setWidget(2, 1, forgot);
		credentialsTable.setWidget(2, 1, useDomain);

		credentialsPanel.add(credentialsTable);

		infoNemesisPanel.add(new HTML("<strong class>What is Smart Kpis?</strong>"));
		infoNemesisPanel.add(new HTML("<ul>" +
			"<li>Network VP's platform that gathers the most important indicators generated from the cellular network." +
			"<li>Uses advanced visualization methods to show all those indicators." +
			"<li>Handles external data integration: MIDAS, Prepaid Platform, SMSC, etc." +
			"</ul>"));
		infoNemesisPanel.getWidget(0).addStyleName("silverBottom");

		hintsPanel.setSpacing(10);
		hintsPanel.add(new HTML("<strong>Key Hints:</strong>"));
		hintsPanel.add(new HTML("<ul>" +
				"<li>Use your domain user and password to access the application. That is, the same credentials used to enter your PC session and corporative e-mail account. You must have enabled access." +
				"<li>If you don't have acces to the application please send an e-mail to <a href=\"mailto:miguel.fruto@tigo.com.co;Fabian.Aranda@tigo.com.co\">miguel.fruto@tigo.com.co</a> and CC to Fabian.Aranda@tigo.com.co specifying: NAME, AREA, POSITION, ID and Direct Boss." +
				"<li>If you don't have domain access please specify that in the e-mail and a custom password may be assigned." +
				"</ul>"));
		hintsPanel.getWidget(0).addStyleName("silverBottom");

		northPanel.add(credentialsPanel);
		northPanel.add(infoNemesisPanel);

		southPanel.add(hintsPanel);

		mainPanel.add(northPanel);
		mainPanel.add(southPanel);

		initWidget(mainPanel);
		setAuthenticated(false);
	}

	/**
	 * Fetch the session data for the given userName and password
	 * 
	 * @param userName
	 * */
	private void loadSessionData(String userName, String password, boolean withDomain) {
		MainMenuBar.setStatus(MainMenuBar.LOADING);
		if (dataSvc == null) {
			dataSvc = SmartKpis.getDataSvc();
		}
		AsyncCallback<UserBean> callback = new AsyncCallback<UserBean>() {
			public void onFailure(Throwable caught) {
				PopupMessage.showError("ERROR: " + caught.getMessage());
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}
			public void onSuccess(UserBean result) {
				if(result.getSessionId().equals("no-session")) {
					PopupMessage.showError("Session Error: The session could not be initialized");
				} else if(result.getSessionId().equals("no-user")) {
					PopupMessage.showWarning("Session Warning: The user entered does not exist or does not have an active account");
				} else if(result.getSessionId().equals("no-pass")) {
					PopupMessage.showWarning("Session Warning: Wrong password, please try again or request a new one");
				} else {
					user = result;
					// Save the cookie with an expiration date of 24 hours after current date
					Cookies.setCookie(SESSION_ID, result.getSessionId(), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
					Cookies.setCookie(SESSION_DATA, result.toString(), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
					setAuthenticated(true);
					SmartKpis.setScreen("newRep");
				}
				MainMenuBar.setStatus(MainMenuBar.NORMAL);
			}
		};
		dataSvc.initSession(userName, password, withDomain, callback);
	}

	public void onClick(ClickEvent event) {
		if(event.getSource().equals(submit)) {
			if(userNameField.getText().length() > 1 && passwordField.getText().length() > 1) {
				loadSessionData(userNameField.getText(), passwordField.getValue(), useDomain.getValue());
//				SmartKpis.setScreen("newRep");
			} else {
				PopupMessage.showError("Please enter a valid user name and password");
			}
		} else if(event.getSource().equals(forgot)) {
			PopupMessage.showWarning("Unsupported");
		}
	}

	public native String requestUserName() /*-{
		var WshNetwork = new ActiveXObject('WScript.Network');
		//var Domain = WshNetwork.UserDomain;
		//var ComputerName = WshNetwork.ComputerName;
		var UserName = WshNetwork.UserName;
		return UserName;
	}-*/;

	public void checkSessionData() {
		final String sessionId = Cookies.getCookie(SESSION_ID);
		final String sessionData = Cookies.getCookie(SESSION_DATA);
//		System.out.println("Session ID: " + sessionId);
//		System.out.println("Session Data: " + sessionData);
		if(sessionId == null) {
			setAuthenticated(false);
//			SmartKpis.setScreen("loginForm");
		} else {
			MainMenuBar.setStatus(MainMenuBar.LOADING);
			if (dataSvc == null) {
				dataSvc = SmartKpis.getDataSvc();
			}
			AsyncCallback<String> callback = new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					PopupMessage.showError("ERROR: " + caught.getMessage());
					MainMenuBar.setStatus(MainMenuBar.NORMAL);
				}
				public void onSuccess(String result) {
					setAuthenticated(false);
					user.setSessionId(sessionId);
					user.parseUserBean(sessionData);
					userNameField.setText(user.getUserLogin());
					if(result.equals("no-session")) {
						//PopupMessage.showError("Session Error: The session could not be initialized");
					} else if(result.equals("session-expired")) {
						PopupMessage.showWarning("Warning: Session Expired");
					} else if(result.equals("session-ok"))  {
						// Save the cookie with an expiration date of 24 hours after current date
						Cookies.setCookie(SESSION_ID, sessionId, new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
						Cookies.setCookie(SESSION_DATA, sessionData, new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
						setAuthenticated(true);
						SmartKpis.setScreen("newRep");
					}
					MainMenuBar.setStatus(MainMenuBar.NORMAL);
				}
			};
			dataSvc.checkSession(sessionId, callback);
		}
		MainMenuBar.setStatus(MainMenuBar.NORMAL);
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public UserBean getUserBean() {
		return user;
	}
}
