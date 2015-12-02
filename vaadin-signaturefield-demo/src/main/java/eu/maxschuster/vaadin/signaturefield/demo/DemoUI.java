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
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlBuilder;
import eu.maxschuster.dataurl.DataUrlEncoding;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import eu.maxschuster.vaadin.colorpickerfield.converter.ColorToRgbaConverter;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataUrlConverter;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

@Theme("demo")
@PreserveOnRefresh
@Push(PushMode.DISABLED)
public class DemoUI extends UI {
            
    private static final long serialVersionUID = 1L;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(
            productionMode = true,
            ui = DemoUI.class,
            widgetset = "eu.maxschuster.vaadin.signaturefield.demo.DemoWidgetSet"
    )
    public static class Servlet extends VaadinServlet {
        
        private static final long serialVersionUID = 1L;
            
    }

    private final ObjectProperty<DataUrl> dataUrlProperty
            = new ObjectProperty<DataUrl>(null, DataUrl.class);
    
    private final BeanItemContainer<MimeType> mimeTypeContainer
            = new BeanItemContainer<MimeType>(MimeType.class,
                    Arrays.asList(new MimeType[]{
                        MimeType.PNG,
                        MimeType.JPEG
                    }));
    
    private final DemoUILayout l = new DemoUILayout();

    private IDataUrlSerializer serializer;

    @Override
    protected void init(VaadinRequest request) {
        setContent(l);
        
        // Set href and target manually
        // ToDo: Move back to declarative layout when vaadin bug is fixed
        l.signaturePadLink.setResource(new ExternalResource(
                "https://github.com/szimek/signature_pad"));
        l.signaturePadLink.setTargetName("_blank");
        l.signatureFieldLink.setResource(new ExternalResource(
                "https://github.com/maxschuster/Vaadin-SignatureField"));
        l.signatureFieldLink.setTargetName("_blank");
        
        getPage().setTitle(l.pageTitleLabel.getValue());
        
        l.signatureField.setBackgroundColor("rgba(255,255,255, 1)");
        l.signatureField.setPenColor("rgba(18, 10, 143, 1)");
        l.signatureField.setPropertyDataSource(dataUrlProperty);
        l.signatureField.setConverter(new StringToDataUrlConverter());
        
        l.clearButton.addClickListener(new ClickListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void buttonClick(ClickEvent event) {
                l.signatureField.clear();
            }
        });
        
        final BrowserWindowOpener saveOpener = new BrowserWindowOpener("");
        saveOpener.setWindowName("_blank");
        saveOpener.extend(l.saveButton);
        
        l.mimeTypeComboBox.setContainerDataSource(mimeTypeContainer);

