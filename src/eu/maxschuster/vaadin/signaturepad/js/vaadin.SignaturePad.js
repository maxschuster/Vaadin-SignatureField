window.eu_maxschuster_vaadin_signaturepad_SignaturePad = function() {
	
	/*
     * Methods:
     * addResizeListener()
     * getConnectorId()
     * getElement()
     * getParentId()
     * getRpcProxy()
     * getState()
     * registerRpc()
     * removeResizeListener()
     * translateVaadinUri()
     */
    console.log(this);
    
    var self = this,
        elem = self.getElement(),
        width = self.getState().width,
        height = self.getState().height,
        canvas = document.createElement("canvas");

    elem.className += " v-signaturepad";
    
    function resizeCanvas() {
	    canvas.height = elem.clientHeight || 0;
	    canvas.width = elem.clientWidth || 0;
    }

    self.onStateChange = function() {
        console.log("state changed!", arguments, self.getState());
        
        var state = self.getState();
        
        if (height != state.height || width != state.width) {
        	resizeCanvas();
        }
        
    };
    
    resizeCanvas();
    
    elem.appendChild(canvas);

    console.log(elem, elem.clientWidth, elem.clientHeight, self.getState());
	
    console.log("...");
};