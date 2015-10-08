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

import com.vaadin.annotations.Push;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataUrlConverter;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.util.Arrays;

@Theme("demo")
@Push
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(
            productionMode = true,
            ui = DemoUI.class,
            widgetset = "eu.maxschuster.vaadin.signaturefield.demo.DemoWidgetSet"
    )
    public static class Servlet extends VaadinServlet {
        
    }
    
    private static final String HEIGHT = "190px";
    private static final String WIDTH = "340px";
    
    private final Logger logger =
            LoggerFactory.getLogger(DemoUI.class);

    private final ObjectProperty<DataUrl> dataUrlProperty
            = new ObjectProperty<DataUrl>(null, DataUrl.class);
    
    private final BeanItemContainer<MimeType> mimeTypeContainer
            = new BeanItemContainer<MimeType>(MimeType.class,
                    Arrays.asList(new MimeType[]{
                        MimeType.PNG,
                        MimeType.JPEG
                    }));
    
    private final DemoUILayout L = new DemoUILayout();

    private transient IDataUrlSerializer serializer;

    @Override
    protected void init(VaadinRequest request) {
        
        setContent(L);
        
        // Set href and target manually
        // ToDo: Move back to declarative layout when vaadin bug is fixed
        L.signaturePadLink.setResource(new ExternalResource("https://github.com/szimek/signature_pad"));
        L.signaturePadLink.setTargetName("_blank");
        L.signatureFieldLink.setResource(new ExternalResource("https://github.com/maxschuster/Vaadin-SignatureField"));
        L.signatureFieldLink.setTargetName("_blank");
        
        getPage().setTitle(L.pageTitleLabel.getValue());
        
        L.signatureField.setPropertyDataSource(dataUrlProperty);
        L.signatureField.setConverter(new StringToDataUrlConverter());
        
        L.clearButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                L.signatureField.clear();
            }
        });
        
        final BrowserWindowOpener saveOpener = new BrowserWindowOpener("");
        saveOpener.setWindowName("_blank");
        saveOpener.extend(L.saveButton);
        
        L.mimeTypeComboBox.setContainerDataSource(mimeTypeContainer);

        //l.mimeTypeComboBox.setItemCaptionPropertyId("mimeType");
        //l.mimeTypeComboBox.setNullSelectionAllowed(false);
        L.mimeTypeComboBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                MimeType mimeType = (MimeType) event.getProperty().getValue();
                L.signatureField.setMimeType(mimeType);
            }
        });
        L.mimeTypeComboBox.setValue(MimeType.PNG);

        L.immediateCheckBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean immediate = (Boolean) event.getProperty().getValue();
                L.signatureField.setImmediate(immediate);
            }
        });

        L.readOnlyCheckBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean readOnly = (Boolean) event.getProperty().getValue();
                L.signatureField.setReadOnly(readOnly);
                L.mimeTypeComboBox.setReadOnly(readOnly);
                L.clearButton.setEnabled(!readOnly);
            }
        });

        L.requiredCheckBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean required = (Boolean) event.getProperty().getValue();
                L.signatureField.setRequired(required);
            }
        });

        L.clearButtonEnabledCheckBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean clearButtonEnabled = (Boolean) event.getProperty().getValue();
                L.signatureField.setClearButtonEnabled(clearButtonEnabled);
            }
        });
        
        L.dataUrlAsText.setPropertyDataSource(L.signatureField);

        L.emptyLabel.setValue(String.valueOf(L.signatureField.isEmpty()));

        L.signatureField.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                String signature = (String) event.getProperty().getValue();
                L.stringPreviewImage.setSource(signature != null ? new ExternalResource(signature) : null);
                L.emptyLabel.setValue(String.valueOf(L.signatureField.isEmpty()));
            }
        });
        dataUrlProperty.addValueChangeListener(new ValueChangeListener() {
            
            private int counter = 0;

            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    final DataUrl signature = (DataUrl) event.getProperty().getValue();
                    L.dataUrlPreviewImage.setSource(signature != null
                            ? new ExternalResource(getSerializer().serialize(signature)) : null);
                    StreamResource streamResource = null;
                    if (signature != null) {
                        StreamSource streamSource = new StreamSource() {

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
                    L.binaryPreviewImage.setSource(streamResource);
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    counter++;
                }
            }
        });
        
        L.resultsAccordion.setSelectedTab(0);
    }
    
    public IDataUrlSerializer getSerializer() {
        if (serializer == null) {
            serializer = new DataUrlSerializer();
        }
        return serializer;
    }

}
