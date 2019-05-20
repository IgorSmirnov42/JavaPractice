package spb.hse.smirnov.cannon;

import gherkin.lexer.Fi;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class CannonGame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        var pane = new Pane();
        var canvas = new Canvas(Field.WIDTH, Field.HEIGHT);
        var context = canvas.getGraphicsContext2D();

        var field = new Field();
        var player = new Player(field);
        var aim = new Aim(field);

        pane.getChildren().add(canvas);
        var scene = new Scene(pane, Field.WIDTH, Field.HEIGHT);

        scene.setOnKeyPressed(event -> player.onKeyPressed(event.getCode()));
        scene.setOnKeyReleased(event -> player.onKeyReleased(event.getCode()));

        primaryStage.setScene(scene);
        primaryStage.show();

        var gameCycle = new GameCycle(context, field, player, aim);
        gameCycle.startGame();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
