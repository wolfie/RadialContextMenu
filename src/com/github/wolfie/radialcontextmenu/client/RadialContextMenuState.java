package com.github.wolfie.radialcontextmenu.client;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.communication.SharedState;

public class RadialContextMenuState extends SharedState {
	private static final long serialVersionUID = 3980984880326145324L;
	public List<String> captions = new ArrayList<String>();
	public List<String> colors = new ArrayList<String>();
}
