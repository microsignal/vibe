package io.microvibe.util.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {
	static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

	private QRCodeUtil() {
	}

	public static String decodeQRCode(BufferedImage image) throws NotFoundException {
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		Binarizer binarizer = new HybridBinarizer(source);
		BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
		Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf8");
		hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE));
		Result result = new MultiFormatReader().decode(binaryBitmap, hints);// 对图像进行解码
		logger.debug("result's BarcodeFormat：{} ", result.getBarcodeFormat());
		return result.getText();
	}

	public static void encodeQRCode(QRCodeConfig config, OutputStream out) throws WriterException, IOException {
		// 生成矩阵
		BitMatrix bitMatrix = toBitMatrix(config);
		String format = config.getFormat();
		ImageColor imageColor = config.getImageColor();
		// 输出图像
		MatrixToImageWriter.writeToStream(bitMatrix, format, out, imageColor);
	}

	public static BufferedImage encodeQRCode(QRCodeConfig config) throws WriterException {
		// 生成矩阵
		BitMatrix bitMatrix = toBitMatrix(config);
		ImageColor imageColor = config.getImageColor();
		// 输出图像
		return MatrixToImageWriter.toBufferedImage(bitMatrix, imageColor);
	}

	/**
	 * 生成矩阵
	 */
	public static BitMatrix toBitMatrix(QRCodeConfig config) throws WriterException {
		String content = config.getContent();
		String charset = config.getCharset();
		ErrorCorrectionLevel errorCorrectionLevel = config.getErrorCorrectionLevel();
		int width = config.getWidth();
		int height = config.getHeight();
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, charset);
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
		// 生成矩阵
		return new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
	}

}
