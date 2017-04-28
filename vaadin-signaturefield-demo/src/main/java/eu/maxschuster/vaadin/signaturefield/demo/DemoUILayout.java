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
package eu.maxschuster.vaadin.signaturefield.demo;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Image;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import eu.maxschuster.vaadin.signaturefield.SignatureField;

/**
 *
 * @author Max Schuster
 */
@DesignRoot
public class DemoUILayout extends VerticalLayout {
    
    public Label pageTitleLabel;
    public SignatureField signatureField;
    public Button clearButton;
    public Button saveButton;
    public ComboBox mimeTypeComboBox;
    public TextField dotSizeTextField;
    public TextField minWidthTextField;
    public TextField maxWidthTextField;
    public TextField velocityFilterWeightTextField;
    public ColorPicker backgroundColorColorPicker;
    public ColorPicker penColorColorPicker;
    public CheckBox immediateCheckBox;
    public CheckBox readOnlyCheckBox;
    public CheckBox requiredCheckBox;
    public CheckBox clearButtonEnabledCheckBox;
    public Label emptyLabel;
    public Image stringPreviewImage;
    public Image dataUrlPreviewImage;
    public Image binaryPreviewImage;
    public TextArea dataUrlAsText;
    public Accordion resultsAccordion;
    public Link signaturePadLink;
    public Link signatureFieldLink;
    public Button testFromStringButton;
    public Button testFromDataUrlButton;
    public Button testTransparentButton;
    
    public DemoUILayout() {
        Design.read(this);
    }
    
}
