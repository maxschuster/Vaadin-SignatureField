/*
 * Copyright 2014 Max.
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
package eu.maxschuster.vaadin.signaturefield.client.signaturepad;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Gets fired when the user stroke ends
 * 
 * @author Max Schuster
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
