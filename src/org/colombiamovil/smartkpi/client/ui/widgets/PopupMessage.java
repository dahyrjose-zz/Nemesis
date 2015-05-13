package org.colombiamovil.smartkpi.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class that allows the display of popup messages within the application.
 * Supported messages are: help, info, warning and error, and each of them has 
 * different behavior and look 'n feel.
 * 
 * It's important to keep in mind that the text is given as HTML, so that there is 
 * flexibility in the format of the displayed text.
 * */
public class PopupMessage extends PopupPanel implements ClickHandler {

	public static final String HELP_MESSAGE = "HELP";
	public static final String INFO_MESSAGE = "INFO";
	public static final String WARNING_MESSAGE = "WARNING";
	public static final String ERROR_MESSAGE = "ERROR";

	private static PopupMessage defaultHelpBox;
	private static PopupMessage defaultInfoBox;
	private static PopupMessage defaultWarningBox;
	private static PopupMessage defaultErrorBox;

	// Using this widget may represent a javascript security issue that must be addressed
	private HTML text, close;

	/**
	 * Constructor for message boxes
	 * 
	 * @param messageType The type of the message, depending on it the look 'n feel and position of the box varies
	 * @param message the initial text of the message box
	 * */
	private PopupMessage(String messageType, String message) {
		super();
		this.text = new HTML(message);
		this.close = new HTML("<tr width='100%'><td width='100%'><img src='images/delete.gif' align='right'></img></td></tr><br/>");
		text.setStyleName("nms-PopupMessage");
		text.addClickHandler(this);
		close.addClickHandler(this);
		if(messageType.equals(HELP_MESSAGE)) {
			text.addStyleName("nms-HelpLabel");
			setAutoHideEnabled(true);
			//setAnimationEnabled(true);
			setModal(false);
			setWidget(this.text);
		} else if(messageType.equals(INFO_MESSAGE)) {
			text.addStyleName("nms-InfoLabel");
			setAutoHideEnabled(true);
			setWidget(new InnerWidget(this.text, messageType));
		} else if(messageType.equals(WARNING_MESSAGE)) {
			text.addStyleName("nms-WarningLabel");
			setAutoHideEnabled(true);
			setWidget(new InnerWidget(this.text, messageType));
		} else if(messageType.equals(ERROR_MESSAGE)) {
			text.addStyleName("nms-ErrorLabel");
			setAnimationEnabled(false);
			setAutoHideEnabled(false);
			setModal(true);
			setWidget(new InnerWidget(this.text, messageType));
		}
	}

	/**
	 * Singleton method to obtain the common help box.
	 * Help boxes appear at the position where the mouse cursor is when shown.
	 * */
	public static PopupMessage getDefaultHelpBox() {
		if(defaultHelpBox == null) {
			defaultHelpBox = new PopupMessage(HELP_MESSAGE, "");
		}
		return defaultHelpBox;
	}

	/**
	 * Singleton method to obtain the common info box.
	 * Info boxes appear centered in the screen.
	 * */
	public static PopupMessage getDefaultInfoBox() {
		if(defaultInfoBox == null) {
			defaultInfoBox = new PopupMessage(INFO_MESSAGE, "");
		}
		return defaultInfoBox;
	}

	/**
	 * Singleton method to obtain the common warning box.
	 * Warning boxes appear centered in the screen.
	 * */
	public static PopupMessage getDefaultWarningBox() {
		if(defaultWarningBox == null) {
			defaultWarningBox = new PopupMessage(WARNING_MESSAGE, "");
		}
		return defaultWarningBox;
	}

	/**
	 * Singleton method to obtain the common error box.
	 * Error boxes appear centered in the screen.
	 * */
	public static PopupMessage getDefaultErrorBox() {
		if(defaultErrorBox == null) {
			defaultErrorBox = new PopupMessage(ERROR_MESSAGE, "");
		}
		return defaultErrorBox;
	}

	/**
	 * Changes the text of the message box
	 * 
	 * @param text The new message to show at the text box
	 * */
	private void setText(String text) {
		this.text.setHTML(text);
//		this.text.setHTML(text + "<br/><img src='images/delete.gif' aligh='right'></img>");
	}

	/**
	 * Show the default help text box with the given message
	 * 
	 * @param helpMessage The text to show as help in the text box
	 * */
	public static void showHelp(String helpMessage) {
		getDefaultHelpBox().setText(helpMessage);
		getDefaultHelpBox().setPopupPosition(DOM.eventGetClientX(Event.getCurrentEvent()), DOM.eventGetClientY(Event.getCurrentEvent()));
		getDefaultHelpBox().show();
	}

	/**
	 * Show the default help text box with the given message
	 * The position will be relative to the given widget, starting at his lower right corner
	 * 
	 * @param helpMessage The text to show as help in the text box
	 * */
	public static void showHelp(String helpMessage, Widget widget) {
		getDefaultHelpBox().setText(helpMessage);
		getDefaultHelpBox().setPopupPosition(widget.getAbsoluteLeft() + widget.getOffsetWidth(), widget.getAbsoluteTop() + widget.getOffsetHeight());
		getDefaultHelpBox().show();
	}

	public static void hideHelp() {
		getDefaultHelpBox().hide();
	}
	/**
	 * Show the default error text box with the given message
	 * 
	 * @param errorMessage The text to show as error in the text box
	 * */
	public static void showError(String errorMessage) {
		getDefaultErrorBox().setText(errorMessage);
		getDefaultErrorBox().center();
	}

	/**
	 * Show the warning error text box with the given message
	 * 
	 * @param warningMessage The text to show as warning in the text box
	 * */
	public static void showWarning(String warningMessage) {
		getDefaultWarningBox().setText(warningMessage);
		getDefaultWarningBox().center();
	}

	/**
	 * Show the default info text box with the given message
	 * 
	 * @param infoMessage The text to show as info in the text box
	 * */
	public static void showInfo(String infoMessage) {
		getDefaultInfoBox().setText(infoMessage);
		getDefaultInfoBox().center();
	}

	private class InnerWidget extends HorizontalPanel {
		public InnerWidget(HTML label, String messageType) {
			Image img = new Image();
			if(messageType.equals(INFO_MESSAGE)) {
				img.setUrl("images/loading/blank.gif");
				img.setWidth("16px");
				img.setHeight("16px");
				img.addClickHandler(PopupMessage.this);
			} else if(messageType.equals(WARNING_MESSAGE)) {
				img.setUrl("images/icon_warning.jpg");
				img.setWidth("80px");
				img.setHeight("76px");
				img.addClickHandler(PopupMessage.this);
			} else if(messageType.equals(ERROR_MESSAGE)) {
				img.setUrl("images/icon_error.gif");
				img.setWidth("59px");
				img.setHeight("56px");
				img.addClickHandler(PopupMessage.this);
			}
			setSpacing(10);
			if(!messageType.equals(HELP_MESSAGE)) add(img);
			add(label);
			if(!messageType.equals(HELP_MESSAGE)) add(close);
		}
	}

	public void onClick(ClickEvent event) {
		hide(false);
	}
}
