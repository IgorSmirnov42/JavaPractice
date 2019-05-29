package spb.hse.smirnov.findpair;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameCycle {
    private Field field;
    private GraphicsContext context;
    private AnimationTimer timer;

    public GameCycle(Field field, GraphicsContext context) {
        this.field = field;
        this.context = context;
    }

    public void startGame() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!field.isEndOfGame()) {
                    field.tick();
                    Drawer.drawFieldBase(field, context);
                    Drawer.drawActiveCells(field, context);
                } else {
                    Drawer.drawCongratulations(field, context);
                    timer.stop();
                }
            }
        };
        timer.start();
    }
}
