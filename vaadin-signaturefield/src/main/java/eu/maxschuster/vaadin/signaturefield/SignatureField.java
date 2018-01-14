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

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataUrlConverter;

import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.util.Collection;
import java.util.logging.Logger;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

/**
 * A {@link CustomField} to capture user signatures as data url {@link String}.<br>
 * <br>
 * If you need extended access to the data urls content you can use the
 * {@link StringToDataUrlConverter} that converts the String value to
 * {@link DataUrl} that allows access to the binary contents of the data url.
 * <br>
 * <br>
 * <a href="https://github.com/szimek/signature_pad">signature_pad</a> 
 * by Szymon Nowak (<a href="https://github.com/szimek">szimek</a>) is used to
 * capture the signature at the client-side.
 * 
 * @author Max Schuster
 * @see StringToDataUrlConverter
 * @see DataUrl
 * @see <a href="https://github.com/szimek/signature_pad">signature_pad</a>
 */
public class SignatureField extends CustomField<String> {
    
    private static final long serialVersionUID = 2L;
    
    /**
     * The extension instance
     */
    private final SignatureFieldExtension extension;
    
    private String value;

    /**
     * Creates a new SignatureField instance
     */
    public SignatureField() {
        this(null);
    }
    
    /**
     * Creates a new SignatureField instance with a caption
     * 
     * @param caption 
     *          Field caption
     */
    public SignatureField(String caption) {
        super();
        
        extension = initExtension();

        setImmediate(false);
        setHeight(100, Unit.PIXELS);
        setWidth(300, Unit.PIXELS);
        
        setCaption(caption);
        setPrimaryStyleName("signaturefield");
    }

    /**
     * Allways returns <code>null</code>
     * @return Allways <code>null</code>
     */
    @Override
    protected final Component initContent() {
        return null;
    }

    /**
     * Allways returns <code>null</code>
     * @return Allways <code>null</code>
     */
    @Override
    protected final Component getContent() {
        return null;
    }

    @Override
    protected void doSetValue(String value) {
        this.value = value;
        extension.setSignature(value);
    }

