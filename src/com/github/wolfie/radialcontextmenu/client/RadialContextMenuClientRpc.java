package com.github.wolfie.radialcontextmenu.client;

import com.vaadin.shared.communication.ClientRpc;

public interface RadialContextMenuClientRpc extends ClientRpc {
	void closeIfOpen();
}
