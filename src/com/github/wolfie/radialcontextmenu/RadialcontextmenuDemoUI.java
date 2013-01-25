package com.github.wolfie.radialcontextmenu;

import com.github.wolfie.radialcontextmenu.RadialContextMenu.MenuSelectEvent;
import com.github.wolfie.radialcontextmenu.RadialContextMenu.MenuSelectListener;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
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
@Title("Radial Context Menu Demo")
@Theme("demo")
public class RadialcontextmenuDemoUI extends UI {

	@Override
	protected void init(final VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		setContent(layout);

		final Button button1 = new Button("Right Click Me");
		button1.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				button1.setCaption("No, I Said \"Right Click\"");
			}
		});
		layout.addComponent(button1);
		layout.setComponentAlignment(button1, Alignment.MIDDLE_CENTER);

		final RadialContextMenu menu1 = new RadialContextMenu();
		menu1.setStyleName("custom");
		menu1.addMenuItem("First and Close", new MenuSelectListener() {
			@Override
			public void menuSelected(final MenuSelectEvent e) {
				Notification.show("First Item Pressed");
				e.getSource().closeIfOpen();
			}
		});
		menu1.addMenuItem("Second Item", new MenuSelectListener() {
			@Override
			public void menuSelected(final MenuSelectEvent e) {
				Notification.show("Second Item Pressed");
			}
		});
		menu1.addMenuItem("Third Item", new MenuSelectListener() {
			@Override
			public void menuSelected(final MenuSelectEvent e) {
				Notification.show("Third Item Pressed");
			}
		});
		menu1.extend(button1);

		final Button button2 = new Button("Right Click Me");
		button1.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				button2.setCaption("No, I Said \"Right Click\"");
			}
		});

		layout.addComponent(button2);
		layout.setComponentAlignment(button2, Alignment.MIDDLE_CENTER);

		final RadialContextMenu menu2 = new RadialContextMenu();
		menu2.addMenuItem("First and Close", new MenuSelectListener() {
			@Override
			public void menuSelected(final MenuSelectEvent e) {
				Notification.show("First Item Pressed");
				e.getSource().closeIfOpen();
			}
		});
		menu2.addMenuItem("Second Item", new MenuSelectListener() {
			@Override
			public void menuSelected(final MenuSelectEvent e) {
				Notification.show("Second Item Pressed");
			}
		});

		menu2.extend(button2);
	}
}