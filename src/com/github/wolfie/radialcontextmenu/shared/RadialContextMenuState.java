package com.github.wolfie.radialcontextmenu.shared;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.communication.SharedState;

public class RadialContextMenuState extends SharedState {
	private static final long serialVersionUID = 3980984880326145324L;
	public List<String> captions = new ArrayList<String>();
	public String styleName;
}
