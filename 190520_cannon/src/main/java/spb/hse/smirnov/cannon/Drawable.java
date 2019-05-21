package spb.hse.smirnov.cannon;

import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;

/** Interface to draw objects using GraphicsContext */
public interface Drawable {
    /**
     * Draws object on a context.
     * Doesn't change context parameters (such as line width, font, color, etc...)
     */
    void draw(@NotNull GraphicsContext context);
}
