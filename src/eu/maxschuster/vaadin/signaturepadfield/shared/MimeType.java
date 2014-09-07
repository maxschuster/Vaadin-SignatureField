package eu.maxschuster.vaadin.signaturepadfield.shared;

public enum MimeType {

	PNG("image/png"),
	JPEG("image/jpeg");
	
	private final String mimeType;
	
	MimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}
	
	public static MimeType valueOfMimeType(String mimeType) {
		if (mimeType == null) {
			throw new NullPointerException("mimeType must not be null!");
		}
		for (MimeType mime : values()) {
			if (mime.getMimeType().equals(mimeType)) {
				return mime;
			}
		}
		throw new IllegalArgumentException("MIME-Type " + mimeType + " not found!");
	}
	
}
