package org.colombiamovil.smartkpi.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Image;

public class InfoWidget extends Image implements ClickHandler, MouseOverHandler {

	private String messageType;
	private String message;
	public static final String CLICK_ACTION = "CLICK";
	public static final String MOUSE_ACTION = "MOUSE";

	public InfoWidget(String messageType, String message) {
		this(messageType, CLICK_ACTION, message);
	}

	public InfoWidget(String messageType, String actionType, String message) {
		super("images/question.gif");
		this.messageType = messageType;
		this.message = message;
		addStyleName("nms-InfoWidget");
		if(actionType.equals(CLICK_ACTION)) addClickHandler(this);
		else if(actionType.equals(MOUSE_ACTION)) addMouseOverHandler(this);
	}

	public void onClick(ClickEvent event) {
		if(messageType.equals(PopupMessage.HELP_MESSAGE)) {
			PopupMessage.showHelp(message,this);
		} else if(messageType.equals(PopupMessage.INFO_MESSAGE)) {
			PopupMessage.showInfo(message);
		}
	}

	public void onMouseOver(MouseOverEvent event) {
		PopupMessage.showHelp(message);
	}
}
