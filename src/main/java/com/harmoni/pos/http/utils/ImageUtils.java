package com.harmoni.pos.http.utils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Utility class for image processing operations such as compression and resizing.
 */
public class ImageUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private ImageUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Compresses and resizes the given image data to 800x600 JPEG format.
     *
     * @param imageData the original image data as a byte array
     * @return the compressed image data as a byte array
     * @throws IOException if an error occurs during image processing
     */
    public static byte[] compressImage(byte[] imageData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        BufferedImage image = ImageIO.read(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizeImage(image, 800, 600), "jpg", outputStream); // Resize to 800x600
        return outputStream.toByteArray();
    }

    /**
     * Resizes the given BufferedImage to the specified width and height.
     *
     * @param originalImage the original BufferedImage
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @return the resized BufferedImage
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
}
