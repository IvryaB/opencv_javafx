package com.example.opencv_javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HelloController {

    @FXML
    public ImageView currentFrame;
    @FXML
    public Button button;
    @FXML
    public Label cameraDetails;
    @FXML
    public Button cameraSetButton;
    @FXML
    public TextField cameraIdField;

    // WORKING VARS //
    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;

    // the OpenCV object that realizes the video capture
    private final VideoCapture capture = new VideoCapture();

    // a flag to change the button behavior
    private boolean cameraActive = false;

    // the id of the camera to be used
    // 0 - first camera, 1 - second, etc.
    private int cameraId = 0;

    // fps displayed
    private final int cameraFPS = 10;

    // grab a frame every X ms
    private final int waitMs = 1000 / cameraFPS;

    // SETTINGS //
    // displaying in gray tones
    private boolean isGrayTones = false;

    // flip image vertically
    private boolean flipY = true;

    // flip image horizontally
    private boolean flipX = false;

    @FXML
    public void startCamera(ActionEvent actionEvent) {
        if (!cameraActive) {
            // start the video capture
            capture.open(cameraId);

            // is the video stream available?
            if (capture.isOpened()) {
                cameraActive = true;
                Runnable frameGrabber = () -> {
                    // effectively grab and process a single frame
                    Mat frame = grabFrame();
                    // convert and show the frame
                    Image imageToShow = Utils.mat2Image(frame, flipY, flipX);
                    updateImageView(currentFrame, imageToShow);
                };

                timer = Executors.newSingleThreadScheduledExecutor();
                timer.scheduleAtFixedRate(frameGrabber, 0, waitMs, TimeUnit.MILLISECONDS);

                // update the button content
                button.setText("Stop");
            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            cameraActive = false;
            // update again the button content
            button.setText("Start");

            // stop the timer
            stopAcquisition();
        }

        updateCameraDetails();
    }

    private Mat grabFrame() {
        // init everything
        Mat frame = new Mat();

        // check if the capture is open
        if (capture.isOpened()) {
            try {
                // read the current frame
                capture.read(frame);
                if (isGrayTones) {
                    // turning to gray
                    // if the frame is not empty, process it
                    if (!frame.empty()) {
                        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                    }
                }
            } catch (Exception e) {
                // log the error
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        return frame;
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition() {
        if (timer != null && !timer.isShutdown()) {
            try {
                // stop the timer
                timer.shutdown();
                timer.awaitTermination(waitMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (capture.isOpened()) {
            // release the camera if capturing
            capture.release();
        }
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view
     *            the {@link ImageView} to update
     * @param image
     *            the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    protected void setClosed() {
        stopAcquisition();
    }

    public void setCamera(ActionEvent actionEvent) {
        try {
            String cameraIdStr = cameraIdField.getText();
            int newCameraId = Integer.parseInt(cameraIdStr);
            if (cameraActive && cameraId != newCameraId) {
                cameraId = newCameraId;
                restartCamera();
            }
            if (!cameraActive) {
                startCamera();
            }
        } catch (Exception e) {
            cameraIdField.setText("Wrong camera ID input");
        }
    }

    private void restartCamera() {
        // TODO rewrite
        startCamera();
        startCamera();
    }

    private void startCamera() {
        startCamera(null);
    }

    public void updateCameraDetails() {
        cameraDetails.setText("Camera[id%s]  /  isActive[%s]".formatted(cameraId, cameraActive));
    }

}