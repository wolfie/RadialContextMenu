package com.github.wolfie.radialcontextmenu.client;

import com.github.wolfie.radialcontextmenu.RadialContextMenu;
import com.github.wolfie.radialcontextmenu.client.RadialContextMenuExtension.ItemClickListener;
import com.github.wolfie.radialcontextmenu.shared.RadialContextMenuClientRpc;
import com.github.wolfie.radialcontextmenu.shared.RadialContextMenuServerRpc;
import com.github.wolfie.radialcontextmenu.shared.RadialContextMenuState;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(RadialContextMenu.class)
public class RadialContextMenuConnector extends AbstractExtensionConnector
		implements ItemClickListener {
	private static final long serialVersionUID = -5237182069981212243L;
	private final RadialContextMenuExtension menu = new RadialContextMenuExtension();
	private RadialContextMenuServerRpc rpc;

	@Override
	protected void init() {
		rpc = RpcProxy.create(RadialContextMenuServerRpc.class, this);
		registerRpc(RadialContextMenuClientRpc.class,
				new RadialContextMenuClientRpc() {
					private static final long serialVersionUID = 7113828973377686218L;

					@Override
					public void closeIfOpen() {
						menu.close();
					}
				});
		menu.setItemClickListener(this);
	}

	@Override
	protected void extend(final ServerConnector target) {
		if (!(target instanceof ComponentConnector)) {
			rpc.throwNotAComponentConnectorException();
		}

		final ComponentConnector component = (ComponentConnector) target;
		menu.extend(component.getWidget());
	}

	@Override
	public void onStateChanged(final StateChangeEvent stateChangeEvent) {
		menu.setup(getState().captions);
	}

	@Override
	public RadialContextMenuState getState() {
		return (RadialContextMenuState) super.getState();
	}

	@SuppressWarnings("unused")
	private void debug(final String string) {
		VConsole.log("RadialContextMenuConnector: " + string);
	}

	@Override
	public void itemClicked(final int item) {
		rpc.itemClicked(item);
	}
}