    @Override
    protected boolean setValue(String value, boolean userOriginated) {
        return super.setValue(value, userOriginated); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getValue() {
        return value;
    }
    
    /**
     * Creates the javascript extension used to communicate with the
     * client-side.
     * @return The extension of this field
     */
    private SignatureFieldExtension initExtension() {
        SignatureFieldExtension ext = new SignatureFieldExtension(this);
        ext.addSignatureChangeListener(e -> setValue(e.getSignature(), true));
        return ext;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        extension.setReadOnly(readOnly);
    }

    public boolean getImmediate() {
        return extension.getImmediate();
    }
    
    public void setImmediate(boolean immediate) {
        extension.setImmediate(immediate);
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
        if (attr.hasKey("mime-type")) {
            MimeType mimeType = null;
            String mimeTypeString = DesignAttributeHandler.getFormatter().parse(
                     attr.get("mime-type"), String.class);
            try {
                mimeType = MimeType.valueOfMimeType(mimeTypeString);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(SignatureField.class.getName()).info(
                        "Unsupported MIME-Type found when reading from design : "
                                .concat(mimeTypeString));
            }
            setMimeType(mimeType);
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        Attributes attr = design.attributes();
        SignatureField def = designContext.getDefaultInstance(this);
        MimeType mimeType = getMimeType();
        if (mimeType != null) {
            String mimeTypeDef = null;
            if (def.getMimeType() != null) {
                mimeTypeDef = getMimeType().getMimeType();
            }
            DesignAttributeHandler.writeAttribute("mime-type", attr,
                    mimeType.getMimeType(), mimeTypeDef, String.class, designContext);
        }
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> a = super.getCustomAttributes();
        a.add("mime-type");
        return a;
    }
    
    /**
     * Gets the radius of a single dot.
     *
     * @return Radius of a single dot.
     */
    public Double getDotSize() {
        return extension.getDotSize();
    }

    /**
     * Sets the radius of a single dot.
     *
     * @param dotSize Radius of a single dot.
     */
    public void setDotSize(Double dotSize) {
        extension.setDotSize(dotSize);
    }

    /**
     * Sets the radius of a single dot.
     *
     * @param dotSize Radius of a single dot.
     * @return This {@link SignatureField}
     */
    public SignatureField withDotSize(Double dotSize) {
        extension.setDotSize(dotSize);
        return this;
    }

    /**
     * Gets the minimum width of a line. Defaults to 0.5.
     *
     * @return Minimum width of a line.
     */
    public double getMinWidth() {
        return extension.getMinWidth();
    }

    /**
     * Sets the minimum width of a line. Defaults to 0.5.
     *
     * @param minWidth Minimum width of a line.
     */
    public void setMinWidth(double minWidth) {
        extension.setMinWidth(minWidth);
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
        return extension.getMaxWidth();
    }

    /**
     * Sets the maximum width of a line.
     *
     * @param maxWidth Maximum width of a line.
     */
    public void setMaxWidth(double maxWidth) {
        extension.setMaxWidth(maxWidth);
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
     * Some predefined colors can be found in class {@link SampleColors}
     *
     * @return Color used to clear the background.
     */
    public String getBackgroundColor() {
        return extension.getBackgroundColor();
    }

    /**
     * Sets the color used to clear the background. Can be any color format
     * accepted by context.fillStyle. Defaults to "rgba(0,0,0,0)" (transparent
     * black). Use a non-transparent color e.g. "rgb(255,255,255)" (opaque
     * white) if you'd like to save signatures as JPEG images.<br>
     * <br>
     * Some predefined colors can be found in class {@link SampleColors}
     *
     * @param backgroundColor Color used to clear the background.
     */
    public void setBackgroundColor(String backgroundColor) {
        extension.setBackgroundColor(backgroundColor);
    }

    /**
     * Sets the color used to clear the background. Can be any color format
     * accepted by context.fillStyle. Defaults to "rgba(0,0,0,0)" (transparent
     * black). Use a non-transparent color e.g. "rgb(255,255,255)" (opaque
     * white) if you'd like to save signatures as JPEG images.<br>
     * <br>
     * Some predefined colors can be found in class {@link SampleColors}
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
     * Some predefined colors can be found in class {@link SampleColors}
     *
     * @return The color used to draw the lines.
     */
    public String getPenColor() {
        return extension.getPenColor();
    }

    /**
     * Sets the color used to draw the lines. Can be any color format accepted
     * by context.fillStyle.<br>
     * <br>
     * Some predefined colors can be found in class {@link SampleColors}
     *
     * @param penColor The color used to draw the lines.
     */
    public void setPenColor(String penColor) {
        extension.setPenColor(penColor);
    }
    
    /**
     * Sets the color used to draw the lines. Can be any color format accepted
     * by context.fillStyle.<br>
     * <br>
     * Some predefined colors can be found in class {@link SampleColors}
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
        return extension.getVelocityFilterWeight();
    }

    /**
     * Sets the velocity filter weight
     *
     * @param velocityFilterWeight The velocity filter weight
     */
    public void setVelocityFilterWeight(double velocityFilterWeight) {
        extension.setVelocityFilterWeight(velocityFilterWeight);
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
        return extension.getMimeType();
    }

    /**
     * Sets the {@link MimeType} of generated images
     *
     * @param mimeType The {@link MimeType} of generated images
     */
    public void setMimeType(MimeType mimeType) {
        extension.setMimeType(mimeType);
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
        return extension.isClearButtonEnabled();
    }

    /**
     * Sets the visibility of the clear button
     *
     * @param clearButtonEnabled Should show a clear button in the
     * {@link SignatureField}
     */
    public void setClearButtonEnabled(boolean clearButtonEnabled) {
        extension.setClearButtonEnabled(clearButtonEnabled);
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
