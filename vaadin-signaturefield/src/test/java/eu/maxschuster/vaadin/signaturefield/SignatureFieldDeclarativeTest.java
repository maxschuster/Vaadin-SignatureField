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
package eu.maxschuster.vaadin.signaturefield;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinService;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.tests.design.TestDeploymentConfiguration;
import com.vaadin.tests.design.TestVaadinService;
import eu.maxschuster.vaadin.signaturefield.SignatureField;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * TODO: Make tests work this work..-
 * @author Max Schuster
 */
@SuppressWarnings("unchecked")
public class SignatureFieldDeclarativeTest extends DeclarativeTestBase<SignatureField> {
    
    private String getCompleteDesign() {
        return "<!DOCTYPE html>"
            + "<html>\n" +
            "    <head>\n" +
            "        <meta name=\"package-mapping\" content=\"eu_maxschuster_vaadin_signaturefield:eu.maxschuster.vaadin.signaturefield\">\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <eu_maxschuster_vaadin_signaturefield-signature-field "
                        + "pen-color=\"#ffffff\" "
                + "clear-button-enabled=\"true\" "
                        + "background-color=\"#12bdf6\" "
                        + "velocity-filter-weight=\"1\" "
                        + "width=\"123px\" "
                        + "height=\"123px\" "
                + ">"
                    + "</eu_maxschuster_vaadin_signaturefield-signature-field>\n" +
            "    </body>\n" +
            "</html>";
    }

    private SignatureField getCompleteExpected() {
        SignatureField sf = new SignatureField();
        
        sf.withBackgroundColor("#12bdf6")
                .withPenColor("#ffffff")
                .withClearButtonEnabled(true)
                .withHeight(123, Sizeable.Unit.PIXELS)
                .withMaxWidth(10)
                .withMimeType(MimeType.JPEG)
                .withMinWidth(10)
                .withPenColor("#ffffff")
                .withReadOnly(true)
                .withVelocityFilterWeight(1)
                .withWidth(123, Sizeable.Unit.PIXELS);
        
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(SignatureField.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException(
                    "Could not get supported attributes for class "
                            + SignatureField.class.getName());
        }
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            String getter = descriptor.getReadMethod() != null ? descriptor.getReadMethod().getName() : "null";
            String setter = descriptor.getWriteMethod() != null ? descriptor.getWriteMethod().getName() : "null";
            System.out.println(descriptor.getName() + " " + getter + " " + setter);
        }
        
        System.out.println(write(sf));
        
        return sf;
    }
    
    private SignatureField getEmptyExpected() {
        SignatureField sf = new SignatureField();
        return sf;
    }
    
    private String getEmptyDesign() {
        return "<!DOCTYPE html>"
            + "<html>\n" +
            "    <head>\n" +
            "        <meta name=\"package-mapping\" content=\"eu_maxschuster_vaadin_signaturefield:eu.maxschuster.vaadin.signaturefield\">\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <eu_maxschuster_vaadin_signaturefield-signature-field>"
                    + "</eu_maxschuster_vaadin_signaturefield-signature-field>\n" +
            "    </body>\n" +
            "</html>";
    }
    
    @BeforeClass
    public static void beforeClass() {
        DeploymentConfiguration conf = new TestDeploymentConfiguration();
        VaadinService service = new TestVaadinService(conf);
        VaadinService.setCurrent(service);
    }
    
    @AfterClass
    public static void afterClass() {
        VaadinService.setCurrent(null);
    }
    
    //@Test
    public void testEmpty() {
        String design = getEmptyDesign();
        SignatureField sf = getEmptyExpected();
        testRead(design, sf);
        testWrite(design, sf);
    }
    
    //@Test
    public void testComplete() {
        String design = getCompleteDesign();
        SignatureField sf = getCompleteExpected();
        testRead(design, sf);
        testWrite(design, sf);
    }
    
}
