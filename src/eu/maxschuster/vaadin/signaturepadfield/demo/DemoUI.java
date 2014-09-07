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

package eu.maxschuster.vaadin.signaturepadfield.demo;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.maxschuster.vaadin.signaturepadfield.Signature;
import eu.maxschuster.vaadin.signaturepadfield.SignaturePadField;
import eu.maxschuster.vaadin.signaturepadfield.shared.MimeType;

@SuppressWarnings("serial")
@Theme("signaturepad")
public class DemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(
			productionMode = false,
			ui = DemoUI.class,
			widgetset = "eu.maxschuster.vaadin.signaturepadfield.SignaturePadFieldWidgetset"
	)
	public static class Servlet extends VaadinServlet {}

	@Override
	protected void init(VaadinRequest request) {
		
		final MarginInfo marginTopBottom = new MarginInfo(5);
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		
		final BeanItemContainer<MimeType> mimeTypeContainer =
				new BeanItemContainer<>(MimeType.class);
		mimeTypeContainer.addBean(MimeType.PNG);
		mimeTypeContainer.addBean(MimeType.JPEG);
		
		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		layout.addComponent(buttonLayout);
		
		final SignaturePadField signaturePadField = new SignaturePadField();
		layout.addComponent(signaturePadField);
		signaturePadField.setBackgroundColor(SignaturePadField.COLOR_WHITE);
		signaturePadField.setPenColor(SignaturePadField.COLOR_ULTRAMARIN);
		signaturePadField.setWidth("350px");
		
		final Button clearButton = new Button("Clear", new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				signaturePadField.clear();
			}
		});
		buttonLayout.addComponent(clearButton);
		
		final Panel resultPanel = new Panel("Results:");
		resultPanel.setSizeUndefined();
		layout.addComponent(resultPanel);
		
		final FormLayout resultLayout = new FormLayout();
		resultLayout.setMargin(marginTopBottom);
		resultPanel.setContent(resultLayout);
		
		final Image previewImage = new Image("Preview Image:");
		resultLayout.addComponent(previewImage);
		
		final Label emptyLabel = new Label();
		emptyLabel.setCaption("Is Empty:");
		emptyLabel.setValue(String.valueOf(signaturePadField.isEmpty()));
		resultLayout.addComponent(emptyLabel);
		
		signaturePadField.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				Signature signature = (Signature) event.getProperty().getValue();
				previewImage.setSource(signature != null ? new ExternalResource(signature.toDataURL()) : null);
				emptyLabel.setValue(String.valueOf(signaturePadField.isEmpty()));
			}
		});
		
		final Panel optionsPanel = new Panel("Options:");
		optionsPanel.setSizeUndefined();
		layout.addComponent(optionsPanel);
		
		final FormLayout optionsLayout = new FormLayout();
		optionsPanel.setContent(optionsLayout);
		
		final ComboBox mimeTypeComboBox = new ComboBox(null, mimeTypeContainer);
		optionsLayout.addComponent(mimeTypeComboBox);
		mimeTypeComboBox.setItemCaptionPropertyId("mimeType");
		mimeTypeComboBox.setNullSelectionAllowed(false);
		mimeTypeComboBox.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				MimeType mimeType = (MimeType) event.getProperty().getValue();
				signaturePadField.setMimeType(mimeType);
			}
		});
		mimeTypeComboBox.setValue(MimeType.PNG);
		mimeTypeComboBox.setCaption("Result MIME-Type");
		
		final CheckBox immediateCheckBox = new CheckBox("immediate", false);
		optionsLayout.addComponent(immediateCheckBox);
		immediateCheckBox.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean immediate = (boolean) event.getProperty().getValue();
				signaturePadField.setImmediate(immediate);
			}
		});
		
		final CheckBox readOnlyCheckBox = new CheckBox("readOnly", false);
		optionsLayout.addComponent(readOnlyCheckBox);
		readOnlyCheckBox.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean readOnly = (boolean) event.getProperty().getValue();
				signaturePadField.setReadOnly(readOnly);
			}
		});
		
		final CheckBox requiredCheckBox = new CheckBox("required", false);
		optionsLayout.addComponent(requiredCheckBox);
		requiredCheckBox.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean required = (boolean) event.getProperty().getValue();
				signaturePadField.setRequired(required);
			}
		});
		
	}

}