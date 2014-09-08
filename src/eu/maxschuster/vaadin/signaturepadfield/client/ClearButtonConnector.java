package eu.maxschuster.vaadin.signaturepadfield.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

import eu.maxschuster.vaadin.signaturepadfield.ClearButton;
import eu.maxschuster.vaadin.signaturepadfield.shared.ClearButtonServerRpc;

@Connect(ClearButton.class)
public class ClearButtonConnector extends AbstractExtensionConnector {
	
    public static final String CLASSNAME = "v-clearbutton";
    
    public static final String CLASSNAME_BUTTON = CLASSNAME + "-button";
	
	private ClearButtonServerRpc serverRpc =
			getRpcProxy(ClearButtonServerRpc.class);

	@Override
	protected void extend(ServerConnector target) {
		final VSignaturePadField field =
                ((SignaturePadFieldConnector) target).getWidget();
		
		field.addStyleName(CLASSNAME);
		field.addStyleDependentName(CLASSNAME);
		
		Anchor closeButton = new Anchor();
		field.getElement().appendChild(closeButton.getElement());
		closeButton.setStylePrimaryName(CLASSNAME_BUTTON);
		attachClickListener(closeButton.getElement());
		
	}
	
	public void click() {
		serverRpc.clear();
	}
	
	// FIXME dont work against gwt events...
	protected final native void attachClickListener(Element target) /*-{
		var self = this;
		target.onclick = function() {
			self.@eu.maxschuster.vaadin.signaturepadfield.client.ClearButtonConnector::click()();
		}
	}-*/;

}
