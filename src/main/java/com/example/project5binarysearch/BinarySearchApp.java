package com.example.project5binarysearch;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class BinarySearchApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        TextField inputField = new TextField();
        Button generateButton = new Button("Generate");
        TextField targetField = new TextField();
        Button searchButton = new Button("Search");
        Button resetButton = new Button("Reset");
        Label statusLabel = new Label();
        TextField delayInput = new TextField();



        HighlightableTextArea visualizationArea = new HighlightableTextArea();
        visualizationArea.getTextArea().setEditable(false);
        visualizationArea.getTextArea().setWrapText(true);
        visualizationArea.setPrefHeight(500);



        HBox topRow = new HBox(10);
        topRow.setPadding(new Insets(10));
        topRow.getChildren().addAll(inputField, generateButton, targetField, searchButton, delayInput, resetButton);

        VBox root = new VBox(10);
        root.getChildren().addAll(topRow, visualizationArea, statusLabel);

        generateButton.setOnAction(event -> {
            try {

                int size = Integer.parseInt(inputField.getText().trim());

                ArrayList<Integer> numbers = new ArrayList<>();
                Random random = new Random();
                int range = size * 2;
                for (int i = 0; i < size; i++) {
                    numbers.add(random.nextInt(range) + 1);
                }
                Collections.sort(numbers);

                StringBuilder sb = new StringBuilder();
                for (int num : numbers) {
                    sb.append(num).append("\n");
                }
                visualizationArea.setText(sb.toString());
            } catch (NumberFormatException e) {

                System.err.println("Input must be an integer.");
            }
        });



        searchButton.setOnAction(event -> {
            try {
                int[] array = Arrays.stream(visualizationArea.getText().split("\n")).mapToInt(Integer::parseInt).toArray();
                int key = Integer.parseInt(targetField.getText().trim());
                binarySearchWithVisualization(array, key, delayInput, statusLabel, visualizationArea);
            } catch (NumberFormatException e) {
                statusLabel.setText("Invalid input for search key.");
            }
        });

        resetButton.setOnAction(event -> {
            inputField.clear();
            targetField.clear();
            visualizationArea.getTextArea().clear();
        });



        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Binary Search Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
        statusLabel.setText("");
    }


private void binarySearchWithVisualization(int[] array, int key, TextField delayInput, Label statusLabel, HighlightableTextArea visualizationArea) {
    final int[] low = {0};
    final int[] high = {array.length - 1};
    final int[] step = {0};
    final int[] iterCount = {0};
    Timeline timeline = new Timeline();
    int duration = Integer.parseInt(delayInput.getText());
    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration), event -> {
        int mid = (low[0] + high[0]) / 2;
        iterCount[0]++;
        statusLabel.setText("Iteration count: " + iterCount[0] + ". Range: " + low[0] + " - " + high[0] + ". Mid: " + mid);
        if (low[0] <= high[0]) {
            highlightArrayPositions(low[0], mid, high[0], visualizationArea);
            if (array[mid] < key) {
                low[0] = mid + 1;
            } else if (array[mid] > key) {
                high[0] = mid - 1;
            } else {
                statusLabel.setText("Value found at index: " + mid + " in " + iterCount[0] + " iterations");
                timeline.stop();
            }
        } else {
            statusLabel.setText("Value not found" + " in " + iterCount[0] + " iterations");
            timeline.stop();
        }
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
}

    private void highlightArrayPositions(int low, int mid, int high, HighlightableTextArea visualizationArea) {
    visualizationArea.removeHighlight();
    int lowStartPos = visualizationArea.getText().indexOf(String.valueOf(low));
    int highEndPos = visualizationArea.getText().indexOf(String.valueOf(high));

    visualizationArea.highlight(lowStartPos, highEndPos);


}
    class HighlightableTextArea extends StackPane {
        final TextArea textArea = new TextArea();
        int highlightStartPos = -1;
        int highlightEndPos = -1;
        boolean highlightInProgress = false;

        final Rectangle highlight = new Rectangle();

        private StringProperty text = new SimpleStringProperty();

        private Group selectionGroup;

        public final String getText() {
            return text.get();
        }

        public final void setText(String value) {
            text.set(value);
        }

        public final StringProperty textProperty() {
            return text;
        }

        public HighlightableTextArea() {
            highlight.setFill(Color.LIGHTBLUE);
            highlight.setMouseTransparent(true);
            highlight.setBlendMode(BlendMode.DARKEN);

            textArea.textProperty().bindBidirectional(text);
            getChildren().add(textArea);
            setAlignment(Pos.TOP_LEFT);
            textArea.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (highlightStartPos > -1 && highlightEndPos > -1 && selectionGroup != null) {
                    highlightInProgress = true;
                    textArea.selectRange(highlightStartPos, highlightEndPos);
                    Bounds bounds = selectionGroup.getBoundsInLocal();
                    updateHightlightBounds(bounds);
                }
            });
        }

        private void updateHightlightBounds(Bounds bounds) {
            if (bounds.getWidth() > 0) {
                if (!getChildren().contains(highlight)) {
                    getChildren().add(highlight);
                }
                highlight.setTranslateX(bounds.getMinX() + 1);
                highlight.setTranslateY(bounds.getMinY() + 1);
                highlight.setWidth(bounds.getWidth());
                highlight.setHeight(bounds.getHeight());
                Platform.runLater(() -> {
                    textArea.deselect();
                    highlightInProgress = false;
                });
            }
        }

        public TextArea getTextArea() {
            return textArea;
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            if (selectionGroup == null) {
                final Region content = (Region) lookup(".content");
                // Looking for the Group node that is responsible for selection
                content.getChildrenUnmodifiable().stream().filter(node -> node instanceof Group).map(node -> (Group) node).filter(grp -> {
                    boolean notSelectionGroup = grp.getChildren().stream().anyMatch(node -> !(node instanceof Path));
                    return !notSelectionGroup;
                }).findFirst().ifPresent(n -> {
                    n.boundsInLocalProperty().addListener((obs, old, bil) -> {
                        if (highlightInProgress) {
                            updateHightlightBounds(bil);
                        }
                    });
                    selectionGroup = n;
                });
            }
        }

        public void highlight(int startPos, int endPos) {
            highlightInProgress = true;
            highlightStartPos = startPos;
            highlightEndPos = endPos;
            textArea.selectRange(startPos, endPos);
        }

        public void removeHighlight() {
            textArea.deselect();
            getChildren().remove(highlight);
            highlightStartPos = -1;
            highlightEndPos = -1;
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}

