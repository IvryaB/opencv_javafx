package com.example.opencv_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.IOException;

public class HelloApplication extends Application {

    public static int WIDTH = 1280;
    public static int HEIGHT = 720;

    @Override
    public void start(Stage stage) throws IOException {
        startMainWindow(new WindowData(stage, WIDTH, HEIGHT));
    }

    private void startMainWindow(WindowData data) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Stage stage = data.stage();
        Scene scene = new Scene(fxmlLoader.load(), data.width(), data.height());

        stage.setTitle("Camera View");
        stage.setScene(scene);
        stage.setOnShown(event -> {
            if (fxmlLoader.getController() instanceof HelloController controller) {
                controller.updateCameraDetails();
            }
        });
        stage.setOnCloseRequest(event -> {
            if (fxmlLoader.getController() instanceof HelloController controller) {
                controller.setClosed();
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}