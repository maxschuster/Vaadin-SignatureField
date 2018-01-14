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
package eu.maxschuster.vaadin.signaturefield.demo;

import com.vaadin.annotations.PreserveOnRefresh;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Notification;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlBuilder;
import eu.maxschuster.dataurl.DataUrlEncoding;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import eu.maxschuster.vaadin.colorconverters.ColorToRgbaConverter;
import eu.maxschuster.vaadin.signaturefield.SignatureField;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataUrlConverter;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

@Theme("demo")
@PreserveOnRefresh
public class DemoUI extends DemoUILayout {
            
    private static final long serialVersionUID = 2L;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(
            productionMode = true,
            ui = DemoUI.class,
            widgetset = "eu.maxschuster.vaadin.signaturefield.demo.DemoWidgetSet"
    )
    public static class Servlet extends VaadinServlet {
        
        private static final long serialVersionUID = 1L;
            
    }
    
    private final Binder<SignatureField> binder = new Binder<>(SignatureField.class);

    private final IDataUrlSerializer serializer = new DataUrlSerializer();
    
    private final StringToDoubleConverter doubleConverter
            = new StringToDoubleConverter(0d, "Please enter a valid nubmer");
    
    private final StringToDataUrlConverter dataUrlConverter
            = new StringToDataUrlConverter();
    
    private final BrowserWindowOpener saveOpener = new BrowserWindowOpener("");
    
    private int counter = 0;

