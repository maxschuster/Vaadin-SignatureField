/**
 * @property {Element} parent Parent element
 * @property {Element} canvas Canvas
 * @property {String} signature Signature data url
 * @property {SignaturePad} signaturePad SignaturePad instance
 * @returns {undefined}
 */
function eu_maxschuster_vaadin_signaturefield_internal_SignatureFieldExtension() {

    "use strict";

    /* jshint validthis:true */
    /* jshint -W087 */ // All 'debugger' statements should be removed

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

    this.proxy = function (func, ctx, args) {
        ctx = ctx || this;
        return function () {
            func.apply(ctx, args || arguments);
        };
    };

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

    this.findAndExtendParent = function () {
        var parent = this.getElement(this.getParentId());
        parent.tabIndex = -1;
        this.addEvent(parent, "focus", this.proxy(this.onFocus));
        this.addEvent(parent, "blur", this.proxy(this.onBlur));
        return parent;
    };

    this.createCanvas = function (parent) {
        var canvas = document.createElement("canvas");
        parent.appendChild(canvas);
        return canvas;
    };

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

    this.createCloseButton = function (parent) {
        var closeButton = document.createElement("button");
        closeButton.setAttribute("class", "signaturefield-clearbutton");
        parent.appendChild(closeButton);
        this.addEvent(closeButton, "click", this.proxy(this.onClearButtonClick));
        return closeButton;
    };

    this.onResize = function () {
        this.updateCanvasSize();
    };

    this.onStrokeBegin = function () {
        this.parent.focus();
    };

    this.onStrokeEnd = function () {
        if (this.immediate) {
            this.updateSignature();
        }
    };

    this.onFocus = function () {
        this.addClass(this.parent, "v-focus");
    };

    this.onBlur = function () {
        this.updateSignature();
        this.removeClass(this.parent, "v-focus");
    };

    this.onClearButtonClick = function () {
        this.clear();
    };

    this.getComputedStyle = function (el) {
        if (window.getComputedStyle) {
            return window.getComputedStyle(el);
        }
        return el.currentStyle;
    };

    this.onStateChange = function () {
        this.updateSignatuePad();
    };

    this.updateSignatuePad = function () {
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

    this.getCurrentSignature = function () {
        var signaturePad = this.signaturePad;
        return signaturePad.isEmpty() ?
                null : signaturePad.toDataURL(this.mimeType, 1);
    };

    this.updateSignature = function (signature) {
        var oldSignature = this.signature,
                newSignature = arguments.length > 0 ?
                signature : this.getCurrentSignature();
        if (newSignature !== oldSignature) {
            this.fireSignatureChange(newSignature);
        }
    };

    this.clear = function () {
        this.signaturePad.clear();
        this.updateSignature(null);
    };

    /**
     * Cross browser add event.
     * Borrowed from https://github.com/samie/Idle
     * 
     * @param {Element} ob
     * @param {String} type
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
        existing.push(clazz);
        el.setAttribute("class", existing.join(" "));
    };

    this.init();

}