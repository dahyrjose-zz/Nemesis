package org.colombiamovil.smartkpi.client.ui.widgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Class to handle special events over the standard GWT Tree Widget
 * */
public class FixedTree extends Tree {

//	private Vector<MultipleStateBox> items = new Vector<MultipleStateBox>();
	
	
	
	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONCLICK) return;
		super.onBrowserEvent(event);
	}

	/**
	 * Set all items in the tree to the unchecked state
	 * */
	public void clearSelection() {
		TreeItem item;
		for(int i=0; i<getItemCount(); i++) {
			item = getItem(i);
			for(int j=0; j<item.getChildCount(); j++) {
				//((MultipleStateBox)item.getChild(j).getWidget()).setValue(MultipleStateBox.UNCHECKED);
				((MultipleStateBox)(((HorizontalPanel)item.getChild(j).getWidget()).getWidget(1))).setValue(MultipleStateBox.UNCHECKED);			
			}
		}
	}

}