    @Override
    protected void init(VaadinRequest request) {
        
        // Set href and target manually
        // ToDo: Move back to declarative layout when vaadin bug is fixed
        signaturePadLink.setResource(new ExternalResource(
                "https://github.com/szimek/signature_pad"));
        signaturePadLink.setTargetName("_blank");
        signatureFieldLink.setResource(new ExternalResource(
                "https://github.com/maxschuster/Vaadin-SignatureField"));
        signatureFieldLink.setTargetName("_blank");
        
        getPage().setTitle(pageTitleLabel.getValue());
        
        signatureField.setBackgroundColor("rgba(255,255,255, 1)");
        signatureField.setPenColor("rgba(18, 10, 143, 1)");
        binder.forField(signatureField)
                .bind(SignatureField::getValue, SignatureField::setValue);
        
        clearButton.addClickListener(e -> signatureField.clear());
        
        saveOpener.setWindowName("_blank");
        saveOpener.extend(saveButton);
        
        mimeTypeComboBox.setItems(Arrays.asList(MimeType.values()));
        mimeTypeComboBox.setEmptySelectionAllowed(false);
        binder.forField(mimeTypeComboBox)
                .bind(SignatureField::getMimeType, SignatureField::setMimeType);
        mimeTypeComboBox.addValueChangeListener(e -> updateResources());
        
        mimeTypeComboBox.setValue(signatureField.getMimeType());
        
        binder.forField(dotSizeTextField)
                .withNullRepresentation("")
                .withConverter(doubleConverter)
                .bind(SignatureField::getDotSize, SignatureField::setDotSize);
        
        binder.forField(minWidthTextField)
                .withConverter(doubleConverter)
                .asRequired()
                .bind(SignatureField::getMinWidth, SignatureField::setMinWidth);
        
        binder.forField(maxWidthTextField)
                .withConverter(doubleConverter)
                .asRequired()
                .bind(SignatureField::getMaxWidth, SignatureField::setMaxWidth);
        
        binder.forField(velocityFilterWeightTextField)
                .withConverter(doubleConverter)
                .asRequired()
                .bind(SignatureField::getVelocityFilterWeight, SignatureField::setVelocityFilterWeight);
        
        binder.forField(backgroundColorColorPicker)
                .withConverter(new ColorToRgbaConverter("Error converting color"))
                .bind(SignatureField::getBackgroundColor, SignatureField::setBackgroundColor);
        
        backgroundColorColorPicker.addValueChangeListener(e -> {
            Notification.show(
                    "The background color will change after\n"
                            + "you have cleared the signature field!",
                    Notification.Type.WARNING_MESSAGE
            );
        });
        
        binder.forField(penColorColorPicker)
                .withConverter(new ColorToRgbaConverter("Error converting color"))
                .bind(SignatureField::getPenColor, SignatureField::setPenColor);

        binder.forField(immediateCheckBox)
                .bind(SignatureField::getImmediate, SignatureField::setImmediate);

        readOnlyCheckBox.addValueChangeListener(e -> {
            boolean readOnly = e.getValue();
            signatureField.setReadOnly(readOnly);
            mimeTypeComboBox.setReadOnly(readOnly);
            clearButton.setEnabled(!readOnly);
        });

        binder.forField(requiredCheckBox)
                .bind(SignatureField::isRequiredIndicatorVisible, SignatureField::setRequiredIndicatorVisible);

        binder.forField(clearButtonEnabledCheckBox)
                .bind(SignatureField::isClearButtonEnabled, SignatureField::setClearButtonEnabled);
        
        binder.forField(dataUrlAsText)
                .bind(SignatureField::getValue, SignatureField::setValue);

        emptyLabel.setValue(String.valueOf(signatureField.isEmpty()));

        signatureField.addValueChangeListener(e -> updateResources());
        
        testFromDataUrlButton.addClickListener(e -> {
            testFromDataUrlButton.setEnabled(true);
            try {
                byte[] data = IOUtils.toByteArray(DemoUI.class.getResourceAsStream("hello-dataurl.png"));
                DataUrl value = (new DataUrlBuilder())
                        .setData(data)
                        .setMimeType("image/png")
                        .setEncoding(DataUrlEncoding.BASE64)
                        .build();
                signatureField.setValue(dataUrlConverter.convertToPresentation(value, null));
            } catch (IOException ex) {
                Notification.show("Error reading test value!", Notification.Type.ERROR_MESSAGE);
                Logger.getLogger(DemoUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        testFromStringButton.addClickListener(e -> {
            testFromStringButton.setEnabled(true);
            try {
                String value = IOUtils.toString(
                        DemoUI.class.getResourceAsStream("hello-string.txt"));
                signatureField.setValue(value);
            } catch (IOException ex) {
                Notification.show("Error reading test value!", Notification.Type.ERROR_MESSAGE);
                Logger.getLogger(DemoUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        testTransparentButton.addClickListener(e -> {
            testTransparentButton.setEnabled(true);
            try {
                byte[] data = IOUtils.toByteArray(DemoUI.class.getResourceAsStream("transparent.png"));
                DataUrl value = (new DataUrlBuilder())
                        .setData(data)
                        .setMimeType("image/png")
                        .setEncoding(DataUrlEncoding.BASE64)
                        .build();
                signatureField.setValue(dataUrlConverter.convertToPresentation(value, null));
            } catch (IOException ex) {
                Notification.show("Error reading test value!", Notification.Type.ERROR_MESSAGE);
                Logger.getLogger(DemoUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        resultsAccordion.setSelectedTab(0);
        
        binder.setBean(signatureField);
    }
    
    private void updateResources() {
        try {
            String signatureString = signatureField.getValue();
            boolean empty = signatureField.isEmpty();
            
            stringPreviewImage.setSource(!empty ?
                    new ExternalResource(signatureString) : null);
            emptyLabel.setValue(String.valueOf(empty));
            saveButton.setEnabled(!empty);
            
            final DataUrl signature = dataUrlConverter
                    .convertToModel(signatureString, null)
                    .getOrThrow(ex -> new RuntimeException(ex));

            dataUrlPreviewImage.setSource(!empty
                    ? new ExternalResource(serializer.serialize(signature)) : null);
            StreamResource streamResource = null;
            if (!empty) {
                StreamSource streamSource = new StreamSource() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public InputStream getStream() {
                        return new ByteArrayInputStream(signature.getData());
                    }
                };
                MimeType mimeType = MimeType.valueOfMimeType(signature.getMimeType());
                String extension = null;

                switch (mimeType) {
                    case JPEG: extension = "jpg"; break;
                    case PNG: extension = "png"; break;
                }

                streamResource = new StreamResource(streamSource,
                        "signature." + counter + "." + extension);
                streamResource.setMIMEType(signature.getMimeType());
                streamResource.setCacheTime(0);
            }
            saveOpener.setResource(streamResource);
            binaryPreviewImage.setSource(streamResource);
        } catch (MalformedURLException ex) {
            Logger.getLogger(DemoUI.class.getName()).log(
                    Level.SEVERE, ex.getLocalizedMessage(), ex);
        } finally {
            counter++;
        }
    }

}
