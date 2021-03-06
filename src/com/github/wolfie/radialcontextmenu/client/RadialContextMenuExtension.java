package com.github.wolfie.radialcontextmenu.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComputedStyle;
import com.vaadin.client.VConsole;

public class RadialContextMenuExtension implements ContextMenuHandler {

	public interface ItemClickListener {
		void itemClicked(int item);
	}

	private final class ContextPreviewHandler implements NativePreviewHandler {
		@Override
		public void onPreviewNativeEvent(final NativePreviewEvent event) {
			if (event.getTypeInt() == Event.ONCLICK) {
				final NativeEvent mEvent = event.getNativeEvent();
				final Element target = Element.as(mEvent.getEventTarget());

				if (target == canvas.getElement()) {
					handleCanvasClick(mEvent);
				} else {
					close();
				}
			} else if (event.getTypeInt() == Event.ONMOUSEMOVE) {
				final NativeEvent mEvent = event.getNativeEvent();
				handleCanvasMouseMove(mEvent);
			}
		}
	}

	private static final int CANVAS_SIZE_PX = 300;
	private static final double OUTER_RADIUS = CANVAS_SIZE_PX / 2;
	private static final double INNER_RADIUS = 50;

	private static final String CANVAS_CLASSNAME = "h-radialcontextmenu";
	private static final String MENUITEM_CLASSNAME = "menu";

	private static Canvas canvas;
	private static HandlerRegistration previewHandler = null;

	private List<String> captions;
	private List<String> colors;
	private List<Integer> lineWidths;
	private List<String> lineColors;
	private int itemBeingHovered;
	private ItemClickListener itemClickListener = null;
	private String styleName;

	public void extend(final Widget widget) {
		widget.addDomHandler(this, ContextMenuEvent.getType());
	}

	@Override
	public void onContextMenu(final ContextMenuEvent event) {
		if (captions == null || captions.isEmpty()) {
			debug("No menus registered");
			return;
		}

		if (!Canvas.isSupported()) {
			debug("Canvas not supported by this browser: "
					+ BrowserInfo.getBrowserString());
			return;
		}

		close();

		event.getNativeEvent();

		canvas = Canvas.createIfSupported();

		setStyleNames(canvas);

		canvas.getElement().setPropertyInt("width", CANVAS_SIZE_PX);
		canvas.getElement().setPropertyInt("height", CANVAS_SIZE_PX);
		final Style canvasStyle = canvas.getElement().getStyle();

		paintAndHighlight(-1);

		RootPanel.get().add(canvas);
		final int top = event.getNativeEvent().getClientY()
				- canvas.getOffsetHeight() / 2;
		final int left = event.getNativeEvent().getClientX()
				- canvas.getOffsetHeight() / 2;
		canvasStyle.setTop(top, Unit.PX);
		canvasStyle.setLeft(left, Unit.PX);

		previewHandler = Event
				.addNativePreviewHandler(new ContextPreviewHandler());

		event.stopPropagation();
		event.preventDefault();
	}

	private void setStyleNames(final UIObject object) {
		setStyleNames(object.getElement());
	}

	private void setStyleNames(final com.google.gwt.user.client.Element element) {
		String className = CANVAS_CLASSNAME;
		if (styleName != null && !styleName.trim().equals("")) {
			className += " " + CANVAS_CLASSNAME + "-" + styleName + " "
					+ styleName;
		}
		element.setClassName(className);
	}

	private static void debug(final String string) {
		VConsole.log("[RadialContextMenuExtension] " + string);
	}

	public static void close() {
		if (previewHandler != null) {
			previewHandler.removeHandler();
			previewHandler = null;
		}

		if (canvas != null) {
			canvas.removeFromParent();
			canvas = null;
		}
	}

	private void handleCanvasMouseMove(final NativeEvent event) {
		final int x = getOffsetX(event);
		final int y = getOffsetY(event);

		final int item = getItemUnderCursor(x, y);
		if (item != itemBeingHovered) {
			paintAndHighlight(item);
			itemBeingHovered = item;
		}
	}

	private int getItemUnderCursor(final int x, final int y) {
		if (!pointIsOnMenu(x, y)) {
			return -1;
		}

		final int relativeX = x - canvas.getElement().getPropertyInt("width")
				/ 2;
		final int relativeY = y - canvas.getElement().getPropertyInt("height")
				/ 2;
		final double percentileOfMenu = 1
				- (Math.atan2(relativeX, relativeY) + Math.PI) / (Math.PI * 2);
		return (int) Math.floor(percentileOfMenu * captions.size());
	}

	private boolean pointIsOnMenu(int x, int y) {
		final double distance = Math.sqrt((x -= CANVAS_SIZE_PX / 2) * x
				+ (y -= CANVAS_SIZE_PX / 2) * y);
		return INNER_RADIUS <= distance && distance <= OUTER_RADIUS;
	}

