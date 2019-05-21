package spb.hse.smirnov.cannon;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

public class Aim {
    public static final double AIM_POSITION_X = 750;
    public static final double AIM_RADIUS = 10;
    @NotNull private Field field;

    public Aim(@NotNull Field field) {
        this.field = field;
    }

    public void draw(@NotNull GraphicsContext context) {
        var fill = context.getFill();

        context.setFill(Color.DARKGOLDENROD);
        context.fillOval(AIM_POSITION_X - AIM_RADIUS,
                field.getYByX(AIM_POSITION_X) - AIM_RADIUS,
                AIM_RADIUS * 2,
                AIM_RADIUS * 2);

        context.setFill(fill);
    }
}
