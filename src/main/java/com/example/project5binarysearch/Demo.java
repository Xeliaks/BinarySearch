package com.example.project5binarysearch;

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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Demo extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        Scene sc = new Scene(root, 600, 600);
        stage.setScene(sc);
        stage.show();


        final HighlightableTextArea highlightableTextArea = new HighlightableTextArea();
        highlightableTextArea.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        highlightableTextArea.getTextArea().setWrapText(true);
        highlightableTextArea.getTextArea().setStyle("-fx-font-size: 20px;");
        VBox.setVgrow(highlightableTextArea,Priority.ALWAYS);

        Button highlight = new Button("Highlight");
        TextField stF = new TextField("40");
        TextField enF = new TextField("50");
        HBox hb = new HBox(highlight,stF,enF);
        hb.setSpacing(10);
        highlight.setOnAction(e->{highlightableTextArea.highlight(Integer.parseInt(stF.getText()), Integer.parseInt(enF.getText()));});

        Button remove = new Button("Remove Highlight");
        remove.setOnAction(e->highlightableTextArea.removeHighlight());

        Label lbl = new Label("Resize the window to see if the highlight is moving with text");
        lbl.setStyle("-fx-font-size: 17px;-fx-font-style:italic;");
        HBox rb = new HBox(remove,lbl);
        rb.setSpacing(10);

        root.getChildren().addAll(hb,rb,highlightableTextArea);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Custom TextArea Component.
     */
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
            highlight.setFill(Color.RED);
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

}
