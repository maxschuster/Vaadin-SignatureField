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
package eu.maxschuster.vaadin.signaturefield.client.signaturepad;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Gets fired when the user stroke begins
 * 
 * @author Max Schuster
 */
public class StrokeBeginEvent extends GwtEvent<StrokeBeginEventHandler> {
    
    public static final Type<StrokeBeginEventHandler> TYPE =
            new Type<StrokeBeginEventHandler>();
    
    private final SignaturePad signaturePad;

    public StrokeBeginEvent(SignaturePad signaturePad) {
        this.signaturePad = signaturePad;
    }

    @Override
    public Type<StrokeBeginEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StrokeBeginEventHandler handler) {
        handler.onStrokeBegin(this);
    }

    public SignaturePad getSignaturePad() {
        return signaturePad;
    }

}
