/* I don't use Notnull because i have problems with internet now.
* Such a bad work because half of the time i was fixing this...*/
package spb.hse.smirnov.findpair;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class FindPair extends Application {

    private static int fieldSide;

    @Override
    public void start(Stage primaryStage) {
        var pane = new Pane();
        var canvas = new Canvas(Field.WIDTH, Field.HEIGHT);
        var context = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);
        var scene = new Scene(pane, Field.WIDTH, Field.HEIGHT);
        var field = new Field(fieldSide);
        scene.setOnMouseClicked(event -> {
            field.hit(event.getX(), event.getY());
        });
        primaryStage.setScene(scene);
        primaryStage.show();
        var cycle = new GameCycle(field, context);
        cycle.startGame();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("There should be one and only one argument: N");
        }
        fieldSide = Integer.valueOf(args[0]);
        if (fieldSide % 2 != 0 || fieldSide <= 0) {
            throw new IllegalArgumentException("N should be even and positive");
        }
        Application.launch(args);
    }
}
