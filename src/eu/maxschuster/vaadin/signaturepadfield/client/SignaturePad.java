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

package eu.maxschuster.vaadin.signaturepadfield.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;

public class SignaturePad extends JavaScriptObject {
	
	/**
	 * Callback when stroke begin.
	 * @author Max
	 */
	public interface BeginHandler {
		
		/**
		 * Callback when stroke begin.
		 * @param signaturePad
		 */
		public void onBegin(SignaturePad signaturePad);
		
	}
	
	/**
	 * Callback when stroke end.
	 * @author Max
	 */
	public interface EndHandler {
		
		/**
		 * Callback when stroke end.
		 * @param signaturePad
		 */
		public void onEnd(SignaturePad signaturePad);
		
	}
	
	protected SignaturePad() {}
	
	protected static final native SignaturePad create(CanvasElement canvas) /*-{
		return new $wnd.SignaturePad(canvas);
	}-*/;
	
	public static final SignaturePad create(Canvas canvas) {
		return create(canvas.getCanvasElement());
	}
	
	protected static final native SignaturePad create(CanvasElement canvas, 
			float dotSize, float minWidth, float maxWidth,
			String backgroundColor, String penColor,
			float velocityFilterWeight) /*-{
		return new $wnd.SignaturePad(canvas, {
			dotSize: dotSize,
			minWidth: minWidth,
			maxWidth: maxWidth,
			backgroundColor: backgroundColor,
			penColor: penColor,
			velocityFilterWeight: velocityFilterWeight
		});
	}-*/;
	
	public static final SignaturePad create(Canvas canvas, 
			float dotSize, float minWidth, float maxWidth,
			String backgroundColor, String penColor,
			float velocityFilterWeight) {
		return create(canvas.getCanvasElement(),
				dotSize,
				minWidth,
				maxWidth,
				backgroundColor,
				penColor,
				velocityFilterWeight
			);
	}
	
	/* METHODS */
	
	/**
	 * Returns signature image as data URL
	 * @return Signature image as data URL
	 */
	public final native String toDataURL() /*-{
		return this.toDataURL();
	}-*/;
	
	public final native String toDataURL(String mimeType) /*-{
		return this.toDataURL(mimeType);
	}-*/;
	
	/**
	 * Draws signature image from data URL
	 * @param dataURL Signature image from data URL
	 */
	public final native void fromDataURL(String dataURL) /*-{
		this.fromDataURL(dataURL);
	}-*/;
	
	/**
	 * Clears the canvas
	 */
	public final native void clear() /*-{
		this.clear();
	}-*/;
	
	/**
	 * Returns true if canvas is empty, otherwise returns false
	 * @return True if canvas is empty, otherwise returns false
	 */
	public final native boolean isEmpty() /*-{
		return this.isEmpty();
	}-*/;
	
	/* GETTER / SETTER */

	/**
	 * Gets the radius of a single dot.
	 * @return Radius of a single dot.
	 */
	public final native Float getDotSize() /*-{
		return this.dotSize;
	}-*/;

	/**
	 * Sets the radius of a single dot.
	 * @param dotSize Radius of a single dot.
	 */
	public final native void setDotSize(Float dotSize) /*-{
		if (typeof dotSize === "number") {
			this.dotSize = dotSize;
		} else {
			var self = this;
			this.dotSize = function() {
				return (self.minWidth + self.maxWidth) / 2;
			}
		}
	}-*/;
	
	/**
	 * Gets the minimum width of a line. Defaults to 0.5.
	 * @return Minimum width of a line
	 */
	public final native float getMinWidth() /*-{
		return this.minWidth;
	}-*/;

	/**
	 * Sets the minimum width of a line. Defaults to 0.5.
	 * @param minWidth Minimum width of a line
	 */
	public final native void setMinWidth(float minWidth) /*-{
		this.minWidth = minWidth;
	}-*/;

