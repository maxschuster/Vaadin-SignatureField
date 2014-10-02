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

package eu.maxschuster.vaadin.signaturefield.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.Connect;

import eu.maxschuster.vaadin.signaturefield.SignatureField;
import eu.maxschuster.vaadin.signaturefield.client.SignaturePad.EndHandler;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldClientRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldServerRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldState;

/**
 * Connector of the {@link SignatureField}
 * 
 * @author Max Schuster <dev@maxschuster.eu>
 */
@Connect(SignatureField.class)
public class SignatureFieldConnector extends AbstractFieldConnector {
	
	private final ElementResizeListener elementResizeListener =
			new ElementResizeListener() {
				
				@Override
				public void onElementResize(ElementResizeEvent e) {
					updateCanvasSizeDebounced();
				}
			};
    
    private final RepeatingCommand resizeCommand =
    		new RepeatingCommand() {
		
		@Override
		public boolean execute() {
			getWidget().updateCanvasSize();
			String mimeType = getMimeType().getMimeType();
			String newTextValue = getDataURL(mimeType);
			changeValue(newTextValue, true);
			resizingDebounce = false;
			return false;
		}
	};
	
	private String value;
	
	private boolean changed;
    
    private boolean resizingDebounce = false;
	
	private SignatureFieldServerRpc serverRpc = 
			getRpcProxy(SignatureFieldServerRpc.class);
	
	public SignatureFieldConnector() {
		
		registerRpc(SignatureFieldClientRpc.class,
				new SignatureFieldClientRpc() {

			@Override
			public void clear() {
				getWidget().clear();
			}

			@Override
			public void fromDataURL(String dataURL) {
				SignatureFieldWidget field = getWidget();
				field.fromDataURL(dataURL);
			}
		});
	}

	@Override
	protected void init() {
		super.init();
		SignatureFieldWidget field = getWidget();
		field.setClearButtonVisible(getState().clearButtonEnabled);
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
		field.addClearButtonClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				changeValue(null, true);
				getWidget().clear();
			}
		});
		
		getLayoutManager().addElementResizeListener(
				getWidget().getElement(), elementResizeListener);
	}
	
	protected String getDataURL(String mimeType) {
		SignatureFieldWidget field = getWidget();
		return field.isEmpty() ? null : field.toDataURL(mimeType);
	}
	
	protected MimeType getMimeType() {
		return getState().mimeType != null ? getState().mimeType : MimeType.PNG;
	}
	
	public void updateCanvasSizeDebounced() {
		if (!resizingDebounce) {
			Scheduler.get().scheduleFixedDelay(resizeCommand, 500);
		}
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
		SignatureFieldState state = getState();
		SignatureFieldWidget field = getWidget();
		
		field.setDotSize(state.dotSize);
		field.setMaxWidth(state.maxWidth);
		field.setMinWidth(state.minWidth);
		field.setPenColor(state.penColor);
		field.setVelocityFilterWeight(state.velocityFilterWeight);
		
		field.setClearButtonVisible(state.clearButtonEnabled);
		
		field.setReadOnly(state.readOnly);
		if (state.readOnly) {
			field.setClearButtonVisible(false);
		}
		
		if (event.hasPropertyChanged("mimeType") && !getWidget().isEmpty()) {
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
	public SignatureFieldState getState() {
		return (SignatureFieldState) super.getState();
	}

	@Override
	public SignatureFieldWidget getWidget() {
		return (SignatureFieldWidget) super.getWidget();
	}

	@Override
	protected void updateComponentSize(String newWidth, String newHeight) {
		super.updateComponentSize(newWidth, newHeight);
		getWidget().updateCanvasSize();
	}
	
}
