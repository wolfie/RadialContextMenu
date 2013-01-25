package com.github.wolfie.radialcontextmenu;

import java.util.ArrayList;
import java.util.List;

import com.github.wolfie.radialcontextmenu.shared.RadialContextMenuClientRpc;
import com.github.wolfie.radialcontextmenu.shared.RadialContextMenuServerRpc;
import com.github.wolfie.radialcontextmenu.shared.RadialContextMenuState;
import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.AbstractComponent;

public class RadialContextMenu extends AbstractExtension {
	public interface MenuSelectListener {
		public void menuSelected(MenuSelectEvent event);
	}

	public class MenuSelectEvent {
		private MenuSelectEvent() {
		}

		public RadialContextMenu getSource() {
			return RadialContextMenu.this;
		}
	}

	private static final long serialVersionUID = 539780872106336763L;
	private final List<MenuSelectListener> listeners = new ArrayList<MenuSelectListener>();
	private final RadialContextMenuClientRpc clientRpc = getRpcProxy(RadialContextMenuClientRpc.class);

	public RadialContextMenu() {
		registerRpc(new RadialContextMenuServerRpc() {
			private static final long serialVersionUID = 1861551341415397528L;

			@Override
			public void throwNotAComponentConnectorException() {
				throw new IllegalArgumentException(
						RadialContextMenu.class.getSimpleName()
								+ " can only extend subclasses of "
								+ AbstractComponent.class.getSimpleName());
			}

			@Override
			public void itemClicked(final int item) {
				listeners.get(item).menuSelected(new MenuSelectEvent());
			}
		});
	}

	public void extend(final AbstractComponent target) {
		super.extend(target);
	}

	public void addMenuItem(final String caption,
			final MenuSelectListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("listener may not be null");
		}
		listeners.add(listener);
		getState().captions.add(caption);
	}

	@Override
	protected RadialContextMenuState getState() {
		return (RadialContextMenuState) super.getState();
	}

	public void closeIfOpen() {
		clientRpc.closeIfOpen();
	}

	public void setStyleName(final String styleName) {
		getState().styleName = styleName;
	}
}
