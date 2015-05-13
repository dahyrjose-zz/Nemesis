package org.colombiamovil.smartkpi.client.ui.widgets;

import com.google.gwt.user.client.ui.HTML;

public class OptimalListBox extends HTML {

	public OptimalListBox(String html) {
		super(html);
	}

	public native String getSelectedIds() /*-{
		var box = this.@com.google.gwt.user.client.ui.UIObject::getElement()().getElementsByTagName('*')[0];
		var result = "";
		for(i=0; i<box.length; i=i+1) {
			if(box.options[i].selected) result = result + "," + box.options[i].value;
		}
		return result;
	}-*/;

	public native String getSelectedItems() /*-{
		var box = this.@com.google.gwt.user.client.ui.UIObject::getElement()().getElementsByTagName('*')[0];
		var result = "";
		for(i=0; i<box.length; i=i+1) {
			//alert(sel.options[i].value + " -- " + sel.options[i].text);
			if(box.options[i].selected) result = result + "!" + box.options[i].text;
		}
		return result;
		//this.append(elementsHtml);
		//alert(box.@com.google.gwt.user.client.ui.UIObject::getElement()().innerHTML);
		//box.@com.google.gwt.user.client.ui.UIObject::getElement()().innerHTML = elementsHtml;
//		var sel = this.@com.google.gwt.user.client.ui.UIObject::getElement()();
//		if(document.all) {
//			var orig = sel.outerHTML;
//			alert(orig);
//			var justselect = orig.substring(0, orig.indexOf('>') + 1) + elementsHtml + orig.substring(orig.indexOf('</SELECT>'), orig.length);
//			sel.outerHTML = justselect;
//			alert(sel.innerHTML);
//		} else  {
//			sel.innerHTML = '<OPTION value"OPT">OPT</OPTION>';
//		}
		//box.@com.google.gwt.user.client.ui.UIObject::getElement()().add("document.createElement(\"<OPTION VALUE='OPT'>OPT</OPTION>\")");
		//alert(box.@com.google.gwt.user.client.ui.UIObject::getElement()().innerHTML);
	}-*/;


	public native String getSelectedItemsForXml() /*-{
		var box = this.@com.google.gwt.user.client.ui.UIObject::getElement()().getElementsByTagName('*')[0];
		var result = "";
		for(i=0; i<box.length; i=i+1) {
			//alert(sel.options[i].value + " -- " + sel.options[i].text);
			if(box.options[i].selected) result = result + ",'" + box.options[i].text + "'";
		}
		return result;
	}-*/;

	public native void clearSelection() /*-{
		var box = this.@com.google.gwt.user.client.ui.UIObject::getElement()().getElementsByTagName('*')[0];
		box.selectedIndex = -1;
	}-*/;
}
