package eu.maxschuster.vaadin.signaturepadfield.shared;

import com.vaadin.shared.AbstractFieldState;

public class SignaturePadFieldState extends AbstractFieldState {

	/**
	 * (float or function) Radius of a single dot.
	 */
	public Float dotSize;
	
	/**
	 * (float) Minimum width of a line. Defaults to 0.5.	
	 */
	public float minWidth = .5f;
	
	/**
	 * (float) Maximum width of a line. Defaults to 2.5.
	 */
	public float maxWidth = 2.5f;
	
	/**
	 * (string) Color used to clear the background. Can be any
	 * color format accepted by context.fillStyle. Defaults to
	 * "rgba(0,0,0,0)" (transparent black). Use a non-transparent
	 * color e.g. "rgb(255,255,255)" (opaque white) if you'd
	 * like to save signatures as JPEG images.
	 */
	public String backgroundColor = "rgba(0,0,0,0)";
	
	/**
	 * (string) Color used to draw the lines. Can be any color
	 * format accepted by context.fillStyle. Defaults to "black".
	 */
	public String penColor = "black";
	
	/**
	 * (float) Weight used to modify new velocity based on the
	 * previous velocity. Defaults to 0.7.
	 */
	public float velocityFilterWeight = .7f;
	
	/**
	 * MIME-Type used to create dataURLs
	 */
	public MimeType mimeType = MimeType.PNG;
	
}
