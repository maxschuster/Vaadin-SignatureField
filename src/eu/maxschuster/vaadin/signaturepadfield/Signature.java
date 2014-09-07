package eu.maxschuster.vaadin.signaturepadfield;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import eu.maxschuster.vaadin.signaturepadfield.shared.MimeType;

public class Signature {
	
	private final MimeType mimeType;
	
	private final byte[] data;
	
	public Signature(String dataURL) {
		super();
		mimeType = findMimeType(dataURL);
		data = findData(dataURL);
	}
	
	protected byte[] findData(String dataURL) throws RuntimeException {
		int commaIndex = dataURL.indexOf(',');
		if (commaIndex == -1) {
			throw new RuntimeException("data not found!");
		}
		String base64String = dataURL.substring(commaIndex);
		return decodeBase64(base64String);
	}
	
	protected MimeType findMimeType(String dataURL) {
		int colonIndex = dataURL.indexOf(':');
		int semicolonIndex = dataURL.indexOf(';');
		String mimeTypeString = null;
		if (colonIndex != 4 || semicolonIndex < 6) {
			mimeTypeString = null;
		} else {
			mimeTypeString = dataURL.substring(colonIndex + 1, semicolonIndex);
		}
		return MimeType.valueOfMimeType(mimeTypeString);
	}
	
	protected byte[] decodeBase64(String string) throws IllegalArgumentException {
		return DatatypeConverter.parseBase64Binary(string);
	}
	
	protected String encodeBase64(byte[] data) throws IllegalArgumentException {
		return DatatypeConverter.printBase64Binary(data);
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public byte[] getData() {
		return data;
	}

	public String toDataURL() {
		return "data:" + mimeType.getMimeType() + ";base64," + encodeBase64(data);
	}
	
	public InputStream toInputStream() {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(getData());
		return inputStream;
	}

	@Override
	public String toString() {
		return toDataURL();
	}
	
	
	
}
