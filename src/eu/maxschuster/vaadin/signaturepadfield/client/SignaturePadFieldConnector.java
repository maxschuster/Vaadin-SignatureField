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
				getWidget().clear();
			}

			@Override
			public void fromDataURL(MimeType mimeType, String dataURL) {
				VSignaturePadField field = getWidget();
				field.fromDataURL(dataURL);
			}
		});
	}

	@Override
	protected void init() {
		super.init();
		
		VSignaturePadField field = getWidget();
		
		field.setEndHandler(new EndHandler() {
			
			@Override
			public void onEnd(SignaturePad signaturePad) {
				String mimeType = getMimeType().getMimeType();
				String newTextValue = getDataURL(mimeType);
				changeValue(newTextValue, null);
			}
		});
		
		field.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if (changed) {
					updateValueOnServer();
				}
			}
		});
	}
	
	protected String getDataURL(String mimeType) {
		VSignaturePadField field = getWidget();
		return field.isEmpty() ? null : field.toDataURL(mimeType);
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
		SignaturePadFieldState state = getState();
		VSignaturePadField field = getWidget();
		
		field.setDotSize(state.dotSize);
		field.setMaxWidth(state.maxWidth);
		field.setMinWidth(state.minWidth);
		field.setPenColor(state.penColor);
		field.setVelocityFilterWeight(state.velocityFilterWeight);
		
		getWidget().setReadOnly(state.readOnly);
		
		if (event.hasPropertyChanged("mimeType")) {
			String mimeType = getMimeType().getMimeType();
			String newValue = getDataURL(mimeType);
			changeValue(newValue, true);
		}
		
		if (event.hasPropertyChanged("backgroundColor")) {
			field.setBackgroundColor(state.backgroundColor);
			getWidget().clear();
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
