package eu.maxschuster.vaadin.signaturepadfield;

import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;

import eu.maxschuster.vaadin.signaturepadfield.shared.ClearButtonServerRpc;

public class ClearButton extends AbstractExtension {
	
	protected ClearButton(final SignaturePadField field) {
		registerRpc(new ClearButtonServerRpc() {
			
			@Override
			public void clear() {
				field.clear();
			}
		});
		
		field.addReadOnlyStatusChangeListener(new ReadOnlyStatusChangeListener() {
			
			@Override
			public void readOnlyStatusChange(ReadOnlyStatusChangeEvent event) {
				getState().enabled = !event.getProperty().isReadOnly();
			}
		});
		
		extend((AbstractClientConnector)field);
	}

	public static ClearButton extend(SignaturePadField field) {
		return new ClearButton(field);
	}
	
}
