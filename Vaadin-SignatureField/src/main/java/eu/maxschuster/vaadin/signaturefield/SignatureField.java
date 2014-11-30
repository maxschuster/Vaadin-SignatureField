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

import com.vaadin.data.Property;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.ui.AbstractField;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataUrlConverter;

import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldServerRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldState;

/**
 * An {@link AbstractField} implementation for capturing user signatures as
 * data url {@link String}.<br>
 * <br>
 * If you need extended access to the data urls content you can use the
 * {@link StringToDataUrlConverter} that converts the String value to
 * {@link DataUrl} that allows access to the binary contents of the data url.
 * <br>
 * <br>
 * It is based on {@code SignaturePad} by <b>Szymon Nowak (
 * <a href="https://github.com/szimek">szimek</a>)</b> that has been ported to
 * GWT.<br>
 * <br>
 * The MIT licensed original version can be found at 
 * <a href="https://github.com/szimek/signature_pad">
 * https://github.com/szimek/signature_pad</a><br>
 * <br>
 *
 * @author Max Schuster
 * @see StringToDataUrlConverter
 * @see DataUrl
 * @see <a href="https://github.com/szimek/signature_pad">
 * SignaturePad (JavaScript)</a>
 */
public class SignatureField extends AbstractField<String> {

    /**
     * Creates a new SignatureField instance
     */
    public SignatureField() {
        this(null, null);
    }
    
    /**
     * Creates a new SignatureField instance with a caption
     * 
     * @param caption 
     *          Field caption
     */
    public SignatureField(String caption) {
        this(caption, null);
    }
    
    /**
     * Creates a new SignatureField instance with a data source
     * 
     * @param dataSource 
     *          Property data source
     */
    public SignatureField(Property<?> dataSource) {
        this(null, dataSource);
    }
    
    /**
     * Creates a new SignatureField instance with a caption and data source
     * 
     * @param caption 
     *          Field caption
     * @param dataSource 
     *          Property data source
     */
    public SignatureField(String caption, Property<?> dataSource) {
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
        
        setCaption(caption);
        setPropertyDataSource(dataSource);
    }

    @Override
    public Class<? extends String> getType() {
        return String.class;
    }

    @Override
    protected SignatureFieldState getState() {
        return (SignatureFieldState) super.getState();
    }

    @Override
    protected SignatureFieldState getState(boolean markAsDirty) {
        return (SignatureFieldState) super.getState(markAsDirty);
    }

    /**
     * Is the field empty?<br>
     * The field is considered empty if its value
     * is {@code null}
     * 
     * @return Is the field empty?
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        
        getState().value = getValue();
        System.err.println("value = " + getState().value);
    }
    
    private boolean isEqualNullSafe(Object value1, Object value2) {
        if (value1 == null && value2 == null) {
            return true;
        } else if (value1 != null && value2 != null) {
            return value1.equals(value2);
        }
        return false;
    }

    /**
     * Clears the field.<br>
     * It's an alias for setValue(null)
     * 
     * @see #setValue(java.lang.Object)
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
        return getState(false).dotSize;
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
        return getState(false).minWidth;
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
        return getState(false).maxWidth;
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
     * white) if you'd like to save signatures as JPEG images.<br>
     * <br>
     * Some predefined colors can be found in class {@link Color}
     *
     * @return Color used to clear the background.
     */
    public String getBackgroundColor() {
        return getState(false).backgroundColor;
    }

    /**
     * Sets the color used to clear the background. Can be any color format
     * accepted by context.fillStyle. Defaults to "rgba(0,0,0,0)" (transparent
     * black). Use a non-transparent color e.g. "rgb(255,255,255)" (opaque
     * white) if you'd like to save signatures as JPEG images.<br>
     * <br>
     * Some predefined colors can be found in class {@link Color}
     *
     * @param backgroundColor Color used to clear the background.
     */
    public void setBackgroundColor(String backgroundColor) {
        getState().backgroundColor = backgroundColor;
    }

    /**
     * Sets the color used to draw the lines. Can be any color format accepted
     * by context.fillStyle.<br>
     * <br>
     * Some predefined colors can be found in class {@link Color}
     *
     * @return The color used to draw the lines.
     */
    public String getPenColor() {
        return getState(false).penColor;
    }

    /**
     * Sets the color used to draw the lines. Can be any color format accepted
     * by context.fillStyle.<br>
     * <br>
     * Some predefined colors can be found in class {@link Color}
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
        return getState(false).velocityFilterWeight;
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
        return getState(false).mimeType;
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
        return getState(false).clearButtonEnabled;
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
