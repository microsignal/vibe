package io.microvibe.util.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.google.zxing.common.BitMatrix;

public class MatrixToImageWriter {

	private MatrixToImageWriter() {
	}


	/**
	 * Renders a {@link BitMatrix} as an image, where "false" bits are rendered as
	 * white, and "true" bits are rendered as black.
	 */
	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		return toBufferedImage(matrix, ImageColor.DEFAULT);
	}

	/**
	 * Renders a {@link BitMatrix} as an image, where "false" bits are rendered as
	 * white, and "true" bits are rendered as black.
	 */
	public static BufferedImage toBufferedImage(BitMatrix matrix, ImageColor imageColor) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y,//
						matrix.get(x, y) ? imageColor.getForeground().getRGB() : imageColor.getBackground().getRGB());
			}
		}
		return image;
	}

	/**
	 * Writes a {@link BitMatrix} to a stream.
	 *
	 * @see #toBufferedImage(BitMatrix)
	 */
	public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

	/**
	 * Writes a {@link BitMatrix} to a stream.
	 *
	 * @see #toBufferedImage(BitMatrix)
	 */
	public static void writeToStream(BitMatrix matrix, String format, OutputStream stream, ImageColor imageColor)
			throws IOException {
		BufferedImage image = toBufferedImage(matrix, imageColor);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

}
