package eu.maxschuster.vaadin.signaturepadfield;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractField;

import eu.maxschuster.vaadin.signaturepadfield.shared.MimeType;
import eu.maxschuster.vaadin.signaturepadfield.shared.SignaturePadFieldClientRpc;
import eu.maxschuster.vaadin.signaturepadfield.shared.SignaturePadFieldServerRpc;
import eu.maxschuster.vaadin.signaturepadfield.shared.SignaturePadFieldState;

/**
 * https://github.com/szimek/signature_pad
 * @author Max
 *
 */
@JavaScript({
	"js/signature_pad/signature_pad.js"
})
@StyleSheet({
	"css/SignaturePadField.css"
})
public class SignaturePadField extends AbstractField<Signature> {
	
	public static final String COLOR_WHITE = "white";
	public static final String COLOR_BLACK = "black";
	public static final String COLOR_BLACK_TRANSPARENT = "rgba(0,0,0,0)";
	public static final String COLOR_ULTRAMARIN = "#120a8f";
	
	private SignaturePadFieldClientRpc clientRpc =
			getRpcProxy(SignaturePadFieldClientRpc.class);

	public SignaturePadField() {
		super();
		registerRpc(new SignaturePadFieldServerRpc() {
			
			@Override
			public void setTextValue(String textValue) {
				Signature signature = null;
				if (textValue != null) {
					signature = new Signature(textValue);
				}
				SignaturePadField.this.setValue(signature);
			}
		});
		
		setImmediate(false);
		setHeight(100, Unit.PIXELS);
		setWidth(300, Unit.PIXELS);
	}

	@Override
	public Class<? extends Signature> getType() {
		return Signature.class;
	}

	@Override
	protected SignaturePadFieldState getState() {
		return (SignaturePadFieldState) super.getState();
	}
	
	@Override
	public boolean isEmpty() {
		// Make is empty public
		return super.isEmpty();
	}

	public void clear() {
		clientRpc.clear();
	}
	
	public Float getDotSize() {
		return getState().dotSize;
	}

	public void setDotSize(Float dotSize) {
		getState().dotSize = dotSize;
	}

	public float getMinWidth() {
		return getState().minWidth;
	}

	public void setMinWidth(float minWidth) {
		getState().minWidth = minWidth;
	}

	public float getMaxWidth() {
		return getState().maxWidth;
	}

	public void setMaxWidth(float maxWidth) {
		getState().maxWidth = maxWidth;
	}

	public String getBackgroundColor() {
		return getState().backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		getState().backgroundColor = backgroundColor;
	}

	public String getPenColor() {
		return getState().penColor;
	}

	public void setPenColor(String penColor) {
		getState().penColor = penColor;
	}

	public float getVelocityFilterWeight() {
		return getState().velocityFilterWeight;
	}

	public void setVelocityFilterWeight(float velocityFilterWeight) {
		getState().velocityFilterWeight = velocityFilterWeight;
	}
	
	public MimeType getMimeType() {
		return getState().mimeType;
	}
	
	public void setMimeType(MimeType mimeType) {
		getState().mimeType = mimeType;
	}

}
