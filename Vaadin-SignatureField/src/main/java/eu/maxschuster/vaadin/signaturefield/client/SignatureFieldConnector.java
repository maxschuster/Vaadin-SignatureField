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
import eu.maxschuster.vaadin.signaturefield.client.signaturepad.Debouncer;
import eu.maxschuster.vaadin.signaturefield.client.signaturepad.StrokeEndEvent;
import eu.maxschuster.vaadin.signaturefield.client.signaturepad.StrokeEndEventHandler;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldServerRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldState;

/**
 * Connector of the {@link SignatureField}
 *
 * @author Max Schuster
 */
@Connect(SignatureField.class)
public class SignatureFieldConnector extends AbstractFieldConnector {

    private final ElementResizeListener elementResizeListener = 
            new ElementResizeListener() {

                @Override
                public void onElementResize(ElementResizeEvent e) {
                    resizeDebouncer.execute();
                }
            };
    
    private final Debouncer resizeDebouncer = 
            new Debouncer(new RepeatingCommand() {

                @Override
                public boolean execute() {
                    /*
                    boolean sizeChanged = updateCanvasSize();
                    if (sizeChanged) {
                        String mimeType = getMimeType().getMimeType();
                        String newTextValue = getDataURL(mimeType);
                        Console.log("resize", newTextValue);
                        changeValue(newTextValue, true);
                    }
                    */
                    updateCanvasSize();
                    return false;
                }
            }, 500);

    private String value;
    
    private String lastSendValue;

    private boolean changed;

    private final SignatureFieldServerRpc serverRpc =
            getRpcProxy(SignatureFieldServerRpc.class);

    public SignatureFieldConnector() {
        
    }

    @Override
    protected void init() {
        super.init();
        VSignatureField field = getWidget();
        
        field.setReadOnly(true);

        field.getSignaturePad().addEndEventHandler(new StrokeEndEventHandler() {

            @Override
            public void onStrokeEnd(StrokeEndEvent event) {
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
                getWidget().getSignaturePad().clear();
            }
        });

        getLayoutManager().addElementResizeListener(
                getWidget().getElement(), elementResizeListener);
    }

    protected String getDataURL(String mimeType) {
        VSignatureField field = getWidget();
        return field.isEmpty() ? null : field.toDataURL(mimeType);
    }

    protected MimeType getMimeType() {
        return getState().mimeType != null ? 
                getState().mimeType : MimeType.PNG;
    }

    /**
     * Changes the value
     *
     * @param newValue New value
     * @param immediate If <code>true</code> update immediately. If
     * <code>false</code> don't update. If <code>null</code> take immediate
     * value from state.
     */
    protected void changeValue(String newValue, Boolean immediate) {
        if (value == null && newValue != null
                || value != null && newValue == null
                || !value.equals(newValue)) {
            value = newValue;
            if (immediate != null && immediate
                    || immediate == null && getState().immediate) {
                updateValueOnServer();
            } else {
                changed = true;
            }
        }
    }

    protected void updateValueOnServer() {
        if (!getState().readOnly) {
            serverRpc.setTextValue(value);
            lastSendValue = value;
            changed = false;
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent event) {
        super.onStateChanged(event);
        SignatureFieldState state = getState();
        VSignatureField field = getWidget();

        field.setDotSize(state.dotSize);
        field.setMaxWidth(state.maxWidth);
        field.setMinWidth(state.minWidth);
        field.setPenColor(state.penColor);
        field.setVelocityFilterWeight(state.velocityFilterWeight);

        if (event.hasPropertyChanged("clearButtonEnabled")) {
            field.setClearButtonVisible(state.clearButtonEnabled);
        }
        
        if (event.hasPropertyChanged("readOnly")) {
            field.setReadOnly(state.readOnly);
            if (state.readOnly) {
                field.setClearButtonVisible(false);
            }
        }

        if (event.hasPropertyChanged("mimeType") && !getWidget().isEmpty()) {
            String mimeType = getMimeType().getMimeType();
            String newValue = getDataURL(mimeType);
            changeValue(newValue, true);
        }

        if (event.hasPropertyChanged("backgroundColor")) {
            field.setBackgroundColor(state.backgroundColor);
        }

        if (event.hasPropertyChanged("height")
                || event.hasPropertyChanged("width")) {
            updateCanvasSize();
        }
        
        if (event.hasPropertyChanged("value")) {
            String stateValue = state.value;
            if (stateValue == null || stateValue.isEmpty()) {
                field.getSignaturePad().clear();
                value = null;
            } else {
                if (lastSendValue == null ||
                        !lastSendValue.equals(stateValue)) {
                    getWidget().fromDataURL(stateValue);
                }
            }
            value = stateValue;
        }
    }

    public boolean updateCanvasSize() {
        VSignatureField field = getWidget();
        Canvas canvas = field.getCanvas();
        int oldWidth = canvas.getCoordinateSpaceWidth();
        int newWidth = field.getElement().getClientWidth();
        int oldHeight = canvas.getCoordinateSpaceHeight();
        int newHeight = field.getElement().getClientHeight();
        boolean empty = field.isEmpty();
        boolean sizeChanged = false;
        if (oldWidth != newWidth || oldHeight != newHeight) {
            canvas.setCoordinateSpaceWidth(newWidth);
            canvas.setCoordinateSpaceHeight(newHeight);
            if (!empty) {
                field.getSignaturePad().fromDataURL(value);
            }
            sizeChanged = true;
        }
        return sizeChanged;
    }

    @Override
    public SignatureFieldState getState() {
        return (SignatureFieldState) super.getState();
    }

    @Override
    public VSignatureField getWidget() {
        return (VSignatureField) super.getWidget();
    }

}
