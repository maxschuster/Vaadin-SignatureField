package eu.maxschuster.vaadin.signaturepadfield.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.shared.ui.Connect;

import eu.maxschuster.vaadin.signaturepadfield.SignaturePadField;
import eu.maxschuster.vaadin.signaturepadfield.client.SignaturePad.EndHandler;
import eu.maxschuster.vaadin.signaturepadfield.shared.MimeType;
import eu.maxschuster.vaadin.signaturepadfield.shared.SignaturePadFieldClientRpc;
import eu.maxschuster.vaadin.signaturepadfield.shared.SignaturePadFieldServerRpc;
import eu.maxschuster.vaadin.signaturepadfield.shared.SignaturePadFieldState;

@Connect(SignaturePadField.class)
public class SignaturePadFieldConnector extends AbstractFieldConnector {
	
	private String value;
	
	private boolean changed;
	
	private SignaturePadFieldServerRpc serverRpc = 
			getRpcProxy(SignaturePadFieldServerRpc.class);
	
	public SignaturePadFieldConnector() {
		registerRpc(SignaturePadFieldClientRpc.class,
				new SignaturePadFieldClientRpc() {

			@Override
			public void clear() {
				clearSignaturePad();
			}
		});
	}

	@Override
	protected void init() {
		super.init();
		
		SignaturePad pad = getWidget().signaturePad;
		pad.setEndHandler(new EndHandler() {
			
			@Override
			public void onEnd(SignaturePad signaturePad) {
				String mimeType = getMimeType().getMimeType();
				String newTextValue = getDataURL(mimeType);
				changeValue(newTextValue, null);
			}
		});
		
		getWidget().canvas.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if (changed) {
					updateValueOnServer();
				}
			}
		});
	}
	
	protected void clearSignaturePad() {
		getWidget().signaturePad.clear();
		serverRpc.setTextValue(null);
	}
	
	protected String getDataURL(String mimeType) {
		SignaturePad pad = getWidget().signaturePad;
		return pad.isEmpty() ?
				null : pad.toDataURL(mimeType);
	}
	
	protected MimeType getMimeType() {
		return getState().mimeType != null ? getState().mimeType : MimeType.PNG;
	}
	
	/**
	 * 
	 * @param newValue
	 * @param immediate
	 * If <code>true</code> update immediately.
	 * If <code>false</code> don't update.
	 * If <code>null</code> take immediate value from state.
	 */
	protected void changeValue(String newValue, Boolean immediate) {
		if (value == null && newValue != null ||
				value != null && newValue == null ||
				!value.equals(newValue)) {
			value = newValue;
			if (immediate != null && immediate ||
				immediate == null && getState().immediate) {
				updateValueOnServer();
			} else {
				changed = true;
			} 
		}
	}
	
	protected void updateValueOnServer() {
		if (!getState().readOnly) {
			serverRpc.setTextValue(value);
			changed = false;
		}
	}

	@Override
	public void onStateChanged(StateChangeEvent event) {
		super.onStateChanged(event);
		SignaturePad pad = getWidget().signaturePad;
		SignaturePadFieldState state = getState();
		pad.setDotSize(state.dotSize);
		pad.setMaxWidth(state.maxWidth);
		pad.setMinWidth(state.minWidth);
		pad.setPenColor(state.penColor);
		pad.setVelocityFilterWeight(state.velocityFilterWeight);
		
		getWidget().setReadOnly(state.readOnly);
		
		if (event.hasPropertyChanged("mimeType")) {
			String mimeType = getMimeType().getMimeType();
			String newValue = getDataURL(mimeType);
			changeValue(newValue, true);
		}
		
		if (event.hasPropertyChanged("backgroundColor")) {
			pad.setBackgroundColor(state.backgroundColor);
			clearSignaturePad();
		}
		
		if (event.hasPropertyChanged("height") ||
				event.hasPropertyChanged("width")) {
			getWidget().updateCanvasSize();
		}
	}

	@Override
	public SignaturePadFieldState getState() {
		return (SignaturePadFieldState) super.getState();
	}

	@Override
	public VSignaturePadField getWidget() {
		return (VSignaturePadField) super.getWidget();
	}
	
}
