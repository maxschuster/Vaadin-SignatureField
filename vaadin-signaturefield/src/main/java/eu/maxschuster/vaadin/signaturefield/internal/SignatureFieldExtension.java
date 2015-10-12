/*
 * Copyright 2015 Max Schuster.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.maxschuster.vaadin.signaturefield.internal;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.util.ReflectTools;
import elemental.json.JsonArray;
import elemental.json.JsonString;
import elemental.json.JsonValue;
import eu.maxschuster.vaadin.signaturefield.SignatureField;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import eu.maxschuster.vaadin.signaturefield.shared.internal.SignatureFieldExtensionState;
import java.lang.reflect.Method;
import eu.maxschuster.vaadin.signaturefield.Color;

/**
 * A javascript extension that extends a {@link SignatureField} with the
 * client-side logic.
 * 
 * <a href="https://github.com/szimek/signature_pad">signature_pad</a> 
 * by Szymon Nowak (<a href="https://github.com/szimek">szimek</a>) is used to
 * capture the signature at the client-side.
 * @author Max Schuster
 * @see <a href="https://github.com/szimek/signature_pad">signature_pad</a>
 */
@JavaScript("vaadin://addons/signaturefield/dist/SignatureFieldExtension.min.js")
@StyleSheet("vaadin://addons/signaturefield/dist/SignatureFieldExtension.css")
public class SignatureFieldExtension extends AbstractJavaScriptExtension {
    
    /**
     * Current signature value
     */
    private String signature;
    
    /**
     * Listener that gets called when the signature changes
     */
    public interface SignatureChangeListener {
        
        public static final Method METHOD = ReflectTools.findMethod(
                SignatureChangeListener.class, "signatureChange",
                SignatureChangeEvent.class);
        
        public void signatureChange(SignatureChangeEvent event);
        
    }
    
    /**
     * A signature change event
     */
    public static class SignatureChangeEvent extends Component.Event {
        
        private final SignatureFieldExtension extension;
        
        private final String signature;

        public SignatureChangeEvent(Component source,
                SignatureFieldExtension extension, String signature) {
            super(source);
            this.extension = extension;
            this.signature = signature;
        }

        /**
         * @return The extension that has fired this event
         */
        public SignatureFieldExtension getExtension() {
            return extension;
        }

        /**
         * @return The new signature value
         */
        public String getSignature() {
            return signature;
        }
        
    }

    public SignatureFieldExtension(SignatureField target) {
        // extend the target
        super(target);
        
        /*
         * Gets called from the client-side when it wants to change the signatue
         * value at the server-side.
         */
        addFunction("fireSignatureChange", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) {
                String signature;
                JsonValue jsonValue = arguments.get(0);
                if (jsonValue instanceof JsonString) {
                    signature = jsonValue.asString();
                } else {
                    signature = null;
                }
                setSignature(signature, true);
                fireSignatureChangeEvent(signature);
            }
        });
    }

    @Override
    protected SignatureFieldExtensionState getState() {
        return (SignatureFieldExtensionState) super.getState();
    }

    @Override
    protected SignatureFieldExtensionState getState(boolean markAsDirty) {
        return (SignatureFieldExtensionState) super.getState(markAsDirty);
    }
    
    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        if (initial) {
            updateSignature();
        }
    }
    
    /**
     * Updates the client-side with the current signature value
     */
    protected void updateSignature() {
        callFunction("setSignature", getSignature());
    }
    
    /**
     * @return Current signature value
     */
    public String getSignature() {
        return signature;
    }
    
    /**
     * Set signature current signature value.
     * @param signature Signature
     * @param repaintIsNotNeeded Repaint is not needed
     */
    public void setSignature(String signature, boolean repaintIsNotNeeded) {
        String oldSignature = this.signature;
        if (!SharedUtil.equals(oldSignature, signature)) {
            this.signature = signature;
            if (!repaintIsNotNeeded) {
                updateSignature();
            }
        }
    }
    
    /**
     * Set signature current signature value.
     * @param signature Signature
     */
    public void setSignature(String signature) {
        setSignature(signature, false);
    }
    
    /**
     * Fires a new {@link SignatureChangeEvent} with the given Signature.
     * @param signature New signature
     */
    public void fireSignatureChangeEvent(String signature) {
        fireEvent(new SignatureChangeEvent((Component) getParent(), this, signature));
    }
    
    /**
     * Adds a {@link SignatureChangeListener}.
     * @param listener Listener to add
     */
    public void addSignatureChangeListener(SignatureChangeListener listener) {
        addListener(SignatureChangeEvent.class, listener, SignatureChangeListener.METHOD);
    }
    
    /**
     * Removes a {@link SignatureChangeListener}.
     * @param listener Listener to remove
     */
    public void removeSignatureChangeListener(SignatureChangeListener listener) {
        removeListener(SignatureChangeEvent.class, listener, SignatureChangeListener.METHOD);
    }
    
    /**
     * Returns true if the extension is immediate.
     * @return Extension is immediate.
     */
    public boolean getImmediate() {
        return getState(false).immediate;
    }
    
    /**
     * Sets the immediate value of this extension.
     * @param immediate New immediate value of this extension.
     */
    public void setImmediate(boolean immediate) {
        getState().immediate = immediate;
    }
    
    /**
     * Returns true if extension is read only.
     * @return Extension is read only.
     */
    public boolean getReadOnly() {
        return getState(false).readOnly;
    }
    
    /**
     * Sets the read only value of this extension.
     * @param readOnly Extension is read only.
     */
    public void setReadOnly(boolean readOnly) {
        getState().readOnly = readOnly;
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
        return MimeType.valueOfMimeType(getState(false).mimeType);
    }

    /**
     * Sets the {@link MimeType} of generated images
     *
     * @param mimeType The {@link MimeType} of generated images
     */
    public void setMimeType(MimeType mimeType) {
        getState().mimeType = mimeType.getMimeType();
    }

    /**
     * Gets the visibility of the clear button
     *
     * @return Should show a clear button in the {@link SignatureFieldExtension}
     */
    public boolean isClearButtonEnabled() {
        return getState(false).clearButtonEnabled;
    }

    /**
     * Sets the visibility of the clear button
     *
     * @param clearButtonEnabled Should show a clear button in the
     * {@link SignatureFieldExtension}
     */
    public void setClearButtonEnabled(boolean clearButtonEnabled) {
        getState().clearButtonEnabled = clearButtonEnabled;
    }
    
}
