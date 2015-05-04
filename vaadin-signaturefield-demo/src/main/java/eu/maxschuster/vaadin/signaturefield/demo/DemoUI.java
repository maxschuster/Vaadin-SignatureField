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
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import eu.maxschuster.vaadin.signaturefield.Color;
import eu.maxschuster.vaadin.signaturefield.SignatureField;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataUrlConverter;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.util.Arrays;

@Theme("demo")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(
            productionMode = false,
            ui = DemoUI.class,
            widgetset = "eu.maxschuster.vaadin.signaturefield.demo.DemoWidgetSet"
    )
    public static class Servlet extends VaadinServlet {
        
    }
    
    private static final String HEIGHT = "318px";
    private static final String WIDTH = "556px";
    
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

    private final IDataUrlSerializer serializer = new DataUrlSerializer();
    
    private final String pageTitle = "SignatureField";

    @Override
    protected void init(VaadinRequest request) {
        
        getPage().setTitle(pageTitle);
        
        final VerticalLayout margin = new VerticalLayout();
        margin.setWidth("100%");
        setContent(margin);

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        margin.addComponent(layout);
        margin.setComponentAlignment(layout, Alignment.TOP_CENTER);
        
        final Label header1 = new Label(pageTitle);
        header1.addStyleName("h1");
        header1.setSizeUndefined();
        layout.addComponent(header1);
        layout.setComponentAlignment(header1, Alignment.TOP_CENTER);
        
        final TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeUndefined();
        layout.addComponent(tabSheet);
        layout.setComponentAlignment(tabSheet, Alignment.TOP_CENTER);

        final Panel signaturePanel = new Panel();
        signaturePanel.addStyleName("signature-panel");
        tabSheet.addTab(signaturePanel, "Demo");
        
        final VerticalLayout signatureLayout = new VerticalLayout();
        signatureLayout.setMargin(true);
        signatureLayout.setSpacing(true);
        signatureLayout.setSizeUndefined();
        signaturePanel.setContent(signatureLayout);
        
        final SignatureField signatureField = new SignatureField();
        signatureField.setWidth(WIDTH);
        signatureField.setHeight(HEIGHT);
        signatureField.setPenColor(Color.ULTRAMARINE);
        signatureField.setBackgroundColor("white");
        signatureField.setConverter(new StringToDataUrlConverter());
        signatureField.setPropertyDataSource(dataUrlProperty);
        signatureField.setVelocityFilterWeight(0.7);
        signatureLayout.addComponent(signatureField);
        signatureLayout.setComponentAlignment(
                signatureField, Alignment.MIDDLE_CENTER);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setWidth(WIDTH);
        signatureLayout.addComponent(buttonLayout);

        final Button clearButton = new Button("Clear", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                signatureField.clear();
            }
        });
        buttonLayout.addComponent(clearButton);
        buttonLayout.setComponentAlignment(clearButton, Alignment.MIDDLE_LEFT);
        
        final Label message = new Label("Sign above");
        message.setSizeUndefined();
        buttonLayout.addComponent(message);
        buttonLayout.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
        
        final Button saveButton = new Button("Save");
        buttonLayout.addComponent(saveButton);
        buttonLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
        
        final BrowserWindowOpener saveOpener = new BrowserWindowOpener("");
        saveOpener.setWindowName("_blank");
        saveOpener.extend(saveButton);
        
        final Panel optionsPanel = new Panel();
        optionsPanel.setSizeFull();
        tabSheet.addTab(optionsPanel, "Options");

        final FormLayout optionsLayout = new FormLayout();
        optionsLayout.setMargin(true);
        optionsLayout.setSpacing(true);
        optionsPanel.setContent(optionsLayout);

        final ComboBox mimeTypeComboBox = new ComboBox(null, mimeTypeContainer);
        optionsLayout.addComponent(mimeTypeComboBox);
        mimeTypeComboBox.setItemCaptionPropertyId("mimeType");
        mimeTypeComboBox.setNullSelectionAllowed(false);
        mimeTypeComboBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                MimeType mimeType = (MimeType) event.getProperty().getValue();
                signatureField.setMimeType(mimeType);
            }
        });
        mimeTypeComboBox.setValue(MimeType.PNG);
        mimeTypeComboBox.setCaption("Result MIME-Type");

        final CheckBox immediateCheckBox = new CheckBox("immediate", false);
        optionsLayout.addComponent(immediateCheckBox);
        immediateCheckBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean immediate = (Boolean) event.getProperty().getValue();
                signatureField.setImmediate(immediate);
            }
        });

        final CheckBox readOnlyCheckBox = new CheckBox("readOnly", false);
        optionsLayout.addComponent(readOnlyCheckBox);
        readOnlyCheckBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean readOnly = (Boolean) event.getProperty().getValue();
                signatureField.setReadOnly(readOnly);
                mimeTypeComboBox.setReadOnly(readOnly);
                clearButton.setEnabled(!readOnly);
            }
        });

        final CheckBox requiredCheckBox = new CheckBox(
                "required", false);
        optionsLayout.addComponent(requiredCheckBox);
        requiredCheckBox.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean required = (Boolean) event.getProperty().getValue();
                signatureField.setRequired(required);
            }
        });

        final CheckBox clearButtonEnabledButton = new CheckBox("clearButtonEnabled", false);
        optionsLayout.addComponent(clearButtonEnabledButton);
        clearButtonEnabledButton.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean clearButtonEnabled = (Boolean) event.getProperty().getValue();
                signatureField.setClearButtonEnabled(clearButtonEnabled);
            }
        });
        
        final VerticalLayout resultsLayout = new VerticalLayout();
        resultsLayout.setSizeUndefined();
        signatureLayout.addComponent(resultsLayout);
        
        final Accordion results = new Accordion();
        results.setCaption("Results:");
        results.setSizeUndefined();
        resultsLayout.addComponent(results);

        final Image stringPreviewImage = new Image();
        stringPreviewImage.setWidth(WIDTH);
        stringPreviewImage.setHeight(HEIGHT);
        results.addComponent(stringPreviewImage);
        results.addTab(stringPreviewImage, "String Image");

        final Image dataUrlPreviewImage = new Image();
        dataUrlPreviewImage.setWidth(WIDTH);
        dataUrlPreviewImage.setHeight(HEIGHT);
        results.addTab(dataUrlPreviewImage, "DataURL Image");
        
        final Image binaryPreviewImage = new Image();
        binaryPreviewImage.setWidth(WIDTH);
        binaryPreviewImage.setHeight(HEIGHT);
        results.addTab(binaryPreviewImage, "StreamResource Image");

        final TextArea dataUrlAsText = new TextArea();
        dataUrlAsText.setPropertyDataSource(signatureField);
        dataUrlAsText.setSizeFull();
        
        CustomComponent dataUrlLabelWrapper = new CustomComponent(dataUrlAsText);
        dataUrlLabelWrapper.setWidth(WIDTH);
        dataUrlLabelWrapper.setHeight(HEIGHT);
        
        results.addTab(dataUrlLabelWrapper, "DataURL String");

        final Label emptyLabel = new Label();
        emptyLabel.setCaption("Is Empty:");
        emptyLabel.setValue(String.valueOf(signatureField.isEmpty()));
        signatureLayout.addComponent(emptyLabel);

        signatureField.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                String signature = (String) event.getProperty().getValue();
                stringPreviewImage.setSource(signature != null ? new ExternalResource(signature) : null);
                emptyLabel.setValue(String.valueOf(signatureField.isEmpty()));
            }
        });
        dataUrlProperty.addValueChangeListener(new ValueChangeListener() {
            
            private int counter = 0;

            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    final DataUrl signature = (DataUrl) event.getProperty().getValue();
                    dataUrlPreviewImage.setSource( signature != null
                            ? new ExternalResource(serializer.serialize(signature)) : null);
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
                    binaryPreviewImage.setSource(streamResource);
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    counter++;
                }
            }
        });
        
        results.setSelectedTab(0);
        
        final Label links = new Label("Links");
        links.addStyleName("h1");
        links.setSizeUndefined();
        layout.addComponent(links);
        layout.setComponentAlignment(links, Alignment.TOP_CENTER);
        
        final Link signaturePadLink = new Link(
                "https://github.com/szimek/signature_pad", 
                new ExternalResource("https://github.com/szimek/signature_pad"));
        signaturePadLink.setTargetName("_blank");
        layout.addComponent(signaturePadLink);
        layout.setComponentAlignment(signaturePadLink, Alignment.TOP_CENTER);
        
        final Link signatureFieldLink = new Link(
                "https://github.com/maxschuster/Vaadin-SignatureField", 
                new ExternalResource(
                        "https://github.com/maxschuster/Vaadin-SignatureField"));
        signatureFieldLink.setTargetName("_blank");
        layout.addComponent(signatureFieldLink);
        layout.setComponentAlignment(signatureFieldLink, Alignment.TOP_CENTER);
    }

}