	/**
	 * Gets the maximum width of a line. Defaults to 2.5.
	 * @return Maximum width of a line
	 */
	public final native float getMaxWidth() /*-{
		return this.maxWidth;
	}-*/;

	/**
	 * Sets the maximum width of a line. Defaults to 2.5.
	 * @param maxWidth Maximum width of a line
	 */
	public final native void setMaxWidth(float maxWidth) /*-{
		this.maxWidth = maxWidth;
	}-*/;

	/**
	 * Gets the color used to clear the background.
	 * Can be any color format accepted by
	 * context.fillStyle. Defaults to "rgba(0,0,0,0)"
	 * (transparent black). Use a non-transparent color
	 * e.g. "rgb(255,255,255)" (opaque white) if you'd
	 * like to save signatures as JPEG images.
	 * @return Color used to clear the background
	 */
	public final native String getBackgroundColor() /*-{
		return this.backgroundColor;
	}-*/;

	/**
	 * Sets the color used to clear the background.
	 * Can be any color format accepted by
	 * context.fillStyle. Defaults to "rgba(0,0,0,0)"
	 * (transparent black). Use a non-transparent
	 * color e.g. "rgb(255,255,255)" (opaque white)
	 * if you'd like to save signatures as JPEG images.
	 * @param backgroundColor Color used to clear
	 * the background
	 */
	public final native void setBackgroundColor(String backgroundColor) /*-{
		this.backgroundColor = backgroundColor;
	}-*/;

	/**
	 * Gets the color used to draw the lines. Can be
	 * any color format accepted by context.fillStyle.
	 * Defaults to "black".
	 * @return Color used to draw the lines
	 */
	public final native String getPenColor() /*-{
		return this.penColor;
	}-*/;

	/**
	 * Sets the color used to draw the lines. Can be
	 * any color format accepted by context.fillStyle.
	 * Defaults to "black".
	 * @param penColor Color used to draw the lines
	 */
	public final native void setPenColor(String penColor) /*-{
		this.penColor = penColor;
	}-*/;

	/**
	 * Gets the weight used to modify new velocity
	 * based on the previous velocity. Defaults to 0.7.
	 * @return Weight used to modify new velocity
	 * based on the previous velocity
	 */
	public final native float getVelocityFilterWeight() /*-{
		return this.velocityFilterWeight;
	}-*/;

	/**
	 * Sets the weight used to modify new velocity
	 * based on the previous velocity. Defaults to 0.7.
	 * @param velocityFilterWeight Weight used to
	 * modify new velocity based on the previous velocity
	 */
	public final native void setVelocityFilterWeight(float velocityFilterWeight) /*-{
		this.velocityFilterWeight = velocityFilterWeight;
	}-*/;
	
	public final native BeginHandler getBeginHandler() /*-{
		return this._wrappedBeginHandler_;
	}-*/;
	
	/**
	 * Sets the callback when stroke begin
	 * @param onBegin Callback when stroke begin
	 */
	public final native void setBeginHandler(BeginHandler beginHandler) /*-{
		var self = this;
		self._wrappedBeginHandler_ = beginHandler;
		self.onBegin = !beginHandler ? undefined : function() {
			beginHandler.@eu.maxschuster.vaadin.signaturepadfield.client.SignaturePad.BeginHandler::onBegin(Leu/maxschuster/vaadin/signaturepad/client/SignaturePad;)(self);
		};
	}-*/;
	
	public final native EndHandler getEndHandler() /*-{
		return this._wrappedEndHandler_;
	}-*/;

	/**
	 * Sets the callback when stroke end.
	 * @param onEnd Callback when stroke end.
	 */
	public final native void setEndHandler(EndHandler endHandler) /*-{
		var self = this;
		self._wrappedHandler_ = endHandler;
		self.onEnd = !endHandler ? undefined : function() {
			endHandler.@eu.maxschuster.vaadin.signaturepadfield.client.SignaturePad.EndHandler::onEnd(Leu/maxschuster/vaadin/signaturepad/client/SignaturePad;)(self);
		};
	}-*/;

}
