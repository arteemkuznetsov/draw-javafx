package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;

import java.awt.*;


public class Main extends Application {
    BorderPane root;
    HBox dockPanel;
    Canvas canvas;
    GraphicsContext gc;
    ColorPicker colorPicker;
    Slider slider;

    Color color;
    double brushSize;

    double lastX, lastY; // last cursor X Y
    double ovalX, ovalY;

    private void initComponents(Stage primaryStage) {
        color = Color.BLACK;
        brushSize = 5;

        root = new BorderPane();
        primaryStage.setTitle("Draw");
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();

        canvas = new Canvas(primaryStage.getWidth(), primaryStage.getHeight() - 175); // добавить еще одну область рисования для fillOval на dockPanel
        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        root.getChildren().add(canvas);

        dockPanel = new HBox();
        dockPanel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        dockPanel.setPadding(new Insets(15));
        dockPanel.setSpacing(15);
        dockPanel.setAlignment(Pos.BASELINE_CENTER);
        root.setBottom(dockPanel);

        colorPicker = new ColorPicker(color);
        slider = new Slider(1, 50, 1);
        slider.setValue(brushSize);

        dockPanel.getChildren().addAll(colorPicker, slider);
    }

    @Override
    public void start(Stage primaryStage) {
        initComponents(primaryStage);

        primaryStage.heightProperty().addListener(event -> canvas.setHeight(primaryStage.getHeight() - 175));
        primaryStage.widthProperty().addListener(event -> canvas.setWidth(primaryStage.getWidth()));

        canvas.setOnMousePressed(event -> {
            lastX = event.getX();
            lastY = event.getY();
        });

        root.setOnMousePressed(event -> {
            double x = event.getSceneX();
            double y = event.getSceneY();

            gc.setLineWidth(brushSize);
            gc.setFill(color);
            gc.fillOval(x, y, brushSize, brushSize);
        });

        root.setOnMouseDragged(event -> {
            double x = event.getSceneX();
            double y = event.getSceneY();

            gc.setLineWidth(brushSize);
            gc.setStroke(color);
            gc.strokeLine(lastX, lastY, x, y); // соединяем линией предыдущую координату и текущую
            lastX = x;
            lastY = y;

            System.out.println("x:" + x + " y:" + y);
        });

        colorPicker.setOnAction(event -> {
            color = colorPicker.getValue();
        });

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {

            ovalX = slider.getLayoutX() + (slider.getWidth() / 2.25);
            ovalY = (primaryStage.getHeight() - 120) - slider.getLayoutY();
            double size = (double) newValue;
            gc.clearRect(ovalX - 30, ovalY - 30, 60, 60);
            gc.fillOval(ovalX - 0.5 * size, ovalY - 0.5 * size, size, size);

            brushSize = size;
        });
        slider.setOnMouseExited(event -> gc.clearRect(ovalX - 30, ovalY - 30, 60, 60));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
