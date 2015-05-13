package org.colombiamovil.smartkpi.client.ui.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class MultipleStateBoxClickHandler implements ClickHandler {

	public void onClick(ClickEvent event) {
		if(event.getSource() instanceof MultipleStateBox) {
			((MultipleStateBox)event.getSource()).changeValue();
		}
	}
}
