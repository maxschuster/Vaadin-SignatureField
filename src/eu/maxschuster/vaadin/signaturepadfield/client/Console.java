package eu.maxschuster.vaadin.signaturepadfield.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Console extends JavaScriptObject {
	
	protected Console() {}
	
	public static final native boolean isSupported() /*-{
		return typeof console === "object";
	}-*/;
	
	public static final native void log(Object...objects) /*-{
		if (typeof console === "object") {
			console.log.apply(console, objects);
		}
	}-*/;
	
}
