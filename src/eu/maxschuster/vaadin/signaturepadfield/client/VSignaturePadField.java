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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;

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
    
    public final SignaturePad signaturePad;
    
    public final Canvas canvas;
    
    private boolean readOnly;

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

	public SignaturePad getSignaturePad() {
		return signaturePad;
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
	
	
	
}
