package spb.hse.smirnov.cannon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * Simple implementation of Scorched Earth (video game)
 * https://en.wikipedia.org/wiki/Scorched_Earth_(video_game)
 * To shoot press ENTER
 * To move cannon press LEFT or RIGHT
 * To move cannon's barrel press UP or DOWN
 * To change bullet's size press A or D
 */
public class CannonGame extends Application {

    /** Starts game, initializing player, aim and field */
    @Override
    public void start(@NotNull Stage primaryStage) {
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
