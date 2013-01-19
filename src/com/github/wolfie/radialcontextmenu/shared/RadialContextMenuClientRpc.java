package com.github.wolfie.radialcontextmenu.shared;

import com.vaadin.shared.communication.ClientRpc;

public interface RadialContextMenuClientRpc extends ClientRpc {
	void closeIfOpen();
}
