SignatureField
==============

A Vaadin Field (AbstractField&lt;String&gt;) to capture signatures.
Its value is the data url from the html canvas as simple String.
It is based on a GWT port of Szymon Nowak's [signature_pad](https://github.com/szimek/signature_pad).

## Demo
[http://maxschuster.jelastic.servint.net/SignatureField/](http://maxschuster.jelastic.servint.net/SignatureField/)

## Usage
### Simple
``` java
FormLayout layout = new FormLayout();

SignatureField signatureField = new SignatureField();
signatureField.setWidth("350px");
signatureField.setHeight("150px");

layout.addComponent(signatureField);

signatureField.addValueChangeListener(new ValueChangeListener() {
	@Override
	public void valueChange(ValueChangeEvent event) {
		String signature = (String) event.getProperty().getValue();
		// do something with the string
	}
});
```

### Using DataURL
``` java

ObjectProperty<DataURL> dataUrlProperty =
	new ObjectProperty<DataURL>(null, DataURL.class);

FormLayout layout = new FormLayout();

SignatureField signatureField = new SignatureField();
signatureField.setWidth("350px");
signatureField.setHeight("150px");
signatureField.setConverter(new StringToDataURLConverter());
signatureField.setPropertyDataSource(dataUrlProperty);

layout.addComponent(signatureField);

dataUrlProperty.addValueChangeListener(new ValueChangeListener() {
	@Override
	public void valueChange(ValueChangeEvent event) {
		final DataURL signature = (DataURL) event.getProperty().getValue();
		String mimeType = signature.getAppliedMimeType();
		byte[] data = signature.getData();
		// do something with the data
	}	
});
```

## Licence
* SignatureField Add-on: Apache License Version 2.0
* [signature_pad](https://github.com/szimek/signature_pad): MIT License