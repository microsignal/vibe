package io.microvibe.util.barcode;

import java.awt.Color;

public class ImageColor {
	public static final ImageColor DEFAULT = new ImageColor(Color.BLACK, Color.white);
	public static final ImageColor BLUE = new ImageColor(Color.BLUE, Color.white);
	public static final ImageColor RED = new ImageColor(Color.RED, Color.white);
	public static final ImageColor GREEN = new ImageColor(Color.GREEN, Color.white);
	private Color foreground;
	private Color background;

	public ImageColor(Color foreground, Color background) {
		super();
		this.foreground = foreground;
		this.background = background;
	}

	public Color getForeground() {
		return foreground;
	}

	public Color getBackground() {
		return background;
	}

}
