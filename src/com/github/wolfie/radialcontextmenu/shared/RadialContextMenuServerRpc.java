package com.github.wolfie.radialcontextmenu.shared;

import com.vaadin.shared.communication.ServerRpc;

public interface RadialContextMenuServerRpc extends ServerRpc {

	void throwNotAComponentConnectorException();

	void itemClicked(int item);

}
