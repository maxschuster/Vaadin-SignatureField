/*
 * Copyright 2014 Max Schuster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.maxschuster.vaadin.signaturefield;

import com.vaadin.annotations.JavaScript;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.AbstractField;

import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldClientRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldServerRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldState;

/**
 * https://github.com/szimek/signature_pad
 * @author Max
 *
 */
@JavaScript({
	"js/signature_pad/signature_pad.js"
})
public class SignatureField extends AbstractField<Signature> {
	
	public static final String COLOR_WHITE = "white";
	public static final String COLOR_BLACK = "black";
	public static final String COLOR_BLACK_TRANSPARENT = "rgba(0,0,0,0)";
	public static final String COLOR_ULTRAMARIN = "#120a8f";
	
	private SignatureFieldClientRpc clientRpc =
			getRpcProxy(SignatureFieldClientRpc.class);

	public SignatureField() {
		super();
		registerRpc(new SignatureFieldServerRpc() {
			
			@Override
			public void setTextValue(String textValue) {
				
				Signature signature = null;
				if (textValue != null) {
					signature = new Signature(textValue);
				}
				
				SignatureField.this.setValue(signature, true);
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
	protected SignatureFieldState getState() {
		return (SignatureFieldState) super.getState();
	}
	
	@Override
	public boolean isEmpty() {
		// Make is empty public
		return super.isEmpty();
	}

	@Override
	protected void setValue(Signature newFieldValue, boolean repaintIsNotNeeded)
			throws ReadOnlyException, ConversionException,
			InvalidValueException {
		Signature oldInternalValue = getInternalValue();
		super.setValue(newFieldValue, repaintIsNotNeeded);
		Signature newInternalValue = getInternalValue();
		if (!repaintIsNotNeeded) {
			if (newInternalValue != null && !newInternalValue.equals(oldInternalValue)) {
				clientRpc.fromDataURL(
						newInternalValue.getMimeType(), newInternalValue.toDataURL());
			} else if (newInternalValue == null) {
				clientRpc.clear();
			}
		}
	}

	public void clear() {
		setValue(null);
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

	public boolean isClearButtonEnabled() {
		return getState().clearButtonEnabled;
	}

	public void setClearButtonEnabled(boolean clearButtonEnabled) {
		getState().clearButtonEnabled = clearButtonEnabled;
	}
	
}
