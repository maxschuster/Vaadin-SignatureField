package eu.maxschuster.vaadin.signaturepadfield.shared;

import com.vaadin.shared.communication.ServerRpc;

public interface ClearButtonServerRpc extends ServerRpc {
	
	public void clear();

}
