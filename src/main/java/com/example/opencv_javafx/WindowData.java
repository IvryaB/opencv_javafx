package com.example.opencv_javafx;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.util.Pair;

public final class WindowData {

    private final Stage stage;
    private final Pair<Integer, Integer> sizes;

    public WindowData(Stage stage, int width, int height) {
        this.stage = stage;
        this.sizes = new Pair<>(width, height);
    }

    public WindowData(Stage stage, Pair<Integer, Integer> sizes) {
        this(stage, sizes.getKey(), sizes.getValue());
    }

    public Stage stage() {
        return stage;
    }

    public Pair<Integer, Integer> sizes() {
        return sizes;
    }

    public int width() {
        return sizes.getKey();
    }

    public int height() {
        return sizes.getValue();
    }

}
