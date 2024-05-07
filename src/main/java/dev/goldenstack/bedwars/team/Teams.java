package dev.goldenstack.bedwars.team;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

/**
 * Team constants and util functions.
 */
public final class Teams {

    private Teams() {}

    /**
     * Places the bed of a given team in a map.
     * @param instance the instance to place blocks in
     * @param team the team that owns the bed
     * @param position the bed position of the team
     */
    public static void placeBed(@NotNull Instance instance, @NotNull Team team, @NotNull Team.BedPosition position) {
        Block foot = Teams.getBed(team).withProperties(Map.of(
                "part", "foot",
                "facing", position.direction().toString().toLowerCase(Locale.ROOT)
        ));

        Block head = Teams.getBed(team).withProperties(Map.of(
                "part", "head",
                "facing", position.direction().toString().toLowerCase(Locale.ROOT)
        ));

        Direction dir = position.direction();

        instance.setBlock(position.point(), foot);
        instance.setBlock(position.point().add(dir.normalX(), dir.normalY(), dir.normalZ()), head);
    }

    /**
     * Gets the bed block that represents the given team.
     */
    public static @NotNull Block getBed(@NotNull Team team) {
        return switch (team) {
            case RED -> Block.RED_BED;
            case BLUE -> Block.BLUE_BED;
            case GREEN -> Block.GREEN_BED;
            case YELLOW -> Block.YELLOW_BED;
            case CYAN -> Block.CYAN_BED;
            case WHITE -> Block.WHITE_BED;
            case PINK -> Block.PINK_BED;
            case GRAY -> Block.GRAY_BED;
        };
    }

}
