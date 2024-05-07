package dev.goldenstack.bedwars.team;

import net.minestom.server.coordinate.Point;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * A bedwars team.
 */
public enum Team {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    CYAN,
    WHITE,
    PINK,
    GRAY;

    /**
     * The position of a team's bed.
     * @param point the block of the foot of the bed
     * @param direction the direction from the foot of the bed to the head
     */
    public record BedPosition(@NotNull Point point, @NotNull Direction direction) {}
}
