package com.github.wolfie.radialcontextmenu;

import com.github.wolfie.radialcontextmenu.RadialContextMenu.MenuSelectEvent;
import com.github.wolfie.radialcontextmenu.RadialContextMenu.MenuSelectListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class RadialcontextmenuDemoUI extends UI {

	private RadialContextMenu radialContextMenu;

	@Override
	protected void init(final VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		setContent(layout);

		final Button button = new Button("Right Click Me");
		button.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				button.setCaption("No, I Said \"Right Click\"");
			}
		});
		layout.addComponent(button);
		layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);

		radialContextMenu = new RadialContextMenu();
		radialContextMenu.addMenuItem("First and Close", "#e61f6f",
				new MenuSelectListener() {
					@Override
					public void menuSelected(final MenuSelectEvent e) {
						Notification.show("First Item Pressed");
						e.getSource().closeIfOpen();
					}
				});
		radialContextMenu.addMenuItem("Second Item", "#00b4f0",
				new MenuSelectListener() {
					@Override
					public void menuSelected(final MenuSelectEvent e) {
						Notification.show("Second Item Pressed");
					}
				});
		radialContextMenu.addMenuItem("Third Item", "#41b527",
				new MenuSelectListener() {
					@Override
					public void menuSelected(final MenuSelectEvent e) {
						Notification.show("Third Item Pressed");
					}
				});
		radialContextMenu.extend(button);
	}
}