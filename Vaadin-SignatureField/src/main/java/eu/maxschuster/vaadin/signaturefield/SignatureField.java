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

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldServerRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldState;

/**
 * A {@link Field} wrapping the SignaturePad javascript object by <b>Szymon
 * Nowak (szimek)</b> that can be found at
 * <a href="https://github.com/szimek/signature_pad">
 * https://github.com/szimek/signature_pad</a>
 *
 * @author Max Schuster
 */
public class SignatureField extends AbstractField<String> {

    public static final String COLOR_WHITE = "white";
    public static final String COLOR_BLACK = "black";
    public static final String COLOR_BLACK_TRANSPARENT = "rgba(0,0,0,0)";
    public static final String COLOR_ULTRAMARIN = "#120a8f";

    /**
     * A {@link Field} wrapping the SignaturePad javascript object by <b>Szymon
     * Nowak (szimek)</b> that can be found at
     * <a href="https://github.com/szimek/signature_pad">
     * https://github.com/szimek/signature_pad</a>
     *
     * @author Max Schuster
     */
    public SignatureField() {
        super();
        registerRpc(new SignatureFieldServerRpc() {

            @Override
            public void setTextValue(String textValue) {
                setValue(textValue, true);
            }
        });

        setImmediate(false);
        setHeight(100, Unit.PIXELS);
        setWidth(300, Unit.PIXELS);
    }

    @Override
    public Class<? extends String> getType() {
        return String.class;
    }

    @Override
    protected SignatureFieldState getState() {
        return (SignatureFieldState) super.getState();
    }

    // Make is empty public
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        getState().dataUrl = getValue();
    }

    /**
     * Clears the field
     */
    public void clear() {
        setValue(null);
    }
    
    /**
     * Gets the radius of a single dot.
     *
     * @return Radius of a single dot.
     */
    public Double getDotSize() {
        return getState().dotSize;
    }

    /**
     * Sets the radius of a single dot.
     *
     * @param dotSize Radius of a single dot.
     */
    public void setDotSize(Double dotSize) {
        getState().dotSize = dotSize;
    }

    /**
     * Gets the minimum width of a line. Defaults to 0.5.
     *
     * @return Minimum width of a line.
     */
    public double getMinWidth() {
        return getState().minWidth;
    }

    /**
     * Sets the minimum width of a line. Defaults to 0.5.
     *
     * @param minWidth Minimum width of a line.
     */
    public void setMinWidth(double minWidth) {
        getState().minWidth = minWidth;
    }

    /**
     * Gets the maximum width of a line.
     *
     * @return Maximum width of a line.
     */
    public double getMaxWidth() {
        return getState().maxWidth;
    }

    /**
     * Sets the maximum width of a line.
     *
     * @param maxWidth Maximum width of a line.
     */
    public void setMaxWidth(double maxWidth) {
        getState().maxWidth = maxWidth;
    }

    /**
     * Gets the color used to clear the background. Can be any color format
     * accepted by context.fillStyle. Defaults to "rgba(0,0,0,0)" (transparent
     * black). Use a non-transparent color e.g. "rgb(255,255,255)" (opaque
     * white) if you'd like to save signatures as JPEG images.
     *
     * @return Color used to clear the background.
     */
    public String getBackgroundColor() {
        return getState().backgroundColor;
    }

    /**
     * Sets the color used to clear the background. Can be any color format
     * accepted by context.fillStyle. Defaults to "rgba(0,0,0,0)" (transparent
     * black). Use a non-transparent color e.g. "rgb(255,255,255)" (opaque
     * white) if you'd like to save signatures as JPEG images.
     *
     * @param backgroundColor Color used to clear the background.
     */
    public void setBackgroundColor(String backgroundColor) {
        getState().backgroundColor = backgroundColor;
    }

    /**
     * Sets the color used to draw the lines. Can be any color format accepted
     * by context.fillStyle.
     *
     * @return The color used to draw the lines.
     */
    public String getPenColor() {
        return getState().penColor;
    }

    /**
     * Sets the color used to draw the lines. Can be any color format accepted
     * by context.fillStyle.
     *
     * @param penColor The color used to draw the lines.
     */
    public void setPenColor(String penColor) {
        getState().penColor = penColor;
    }

    /**
     * Gets the velocity filter weight
     *
     * @return The velocity filter weight
     */
    public double getVelocityFilterWeight() {
        return getState().velocityFilterWeight;
    }

    /**
     * Sets the velocity filter weight
     *
     * @param velocityFilterWeight The velocity filter weight
     */
    public void setVelocityFilterWeight(double velocityFilterWeight) {
        getState().velocityFilterWeight = velocityFilterWeight;
    }

    /**
     * Sets the {@link MimeType} of generated images
     *
     * @return The {@link MimeType} of generated images
     */
    public MimeType getMimeType() {
        return getState().mimeType;
    }

    /**
     * Sets the {@link MimeType} of generated images
     *
     * @param mimeType The {@link MimeType} of generated images
     */
    public void setMimeType(MimeType mimeType) {
        getState().mimeType = mimeType;
    }

    /**
     * Gets the visibility of the clear button
     *
     * @return Should show a clear button in the {@link SignatureField}
     */
    public boolean isClearButtonEnabled() {
        return getState().clearButtonEnabled;
    }

    /**
     * Sets the visibility of the clear button
     *
     * @param clearButtonEnabled Should show a clear button in the
     * {@link SignatureField}
     */
    public void setClearButtonEnabled(boolean clearButtonEnabled) {
        getState().clearButtonEnabled = clearButtonEnabled;
    }

}
