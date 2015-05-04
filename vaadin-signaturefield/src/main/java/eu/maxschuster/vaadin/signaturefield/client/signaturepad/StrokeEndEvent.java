/*
 * The MIT License (MIT)
 * 
 * Copyright 2015 Szymon Nowak
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package eu.maxschuster.vaadin.signaturefield.client.signaturepad;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Gets fired when the user stroke ends
 * 
 * @author Szymon Nowak
 */
public class StrokeEndEvent extends GwtEvent<StrokeEndEventHandler> {
    
    public static final Type<StrokeEndEventHandler> TYPE =
            new Type<StrokeEndEventHandler>();
    
    private final SignaturePad signaturePad;

    public StrokeEndEvent(SignaturePad signaturePad) {
        this.signaturePad = signaturePad;
    }

    @Override
    public Type<StrokeEndEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StrokeEndEventHandler handler) {
        handler.onStrokeEnd(this);
    }

    public SignaturePad getSignaturePad() {
        return signaturePad;
    }

}
