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

import eu.maxschuster.vaadin.signaturefield.client.signaturepad.SignaturePad;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

import eu.maxschuster.vaadin.signaturefield.SignatureField;
import java.util.logging.Logger;

/**
 * Widget of the {@link SignatureField}
 *
 * @author Max Schuster
 */
public class VSignatureField extends FlowPanel {
    
    private static final String CLASSNAME = "signaturefield";
    private static final String CLASSNAME_FOCUS = "focus";
    private static final String CLASSNAME_CLEARBUTTON =
            CLASSNAME + "-clearbutton";

    private final SignaturePad signaturePad;
    private final Canvas canvas;
    private final Anchor clearButton;

    public VSignatureField() {

        setStylePrimaryName(CLASSNAME);

        canvas = createCanvas();
        add(canvas);

        clearButton = createClearButton();
        add(clearButton);

        signaturePad = new SignaturePad(canvas);
    }

    private Canvas createCanvas() {
        Canvas newCanvas = Canvas.createIfSupported();
        newCanvas.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                addStyleName(CLASSNAME_FOCUS);
                addStyleDependentName(CLASSNAME_FOCUS);
            }
        });
        newCanvas.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                removeStyleName(CLASSNAME_FOCUS);
                removeStyleDependentName(CLASSNAME_FOCUS);
            }
        });
        return newCanvas;
    }

    private Anchor createClearButton() {
        Anchor _clearButton = new Anchor();
        _clearButton.setStylePrimaryName(CLASSNAME_CLEARBUTTON);
        return _clearButton;
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

    public boolean isReadOnly() {
        return signaturePad.isReadOnly();
    }

    public void setReadOnly(boolean readOnly) {
        signaturePad.setReadOnly(readOnly);
    }

    /* DELEGATES */
    public final String toDataURL() {
        return toDataURL("image/png");
    }

    public final String toDataURL(String mimeType) {
        return signaturePad.toDataURL(mimeType, 1.0f);
    }

    public final void fromDataURL(String dataURL) {
        signaturePad.clear();
        signaturePad.fromDataURL(dataURL);
    }

    public SignaturePad getSignaturePad() {
        return signaturePad;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public final boolean isEmpty() {
        return signaturePad.isEmpty();
    }

    public final Double getDotSize() {
        return signaturePad.getDotSize();
    }

    public final void setDotSize(Double dotSize) {
        signaturePad.setDotSize(dotSize);
    }

    public final double getMinWidth() {
        return signaturePad.getMinWidth();
    }

    public final void setMinWidth(double minWidth) {
        signaturePad.setMinWidth(minWidth);
    }

    public final double getMaxWidth() {
        return signaturePad.getMaxWidth();
    }

    public final void setMaxWidth(double maxWidth) {
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

    public final double getVelocityFilterWeight() {
        return signaturePad.getVelocityFilterWeight();
    }

    public final void setVelocityFilterWeight(double velocityFilterWeight) {
        signaturePad.setVelocityFilterWeight(velocityFilterWeight);
    }

    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return canvas.addBlurHandler(handler);
    }

    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return canvas.addFocusHandler(handler);
    }
    
}