        //l.mimeTypeComboBox.setItemCaptionPropertyId("mimeType");
        //l.mimeTypeComboBox.setNullSelectionAllowed(false);
        l.mimeTypeComboBox.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                MimeType mimeType = (MimeType) event.getProperty().getValue();
                l.signatureField.setMimeType(mimeType);
            }
        });
        l.mimeTypeComboBox.setValue(l.signatureField.getMimeType());
        
        l.dotSizeTextField.setConverter(Double.class);
        l.dotSizeTextField.setNullSettingAllowed(true);
        l.dotSizeTextField.setConvertedValue(l.signatureField.getDotSize());
        l.dotSizeTextField.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                l.signatureField.setDotSize((Double) l.dotSizeTextField.getConvertedValue());
            }
        });
        
        l.minWidthTextField.setConverter(Double.class);
        l.minWidthTextField.setConvertedValue(l.signatureField.getMinWidth());
        l.minWidthTextField.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                l.signatureField.setMinWidth((Double) l.minWidthTextField.getConvertedValue());
            }
        });
        
        l.maxWidthTextField.setConverter(Double.class);
        l.maxWidthTextField.setConvertedValue(l.signatureField.getMaxWidth());
        l.maxWidthTextField.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                l.signatureField.setMaxWidth((Double) l.maxWidthTextField.getConvertedValue());
            }
        });
        
        l.velocityFilterWeightTextField.setConverter(Double.class);
        l.velocityFilterWeightTextField.setConvertedValue(l.signatureField.getVelocityFilterWeight());
        l.velocityFilterWeightTextField.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                l.signatureField.setVelocityFilterWeight((Double) l.velocityFilterWeightTextField.getConvertedValue());
            }
        });
        
        l.backgroundColorColorPicker.setConverter(new ColorToRgbaConverter());
        l.backgroundColorColorPicker.setConvertedValue(l.signatureField.getBackgroundColor());
        l.backgroundColorColorPicker.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                l.signatureField.setBackgroundColor((String)l.backgroundColorColorPicker.getConvertedValue());
                Notification.show(
                        "The background color will change after\n"
                                + "you have cleared the signature field!",
                        Notification.Type.WARNING_MESSAGE
                );
            }
        });
        
        l.penColorColorPicker.setConverter(new ColorToRgbaConverter());
        l.penColorColorPicker.setConvertedValue(l.signatureField.getPenColor());
        l.penColorColorPicker.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                l.signatureField.setPenColor((String)l.penColorColorPicker.getConvertedValue());
            }
        });

        l.immediateCheckBox.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean immediate = (Boolean) event.getProperty().getValue();
                l.signatureField.setImmediate(immediate);
            }
        });

        l.readOnlyCheckBox.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean readOnly = (Boolean) event.getProperty().getValue();
                l.signatureField.setReadOnly(readOnly);
                l.mimeTypeComboBox.setReadOnly(readOnly);
                l.clearButton.setEnabled(!readOnly);
            }
        });

        l.requiredCheckBox.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean required = (Boolean) event.getProperty().getValue();
                l.signatureField.setRequired(required);
            }
        });

        l.clearButtonEnabledCheckBox.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean clearButtonEnabled = (Boolean) event.getProperty().getValue();
                l.signatureField.setClearButtonEnabled(clearButtonEnabled);
            }
        });
        
        l.dataUrlAsText.setPropertyDataSource(l.signatureField);

        l.emptyLabel.setValue(String.valueOf(l.signatureField.isEmpty()));

        l.signatureField.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                String signature = (String) event.getProperty().getValue();
                l.stringPreviewImage.setSource(signature != null ? new ExternalResource(signature) : null);
                l.emptyLabel.setValue(String.valueOf(l.signatureField.isEmpty()));
            }
        });
        dataUrlProperty.addValueChangeListener(new ValueChangeListener() {
            
            private static final long serialVersionUID = 1L;
            
            private int counter = 0;

            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    final DataUrl signature = (DataUrl) event.getProperty().getValue();
                    l.dataUrlPreviewImage.setSource(signature != null
                            ? new ExternalResource(getSerializer().serialize(signature)) : null);
                    StreamResource streamResource = null;
                    if (signature != null) {
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
                    l.binaryPreviewImage.setSource(streamResource);
                } catch (MalformedURLException e) {
                    Logger.getLogger(DemoUI.class.getName()).log(
                            Level.SEVERE, e.getLocalizedMessage(), e);
                } finally {
                    counter++;
                }
            }
        });
        
        l.testFromDataUrlButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(ClickEvent event) {
                l.testFromDataUrlButton.setEnabled(true);
                try {
                    byte[] data = IOUtils.toByteArray(DemoUI.class.getResourceAsStream("hello-dataurl.png"));
                    DataUrl value = (new DataUrlBuilder())
                            .setData(data)
                            .setMimeType("image/png")
                            .setEncoding(DataUrlEncoding.BASE64)
                            .build();
                    dataUrlProperty.setValue(value);
                } catch (IOException ex) {
                    Notification.show("Error reading test value!", Notification.Type.ERROR_MESSAGE);
                    Logger.getLogger(DemoUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        l.testFromStringButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;
            @Override
            public void buttonClick(ClickEvent event) {
                l.testFromStringButton.setEnabled(true);
                try {
                    String value = IOUtils.toString(
                            DemoUI.class.getResourceAsStream("hello-string.txt"));
                    l.signatureField.setValue(value);
                } catch (IOException ex) {
                    Notification.show("Error reading test value!", Notification.Type.ERROR_MESSAGE);
                    Logger.getLogger(DemoUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        l.resultsAccordion.setSelectedTab(0);
    }
    
    public IDataUrlSerializer getSerializer() {
        if (serializer == null) {
            serializer = new DataUrlSerializer();
        }
        return serializer;
    }

}
