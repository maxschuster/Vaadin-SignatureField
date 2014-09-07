package eu.maxschuster.vaadin.signaturepadfield.shared;

import com.vaadin.shared.communication.ServerRpc;

public interface SignaturePadFieldServerRpc extends ServerRpc {

	public void setTextValue(String textValue);
	
}
