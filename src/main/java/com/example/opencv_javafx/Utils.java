package com.example.opencv_javafx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javafx.embed.swing.SwingFXUtils;
import org.opencv.core.Mat;
import java.awt.geom.AffineTransform;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;

public final class Utils
{
    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    public static Image mat2Image(Mat frame, boolean flipY, boolean flipX) {
        try {
            BufferedImage image = matToBufferedImage(frame);
            if (flipY) {
                image = createFlippedV(image);
            }
            if (flipX) {
                image = createFlippedH(image);
            }
            return SwingFXUtils.toFXImage(image, null);
        } catch (Exception e) {
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    /**
     * Generic method for putting element running on a non-JavaFX thread on the
     * JavaFX thread, to properly update the UI
     *
     * @param property
     *            a {@link ObjectProperty}
     * @param value
     *            the value to set for the given {@link ObjectProperty}
     */
    public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }

    private static BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image;
        int width = original.width();
        int height = original.height();
        int channels = original.channels();

        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        image = new BufferedImage(width, height,
                channels > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY);

        DataBufferByte bufferByte = (DataBufferByte) image.getRaster().getDataBuffer();
        final byte[] targetPixels = bufferByte.getData();

        // coping source array to target's array
        // simply, transforms Mat to BufferedImage for ImageView
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

    private static BufferedImage createFlippedV(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
        return createTransformed(image, at);
    }

    private static BufferedImage createFlippedH(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
