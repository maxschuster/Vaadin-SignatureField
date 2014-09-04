package eu.maxschuster.vaadin.signaturepad;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({
	"js/signature_pad/signature_pad.min.js",
	"js/vaadin.SignaturePad.js"
})
@StyleSheet({
	"css/vaadin.SignaturePad.css"
})
public class SignaturePad extends AbstractJavaScriptComponent {

}
