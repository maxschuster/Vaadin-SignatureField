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
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataUrlConverter;

import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldServerRpc;
import eu.maxschuster.vaadin.signaturefield.shared.SignatureFieldState;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

/**
 * A {@link Field} to capture user signatures as data url {@link String}.<br>
 * <br>
 * If you need extended access to the data urls content you can use the
 * {@link StringToDataUrlConverter} that converts the String value to
 * {@link DataUrl} that allows access to the binary contents of the data url.
 * <br>
 * <br>
 * It is based on <b>signature_pad</b> by <b>Szymon Nowak
 * (<a href="https://github.com/szimek">szimek</a>)</b> that has been ported to
 * GWT.<br>
 * <br>
 * The MIT licensed original version can be found at 
 * <a href="https://github.com/szimek/signature_pad">
 * https://github.com/szimek/signature_pad</a><br>
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
        if (initial) {
            getState().value = getValue();
        }
    }

    @Override
    protected void setInternalValue(String newValue) {
        super.setInternalValue(newValue);
        // Update the state value without causing a redraw
        getState(false).value = newValue;
    }

    /**
     * Clears the field.<br>
     * It's an alias for setValue(null)
     * 
     * @see #setValue(java.lang.Object)
     */
    @Override
    public void clear() {
        setValue(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#readDesign(org.jsoup.nodes .Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        Attributes attr = design.attributes();
        if (design.hasAttr("dot-size")) {
            setDotSize(DesignAttributeHandler.readAttribute(
                    "dot-size", attr, Double.class));
        }
        if (design.hasAttr("min-width")) {
            setMinWidth(DesignAttributeHandler.readAttribute(
                    "min-width", attr, Double.class));
        }
        if (design.hasAttr("max-width")) {
            setMaxWidth(DesignAttributeHandler.readAttribute(
                    "max-width", attr, Double.class));
        }
        if (design.hasAttr("background-color")) {
            setBackgroundColor(DesignAttributeHandler.readAttribute(
                    "background-color", attr, String.class));
        }
        if (design.hasAttr("pen-color")) {
            setPenColor(DesignAttributeHandler.readAttribute(
                    "pen-color", attr, String.class));
        }
        if (design.hasAttr("velocity-filter-weight")) {
            setVelocityFilterWeight(DesignAttributeHandler.readAttribute(
                    "velocity-filter-weight", attr, Double.class));
        }
        if (design.hasAttr("clear-button")) {
            setClearButtonEnabled(DesignAttributeHandler.readAttribute(
                    "clear-button", attr, Boolean.class));
        }
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
     * Sets the radius of a single dot.
     *
     * @param dotSize Radius of a single dot.
     * @return This {@link SignatureField}
     */
    public SignatureField withDotSize(Double dotSize) {
        getState().dotSize = dotSize;
        return this;
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
     * Sets the minimum width of a line. Defaults to 0.5.
     *
     * @param minWidth Minimum width of a line.
     * @return This {@link SignatureField}
     */
    public SignatureField withMinWidth(double minWidth) {
        setMinWidth(minWidth);
        return this;
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
     * Sets the maximum width of a line.
     *
     * @param maxWidth Maximum width of a line.
     * @return This {@link SignatureField}
     */
    public SignatureField withMaxWidth(double maxWidth) {
        setMaxWidth(maxWidth);
        return this;
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
     * Sets the color used to clear the background. Can be any color format
     * accepted by context.fillStyle. Defaults to "rgba(0,0,0,0)" (transparent
     * black). Use a non-transparent color e.g. "rgb(255,255,255)" (opaque
     * white) if you'd like to save signatures as JPEG images.<br>
     * <br>
     * Some predefined colors can be found in class {@link Color}
     *
     * @param backgroundColor Color used to clear the background.
     * @return This {@link SignatureField}
     */
    public SignatureField withBackgroundColor(String backgroundColor) {
        setBackgroundColor(backgroundColor);
        return this;
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
     * Sets the color used to draw the lines. Can be any color format accepted
     * by context.fillStyle.<br>
     * <br>
     * Some predefined colors can be found in class {@link Color}
     *
     * @param penColor The color used to draw the lines.
     * @return This {@link SignatureField}
     */
    public SignatureField withPenColor(String penColor) {
        setPenColor(penColor);
        return this;
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
     * Sets the velocity filter weight
     *
     * @param velocityFilterWeight The velocity filter weight
     * @return This {@link SignatureField}
     */
    public SignatureField withVelocityFilterWeight(double velocityFilterWeight) {
        setVelocityFilterWeight(velocityFilterWeight);
        return this;
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
     * Sets the {@link MimeType} of generated images
     *
     * @param mimeType The {@link MimeType} of generated images
     * @return This {@link SignatureField}
     */
    public SignatureField withMimeType(MimeType mimeType) {
        setMimeType(mimeType);
        return this;
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
    
    /**
     * Sets the visibility of the clear button
     *
     * @param clearButtonEnabled Should show a clear button in the
     * {@link SignatureField}
     * @return This {@link SignatureField}
     */
    public SignatureField withClearButtonEnabled(boolean clearButtonEnabled) {
        setClearButtonEnabled(clearButtonEnabled);
        return this;
    }
    /**
     * Sets the error that is shown if the field value cannot be converted to
     * the data source type. If {0} is present in the message, it will be
     * replaced by the simple name of the data source type. If {1} is present in
     * the message, it will be replaced by the ConversionException message.
     * 
     * @param valueConversionError Message to be shown when conversion of the 
     * value fails
     * @return This {@link SignatureField}
     * @see #setConversionError(java.lang.String) 
     * @see #setImmediate(boolean) 
     */
    public SignatureField withConversionError(String valueConversionError) {
        setImmediate(true);
        setConversionError(valueConversionError);
        return this;
    }

    /**
     * Sets the converter used to convert the field value to property data
     * source type. The converter must have a presentation type that matches the
     * field type.
     * 
     * @param converter The new converter to use.
     * @return This {@link SignatureField}
     * @see #setConverter(com.vaadin.data.util.converter.Converter) 
     * @see #setImmediate(boolean) 
     */
    public SignatureField withConverter(Converter<String, ?> converter) {
        setImmediate(true);
        setConverter(converter);
        return this;
    }

    /**
     * Sets the width of the object to "100%". 
     * @return This {@link SignatureField}
     * @see #setWidth(float, com.vaadin.server.Sizeable.Unit) 
     */
    public SignatureField withFullWidth() {
        setWidth(100f, Unit.PERCENTAGE);
        return this;
    }

    /**
     * Sets the height of the object to "100%".
     * @return This {@link SignatureField}
     * @see #setHeight(float, com.vaadin.server.Sizeable.Unit)  
     */
    public SignatureField withFullHeight() {
        setHeight(100f, Unit.PERCENTAGE);
        return this;
    }
    
    /**
     * Changes the readonly state and throw read-only status change events.
     * 
     * @param readOnly a boolean value specifying whether the component is put
     * read-only mode or not
     * @return This {@link SignatureField}
     * @see #setReadOnly(boolean)
     */
    public SignatureField withReadOnly(boolean readOnly) {
        setReadOnly(readOnly);
        return this;
    }

    /**
     * Adds a new validator for the field's value. All validators added to a
     * field are checked each time the its value changes.
     * 
     * @param validator the new validator to be added.
     * @return This {@link SignatureField}
     * @see #addValidator(com.vaadin.data.Validator) 
     */
    public SignatureField withValidator(Validator validator) {
        setImmediate(true);
        addValidator(validator);
        return this;
    }

    /**
     * Sets the width of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @param width the width of the object.
     * @param unit the unit used for the width.
     * @return This {@link SignatureField}
     * @see #setWidth(float, com.vaadin.server.Sizeable.Unit) 
     */
    public SignatureField withWidth(float width, Unit unit) {
        setWidth(width, unit);
        return this;
    }
    
    /**
     * Sets the width of the component using String presentation.
     * 
     * @param width in CSS style string representation, null or empty string to
     * reset
     * @return This {@link SignatureField}
     * @see #setWidth(java.lang.String) 
     */
    public SignatureField withWidth(String width) {
        setWidth(width);
        return this;
    }

    /**
     * Sets the height of the object. Negative number implies unspecified size
     * (terminal is free to set the size).
     * 
     * @param height the height of the object.
     * @param unit the unit used for the width.
     * @return This {@link SignatureField}
     * @see #setHeight(float, com.vaadin.server.Sizeable.Unit) 
     */
    public SignatureField withHeight(float height, Unit unit) {
        setHeight(height, unit);
        return this;
    }

    /**
     * Sets the height of the component using String presentation.
     * 
     * @param height Height of the component
     * @return This {@link SignatureField}
     * @see #setHeight(java.lang.String) 
     */
    public SignatureField withHeight(String height) {
        setHeight(height);
        return this;
    }

}