	private static int getOffsetX(final NativeEvent event) {
		return getOffsetX(Event.as(event));
	}

	private static int getOffsetX(final Event event) {
		return DOM.eventGetClientX(event)
				- DOM.getAbsoluteLeft(DOM.eventGetTarget(event));
	}

	private static int getOffsetY(final NativeEvent event) {
		return getOffsetY(Event.as(event));
	}

	private static int getOffsetY(final Event event) {
		return DOM.eventGetClientY(Event.as(event))
				- DOM.getAbsoluteTop(DOM.eventGetTarget(event));
	}

	private void handleCanvasClick(final NativeEvent event) {
		final int x = getOffsetX(event);
		final int y = getOffsetY(event);
		if (DOM.eventGetTarget(Event.as(event)) != canvas.getElement()
				|| !pointIsOnMenu(x, y)) {
			close();
		} else {
			final int item = getItemUnderCursor(x, y);
			itemClickListener.itemClicked(item);
		}
	}

	private void paintAndHighlight(final int item) {
		if (colors == null) {
			setupMenuItemStyles();
		}

		final Context2d ctx = canvas.getContext2d();
		ctx.clearRect(0, 0, canvas.getOffsetHeight(), canvas.getOffsetHeight());

		final double sectorSize = 2d / captions.size();
		double sectorStart = 1.5;
		double sectorEnd = sectorStart + sectorSize;
		for (int i = 0; i < colors.size(); i++) {
			final String color = colors.get(i);
			final int lineWidth = lineWidths.get(i);
			final String lineColor = lineColors.get(i);

			ctx.beginPath();
			ctx.setFillStyle(color);
			ctx.setStrokeStyle(lineColor);
			ctx.setLineWidth(lineWidth);
			ctx.arc(CANVAS_SIZE_PX / 2, CANVAS_SIZE_PX / 2,
					OUTER_RADIUS - Math.ceil(lineWidth / 2), Math.PI
							* sectorStart, Math.PI * sectorEnd, false);
			ctx.arc(CANVAS_SIZE_PX / 2, CANVAS_SIZE_PX / 2,
					INNER_RADIUS + Math.ceil(lineWidth / 2), Math.PI
							* sectorEnd, Math.PI * sectorStart, true);
			ctx.closePath();
			ctx.fill();

			if (lineWidth > 0) {
				ctx.stroke();
			}

			sectorStart = sectorEnd;
			sectorEnd += sectorSize;
		}

		ctx.setTextAlign(Context2d.TextAlign.CENTER);
		ctx.setFont("14px sans-serif");
		ctx.setLineWidth(3);
		final double textY = (OUTER_RADIUS - INNER_RADIUS) / 2 + INNER_RADIUS;

		for (int i = 0; i < captions.size(); i++) {
			final String caption = captions.get(i);

			ctx.save();
			ctx.translate(canvas.getElement().getPropertyInt("width") / 2,
					canvas.getElement().getPropertyInt("height") / 2);
			double rotate = Math.PI * 2 * i / captions.size();
			rotate += Math.PI / captions.size();
			ctx.rotate(rotate);

			if (item != i) {
				ctx.setFillStyle("black");
				ctx.setStrokeStyle("white");
			} else {
				ctx.setFillStyle("white");
				ctx.setStrokeStyle("black");
			}

			if (Math.PI / 2 < rotate && rotate < Math.PI * 3 / 2) {
				ctx.rotate(Math.PI);
				ctx.strokeText(caption, 0, textY);
				ctx.fillText(caption, 0, textY);
			} else {
				ctx.strokeText(caption, 0, -textY + 7);
				ctx.fillText(caption, 0, -textY + 7);
			}
			ctx.restore();
		}
	}

	private void setupMenuItemStyles() {
		final com.google.gwt.user.client.Element root = DOM.createDiv();
		setStyleNames(root);
		RootPanel.get().getElement().appendChild(root);

		colors = new ArrayList<String>();
		lineWidths = new ArrayList<Integer>();
		lineColors = new ArrayList<String>();

		for (final String caption : captions) {
			final com.google.gwt.user.client.Element menuItem = DOM.createDiv();
			menuItem.setClassName(MENUITEM_CLASSNAME);
			menuItem.setAttribute("caption", caption);
			root.appendChild(menuItem);
			final ComputedStyle style = new ComputedStyle(menuItem);

			final String bgColor = style.getProperty("background-color");
			colors.add(bgColor);

			final int lineWidth = style.getIntProperty("border-left-width");
			lineWidths.add(lineWidth);

			final String lineColor = style.getProperty("border-left-color");
			lineColors.add(lineColor);
		}

		root.removeFromParent();
	}

	public void setup(final List<String> captions, final String styleName) {
		this.captions = captions;
		this.styleName = styleName;

		colors = null;
		lineColors = null;
		lineWidths = null;
	}

	public void setItemClickListener(final ItemClickListener listener) {
		itemClickListener = listener;
	}
}
