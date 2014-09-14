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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import eu.maxschuster.vaadin.signaturefield.SignatureField;
import eu.maxschuster.vaadin.signaturefield.client.SignaturePad.BeginHandler;
import eu.maxschuster.vaadin.signaturefield.client.SignaturePad.EndHandler;

/**
 * Widget of the {@link SignatureField}
 * 
 * @author Max Schuster <dev@maxschuster.eu>
 */
public class SignatureFieldWidget extends FlowPanel {
	
    private static final String CLASSNAME = "signaturefield";
    private static final String CLASSNAME_FOCUS = "focus";
    private static final String CLASSNAME_CLEARBUTTON = CLASSNAME + "-clearbutton";
    
    private final SignaturePad signaturePad;
    private final Canvas canvas;
    private final Anchor clearButton;
    
    private Image resizeTmpImage = new Image();
    
    private UpdateCanvasSizeHandler updateCanvasSizeHandler;
    
    private final RepeatingCommand resizeCommand =
    		new RepeatingCommand() {
		
		@Override
		public boolean execute() {
			updateCanvasSize();
			resizingDebounce = false;
			return false;
		}
	};
    
    private boolean resizingDebounce = false;

	public SignatureFieldWidget() {
		
		setStylePrimaryName(CLASSNAME);
		
		canvas = createCanvas();
		add(canvas);
		
		clearButton = createClearButton();
		add(clearButton);
		
		signaturePad = SignaturePad.create(canvas);
		
		extendSignaturePad(signaturePad);
	}
	
	protected Canvas createCanvas() {
		Canvas canvas = Canvas.createIfSupported();
		canvas.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				addStyleName(CLASSNAME_FOCUS);
				addStyleDependentName(CLASSNAME_FOCUS);
			}
		});
		canvas.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				removeStyleName(CLASSNAME_FOCUS);
				removeStyleDependentName(CLASSNAME_FOCUS);
			}
		});
		return canvas;
	}
	
	protected Anchor createClearButton() {
		Anchor clearButton = new Anchor();
		clearButton.setStylePrimaryName(CLASSNAME_CLEARBUTTON);
		return clearButton;
	}

	public HandlerRegistration addClearButtonClickHandler(ClickHandler handler) {
		return clearButton.addClickHandler(handler);
	}
	
	public boolean isClearButtonVisible() {
		return clearButton.isVisible();
	}
	
	public void setClearButtonVisible(boolean clearButtonVisible) {
		clearButton.setVisible(clearButtonVisible);
	}
	
	public void updateCanvasSize() {
		int oldWidth = canvas.getCoordinateSpaceWidth();
		int newWidth = getElement().getClientWidth();
		int oldHeight = canvas.getCoordinateSpaceHeight();
		int newHeight = getElement().getClientHeight();
		boolean empty = signaturePad.isEmpty();
		if (oldWidth != newWidth || oldHeight != newHeight) {
			if (!empty) {
				resizeTmpImage.setUrl(canvas.toDataUrl("image/png"));
			}
			canvas.setCoordinateSpaceWidth(newWidth);
			canvas.setCoordinateSpaceHeight(newHeight);
			if (!empty) {
				final ImageElement face = ImageElement.as(resizeTmpImage.getElement());
				canvas.getContext2d().drawImage(face, 0, 0);
			}
			if (updateCanvasSizeHandler != null) {
				updateCanvasSizeHandler.onUpdateCanvasSize();
			}
		}
	}
	
	public void updateCanvasSizeDebounced() {
		if (!resizingDebounce) {
			Scheduler.get().scheduleFixedDelay(resizeCommand, 500);
		}
	}

	public boolean isReadOnly() {
		return getSignaturePadReadOnly(signaturePad);
	}

	public void setReadOnly(boolean readOnly) {
		setSignaturePadReadOnly(signaturePad, readOnly);
	}
	
	protected static final native void extendSignaturePad(SignaturePad pad) /*-{
		var super_strokeBegin = pad._strokeBegin,
			super_strokeUpdate = pad._strokeUpdate,
			canvas = pad._canvas;
			
		// proxy stroke methods to allow read only function
		pad._strokeBegin = function(event) {
			if (pad.readOnly !== true) {
				canvas.focus(); // fix focus issue on touch devices
				super_strokeBegin.apply(pad, [event]);
			} else {
				pad._mouseButtonDown = false;
			}
		};
		pad._strokeUpdate = function(event) {
			if (pad.readOnly !== true) {
				super_strokeUpdate.apply(pad, [event]);
			}
		};
	}-*/;
	
	protected static final native boolean getSignaturePadReadOnly(SignaturePad pad) /*-{
		return typeof pad.readOnly === "boolean" ? pad.readOnly : false;
	}-*/;
	
	protected static final native void setSignaturePadReadOnly(SignaturePad pad, boolean readOnly) /*-{
		pad.readOnly = readOnly;
	}-*/;
	
	/* DELEGATES */

	public final String toDataURL() {
		return signaturePad.toDataURL();
	}

	public final String toDataURL(String mimeType) {
		return signaturePad.toDataURL(mimeType);
	}

	public final void fromDataURL(String dataURL) {
		signaturePad.fromDataURL(dataURL);
	}

	public final void clear() {
		signaturePad.clear();
	}
	
	public final boolean isEmpty() {
		return signaturePad.isEmpty();
	}

	public final Float getDotSize() {
		return signaturePad.getDotSize();
	}

	public final void setDotSize(Float dotSize) {
		signaturePad.setDotSize(dotSize);
	}

	public final float getMinWidth() {
		return signaturePad.getMinWidth();
	}

	public final void setMinWidth(float minWidth) {
		signaturePad.setMinWidth(minWidth);
	}

	public final float getMaxWidth() {
		return signaturePad.getMaxWidth();
	}

	public final void setMaxWidth(float maxWidth) {
		signaturePad.setMaxWidth(maxWidth);
	}

	public final String getBackgroundColor() {
		return signaturePad.getBackgroundColor();
	}

	public final void setBackgroundColor(String backgroundColor) {
		signaturePad.setBackgroundColor(backgroundColor);
	}

	public final String getPenColor() {
		return signaturePad.getPenColor();
	}

	public final void setPenColor(String penColor) {
		signaturePad.setPenColor(penColor);
	}

	public final float getVelocityFilterWeight() {
		return signaturePad.getVelocityFilterWeight();
	}

	public final void setVelocityFilterWeight(float velocityFilterWeight) {
		signaturePad.setVelocityFilterWeight(velocityFilterWeight);
	}

	public final BeginHandler getBeginHandler() {
		return signaturePad.getBeginHandler();
	}

	public final void setBeginHandler(BeginHandler beginHandler) {
		signaturePad.setBeginHandler(beginHandler);
	}

	public final EndHandler getEndHandler() {
		return signaturePad.getEndHandler();
	}

	public final void setEndHandler(EndHandler endHandler) {
		signaturePad.setEndHandler(endHandler);
	}

	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return canvas.addBlurHandler(handler);
	}

	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return canvas.addFocusHandler(handler);
	}
	
	public UpdateCanvasSizeHandler getUpdateCanvasSizeHandler() {
		return updateCanvasSizeHandler;
	}

	public void setUpdateCanvasSizeHandler(
			UpdateCanvasSizeHandler updateCanvasSizeHandler) {
		this.updateCanvasSizeHandler = updateCanvasSizeHandler;
	}

	public static interface UpdateCanvasSizeHandler {
		
		public void onUpdateCanvasSize();
		
	}
	
}
