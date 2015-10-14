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

/**
 * Client-side logic of the SignatureField
 * 
 * @property {Element} parent Parent element
 * @property {Element} canvas Canvas
 * @property {Element} clearButton Button that clears the field
 * @property {string} signature Signature data url
 * @property {string} mimeType MIME-Type that determines the format of the signature image
 * @property {SignaturePad} signaturePad SignaturePad instance
 * @property {boolean} immediate Update the signature on the server-side
 * immediately after each pen stroke
 * @property {boolean} readOnly Field is readonly
 * @author Max Schuster
 * @returns {undefined}
 */
function eu_maxschuster_vaadin_signaturefield_SignatureFieldExtension() {

    "use strict";

    /* jshint validthis:true */
    /* jshint -W087 */ // All 'debugger' statements should be removed

    /**
     * Initializes the extension
     * @returns {undefined}
     */
    this.init = function () {
        var state = this.getState();

        this.parent = this.findAndExtendParent();
        this.canvas = this.createCanvas(this.parent);
        this.clearButton = null;
        this.signature = null;
        this.mimeType = "image/png";
        this.signaturePad = this.createSignaturePad(this.canvas);
        this.immediate = state.immediate;
        this.readOnly = state.readOnly;

        this.addResizeListener(this.parent, this.proxy(this.onResize));
    };

    /**
     * Proxy the given function with a context
     * @param {Function} func Function to proxy
     * @param {Object} [ctx=this] Context to apply to the function. Defaults to
     * "this".
     * @param {Array} [args=undefined] arguments that should be passed to the
     * function. Defaults to "arguments"
     * @returns {Function} The function proxy
     */
    this.proxy = function (func, ctx, args) {
        ctx = ctx || this;
        return function () {
            func.apply(ctx, args || arguments);
        };
    };

    /**
     * Imports the given signature dataURL into the canvas. Called from the
     * server-side to update the signature on the client-side
     * @param {string} newSignature Signture to import
     * @returns {undefined}
     */
    this.setSignature = function (newSignature) {
        var signaturePad = this.signaturePad,
                oldSignature = this.signature;
        if (newSignature === oldSignature) {
            return;
        }
        this.signature = newSignature;
        signaturePad.clear();
        if (newSignature) {
            signaturePad.fromDataURL(newSignature);
        }
    };

    /**
     * Extends and extends the parent component of this extension.
     * @returns {Element} The parent element.
     */
    this.findAndExtendParent = function () {
        var parent = this.getElement(this.getParentId());
        parent.tabIndex = -1;
        this.addEvent(parent, "focus", this.proxy(this.onFocus));
        this.addEvent(parent, "blur", this.proxy(this.onBlur));
        return parent;
    };

    /**
     * Creates the canvas element and attaches it to the given parent element.
     * @param {Element} parent Parent element of the canvas.
     * @returns {unresolved}
     */
    this.createCanvas = function (parent) {
        var canvas = document.createElement("canvas");
        parent.appendChild(canvas);
        return canvas;
    };

    /**
     * Updates the canvas size to match its parent's size, without clearing it.
     * @returns {undefined}
     */
    this.updateCanvasSize = function () {
        var style = this.getComputedStyle(this.parent),
                canvas = this.canvas,
                signature = this.signature,
                signaturePad = this.signaturePad,
                width = parseInt(style.width),
                height = parseInt(style.height);

        canvas.width = width;
        canvas.height = height;

        signaturePad.clear();

        if (signature) {
            signaturePad.fromDataURL(signature);
        }
    };

    /**
     * Creates a signature pad instance with the given canvas element.
     * @param {Element} canvas The canvas to use with the signature pad.
     * @returns {SignaturePad} Signature pad instance.
     */
    this.createSignaturePad = function (canvas) {
        var signaturePad = new SignaturePad(canvas);
        signaturePad.onBegin = this.proxy(this.onStrokeBegin);
        signaturePad.onEnd = this.proxy(this.onStrokeEnd);
        signaturePad.vReadOnly = false;

        // Remove window.devicePixelRatio
        signaturePad.fromDataURL = function (dataUrl) {
            var self = this,
                    image = new Image(),
                    width = this._canvas.width,
                    height = this._canvas.height;

            this._reset();
            image.src = dataUrl;
            image.onload = function () {
                self._ctx.drawImage(image, 0, 0, width, height);
            };
            this._isEmpty = false;
        };

        return signaturePad;
    };

    /**
     * Creates the close button element and adds it to the given parent.
     * @param {Element} parent Parent element to add the close button to.
     * @returns {Element} Close button
     */
    this.createCloseButton = function (parent) {
        var closeButton = document.createElement("button");
        closeButton.setAttribute("class", "signaturefield-clearbutton");
        parent.appendChild(closeButton);
        this.addEvent(closeButton, "click", this.proxy(this.onClearButtonClick));
        return closeButton;
    };

    /**
     * Gets called when the parent element has changed its size.
     * @returns {undefined}
     */
    this.onResize = function () {
        this.updateCanvasSize();
    };

    /**
     * Gets called when a stroke begins.
     * @returns {undefined}
     */
    this.onStrokeBegin = function () {
        this.parent.focus();
    };

    /**
     * Gets called when a stroke ends.
     * @returns {undefined}
     */
    this.onStrokeEnd = function () {
        if (this.immediate) {
            this.updateSignature();
        }
    };

    /**
     * Gets called when the field becomes focused.
     * @returns {undefined}
     */
    this.onFocus = function () {
        this.addClass(this.parent, "v-focus");
    };

    /**
     * Gets called when the field loses focus.
     * @returns {undefined}
     */
    this.onBlur = function () {
        this.updateSignature();
        this.removeClass(this.parent, "v-focus");
    };

    /**
     * Gets called when the clear button is clicked.
     * @returns {undefined}
     */
    this.onClearButtonClick = function () {
        this.clear();
    };
    
    /**
     * Gets called when the shared state of this extension changes. Updates the
     * parts of the extension with the new values from the state.
     * @returns {undefined}
     */
    this.onStateChange = function () {
        var state = this.getState(),
                parent = this.parent,
                signaturePad = this.signaturePad;

        signaturePad.dotSize = state.dotSize;
        signaturePad.minWidth = state.minWidth;
        signaturePad.maxWidth = state.maxWidth;
        signaturePad.backgroundColor = state.backgroundColor;
        signaturePad.penColor = state.penColor;
        signaturePad.velocityFilterWeight = state.velocityFilterWeight;

        this.mimeType = state.mimeType;
        this.immediate = state.immediate;

        var readOnly = state.readOnly,
                vReadOnly = signaturePad.vReadOnly;
        if (readOnly && !vReadOnly) {
            signaturePad.off();
            parent.removeAttribute("tabindex");
        } else if (!readOnly && vReadOnly) {
            signaturePad.on();
            parent.tabIndex = -1;
        }
        signaturePad.vReadOnly = readOnly;

        if ((!readOnly && state.clearButtonEnabled) && !this.clearButton) {
            this.clearButton = this.createCloseButton(parent);
        } else if ((readOnly || !state.clearButtonEnabled) && this.clearButton) {
            var clearButton = this.clearButton;
            parent.removeElement(clearButton);
            this.clearButton = null;
        }
    };

    /**
     * Returns the computed style for the given element
     * @param {Element} el Element to get the styles from
     * @returns {CSSStyleDeclaration}
     */
    this.getComputedStyle = function (el) {
        if (window.getComputedStyle) {
            return window.getComputedStyle(el);
        }
        return el.currentStyle;
    };
    
    /**
     * Gets the current signature as dataURL or null if the signatue pad is
     * empty.
     * @returns {string|null} Signature as dataURL or null.
     */
    this.getCurrentSignature = function () {
        var signaturePad = this.signaturePad;
        return signaturePad.isEmpty() ?
                null : signaturePad.toDataURL(this.mimeType, 1);
    };

    /**
     * Updates the server-side signature value with the given signature.
     * @param {string|null} signature Signature value for the server-side.
     * @returns {undefined}
     */
    this.updateSignature = function (signature) {
        var oldSignature = this.signature,
                newSignature = arguments.length > 0 ?
                signature : this.getCurrentSignature();
        if (newSignature !== oldSignature) {
            this.signature = newSignature;
            this.fireSignatureChange(newSignature);
        }
    };

    /**
     * Clears the signature pad and the signature on the server-side.
     * @returns {undefined}
     */
    this.clear = function () {
        this.signaturePad.clear();
        this.updateSignature(null);
    };

    /**
     * Cross browser add event.
     * Borrowed from https://github.com/samie/Idle
     * 
     * @param {Element} ob
     * @param {string} type
     * @param {Function} fn
     * @returns {Boolean}
     */
    this.addEvent = function (ob, type, fn) {
        if (ob.addEventListener) {
            ob.addEventListener(type, fn, false);
        } else if (ob.attachEvent) {
            ob.attachEvent('on' + type, fn);
        } else {
            type = 'on' + type;
            if (typeof ob[type] === 'function') {
                fn = (function (f1, f2) {
                    return function () {
                        f1.apply(this, arguments);
                        f2.apply(this, arguments);
                    };
                })(ob[type], fn);
            }
            ob[type] = fn;
            return true;
        }
        return false;
    };

    /**
     * Adds a style class to the given element.
     * @param {Element} el Element the class should be added to.
     * @param {string} clazz The class that should be added.
     * @returns {undefined}
     */
    this.addClass = function (el, clazz) {
        var existing = (el.getAttribute("class") || "").split(" ");
        for (var e = 0; e < existing.length; e++) {
            var eClazz = existing[e];
            if (eClazz === clazz) {
                return;
            }
        }
        existing.push(clazz);
        el.setAttribute("class", existing.join(" "));
    };

    /**
     * Removes a style class from the given element.
     * @param {Element} el Element the class should be removed from.
     * @param {string} clazz The class that should be removed.
     * @returns {undefined}
     */
    this.removeClass = function (el, clazz) {
        var existing = (el.getAttribute("class") || "").split(" "),
                length = existing.length;
        for (var e = 0; e < length; e++) {
            var eClazz = existing[e];
            if (eClazz === clazz) {
                existing.splice(e, 1);
                length--;
                e--;
            }
        }
        el.setAttribute("class", existing.join(" "));
    };

    // call the init method
    this.init();

}