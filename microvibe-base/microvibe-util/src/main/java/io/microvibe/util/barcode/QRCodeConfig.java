package io.microvibe.util.barcode;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeConfig {
	private String content;
	private String charset;
	private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
	private int width = 200; // 图像宽度
	private int height = 200; // 图像高度
	private String format = "PNG"; // 图像类型
	private ImageColor imageColor = ImageColor.DEFAULT;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public ErrorCorrectionLevel getErrorCorrectionLevel() {
		return errorCorrectionLevel;
	}

	public void setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) {
		this.errorCorrectionLevel = errorCorrectionLevel;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public ImageColor getImageColor() {
		return imageColor;
	}

	public void setImageColor(ImageColor imageColor) {
		this.imageColor = imageColor;
	}

}
