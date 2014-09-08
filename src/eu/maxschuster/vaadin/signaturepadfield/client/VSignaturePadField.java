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

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

import eu.maxschuster.vaadin.signaturepadfield.client.SignaturePad.BeginHandler;
import eu.maxschuster.vaadin.signaturepadfield.client.SignaturePad.EndHandler;

public class VSignaturePadField extends SimplePanel implements RequiresResize {
	
	//import com.vaadin.client.ui.VTextField;
	//import com.vaadin.client.ui.textfield.TextFieldConnector;
	
	/**
     * The input node CSS classname.
     */
    public static final String CLASSNAME = "v-signaturepadfield";
    
    /**
     * This CSS classname is added to the input node on hover.
     */
    public static final String CLASSNAME_FOCUS = "focus";
    
    private final SignaturePad signaturePad;
    
    private final Canvas canvas;

	public VSignaturePadField() {
		this(Canvas.createIfSupported());
	}

	public VSignaturePadField(Canvas canvas) {
		super(canvas);
		this.canvas = canvas;
		signaturePad = SignaturePad.create(canvas);
		extendSignaturePad(signaturePad);
		setStylePrimaryName(CLASSNAME);
		
		canvas.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				addStyleDependentName(CLASSNAME_FOCUS);
			}
		});
		
		canvas.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				removeStyleDependentName(CLASSNAME_FOCUS);
			}
		});
	}

	@Override
	public void setHeight(String height) {
		super.setHeight(height);
		updateCanvasSize();
	}

	@Override
	public void setWidth(String width) {
		super.setWidth(width);
		updateCanvasSize();
	}
	
	public void updateCanvasSize() {
		int oldWidth = canvas.getCoordinateSpaceWidth();
		int newWidth = getElement().getClientWidth();
		int oldHeight = canvas.getCoordinateSpaceHeight();
		int newHeight = getElement().getClientHeight();
		if (oldWidth != newWidth) {
			canvas.setCoordinateSpaceWidth(newWidth);
		}
		if (oldHeight != newHeight) {
			canvas.setCoordinateSpaceHeight(newHeight);
		}
	}

	@Override
	public void onResize() {
		Console.log("OnResize");
	}

	public boolean isReadOnly() {
		return getSignaturePadReadOnly(signaturePad);
	}

	public void setReadOnly(boolean readOnly) {
		setSignaturePadReadOnly(signaturePad, readOnly);
	}
	
	protected static final native void extendSignaturePad(SignaturePad pad) /*-{
		// proxy stroke methods to allow read only function
		var super_strokeBegin = pad._strokeBegin,
			super_strokeUpdate = pad._strokeUpdate;
		pad._strokeBegin = function(event) {
			if (pad.readOnly !== true) {
				super_strokeBegin.apply(pad, [event]);
			} else {
				pad_mouseButtonDown = false;
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
	
	
	
}
