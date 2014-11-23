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
import com.vaadin.server.ExternalResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
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
import eu.maxschuster.dataurl.DefaultDataUrlSerializer;
import eu.maxschuster.vaadin.buttonlink.ButtonLink;

import eu.maxschuster.vaadin.signaturefield.SignatureField;
import eu.maxschuster.vaadin.signaturefield.converter.StringToDataURLConverter;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;

@SuppressWarnings("serial")
@Theme("demo")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(
            productionMode = true,
            ui = DemoUI.class,
            widgetset = "eu.maxschuster.vaadin.signaturefield.demo.DemoWidgetSet"
    )
    public static class Servlet extends VaadinServlet { }

    private final ObjectProperty<DataUrl> dataUrlProperty
            = new ObjectProperty<DataUrl>(null, DataUrl.class);
    
    private final BeanItemContainer<MimeType> mimeTypeContainer
            = new BeanItemContainer<MimeType>(MimeType.class,
                    Arrays.asList(new MimeType[]{
                        MimeType.PNG,
                        MimeType.JPEG
                    }));

    private final DataUrlSerializer serializer = 
            DefaultDataUrlSerializer.get();
    
    private final String pageTitle = "Vaadin-SignatureField";

    private final String VALUE_1;

    private final String VALUE_2;

    private final String VALUE_3;

    public DemoUI(Component content) {
        super(content);
        
        String value1 = null;
        String value2 = null;
        String value3 = null;
        
        try {
            value1 = IOUtils.toString(
                DemoUI.class.getResource("value1.txt"), "UTF-8");
            value2 = IOUtils.toString(
                DemoUI.class.getResource("value2.txt"), "UTF-8");
            value3 = IOUtils.toString(
                DemoUI.class.getResource("value3.txt"), "UTF-8"); 
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        
        VALUE_1 = value1;
        VALUE_2 = value2;
        VALUE_3 = value3;
    }

    public DemoUI() {
        this(null);
    }

    @Override
    protected void init(VaadinRequest request) {
        
        getPage().setTitle(pageTitle);

        final MarginInfo marginTopBottom = new MarginInfo(5);

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setWidth("100%");
        setContent(layout);
        
        final Label header1 = new Label(pageTitle);
        header1.addStyleName("h1");
        header1.setSizeUndefined();
        layout.addComponent(header1);
        layout.setComponentAlignment(header1, Alignment.TOP_CENTER);
        
        final TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth("658px");
        layout.addComponent(tabSheet);
        layout.setComponentAlignment(tabSheet, Alignment.TOP_CENTER);

        final Panel signaturePanel = new Panel();
        signaturePanel.addStyleName("signature-panel");
        signaturePanel.setWidth("100%");
        tabSheet.addTab(signaturePanel, "Demo");
        
        final VerticalLayout signatureLayout = new VerticalLayout();
        signatureLayout.setMargin(true);
        signatureLayout.setSpacing(true);
        signatureLayout.setSizeFull();
        signaturePanel.setContent(signatureLayout);
        
        final SignatureField signatureField = new SignatureField();
        signatureField.setWidth("100%");
        signatureField.setHeight("318px");
        signatureField.setPenColor(SignatureField.COLOR_ULTRAMARIN);
        signatureField.setBackgroundColor("white");
        signatureField.setConverter(new StringToDataURLConverter());
        signatureField.setPropertyDataSource(dataUrlProperty);
        signatureField.setVelocityFilterWeight(0.7);
        signatureLayout.addComponent(signatureField);
        signatureLayout.setComponentAlignment(
                signatureField, Alignment.MIDDLE_CENTER);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setWidth("100%");
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
        
        final ButtonLink saveButtonLink = new ButtonLink("Save", null);
        saveButtonLink.setTargetName("_blank");
        buttonLayout.addComponent(saveButtonLink);
        buttonLayout.setComponentAlignment(
                saveButtonLink, Alignment.MIDDLE_RIGHT);
        
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

        final CheckBox requiredCheckBox = new CheckBox("required", false);
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

        final Panel resultPanel = new Panel("Results:");
        resultPanel.setWidth("100%");
        layout.addComponent(resultPanel);

        final FormLayout resultLayout = new FormLayout();
        resultLayout.setMargin(marginTopBottom);
        resultPanel.setContent(resultLayout);

        final Image stringPreviewImage = new Image("String preview image:");
        stringPreviewImage.setWidth("500px");
        resultLayout.addComponent(stringPreviewImage);

        final Image dataUrlPreviewImage = new Image("DataURL preview image:");
        dataUrlPreviewImage.setWidth("500px");
        resultLayout.addComponent(dataUrlPreviewImage);

        final TextArea textArea = new TextArea("DataURL:");
        textArea.setWidth("100%");
        textArea.setHeight("300px");
        resultLayout.addComponent(textArea);

        final Label emptyLabel = new Label();
        emptyLabel.setCaption("Is Empty:");
        emptyLabel.setValue(String.valueOf(signatureField.isEmpty()));
        resultLayout.addComponent(emptyLabel);

        signatureField.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                String signature = (String) event.getProperty().getValue();
                stringPreviewImage.setSource(signature != null ? new ExternalResource(signature) : null);
                textArea.setValue(signature);
                emptyLabel.setValue(String.valueOf(signatureField.isEmpty()));
            }
        });
        dataUrlProperty.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    final DataUrl signature = (DataUrl) event.getProperty().getValue();
                    dataUrlPreviewImage.setSource(
                            signature != null
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

                        streamResource = new StreamResource(streamSource, "signature." + extension);
                        streamResource.setMIMEType(signature.getMimeType());
                        streamResource.setCacheTime(0);
                    }
                    saveButtonLink.setResource(streamResource);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

}
