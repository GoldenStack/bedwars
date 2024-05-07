package dev.goldenstack.bedwars.team;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

/**
 * Team constants and util functions.
 */
public final class Teams {

    private Teams() {}

    public static final @NotNull Tag<Team> TEAM = Tag.String("Team").map(Team::valueOf, Team::toString);

    public static final @NotNull Tag<Boolean> IS_BED = Tag.Boolean("IsBed");

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
        )).withTag(TEAM, team).withTag(IS_BED, true);

        Block head = Teams.getBed(team).withProperties(Map.of(
                "part", "head",
                "facing", position.direction().toString().toLowerCase(Locale.ROOT)
        )).withTag(TEAM, team).withTag(IS_BED, true);

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
            case GREEN -> Block.LIME_BED;
            case YELLOW -> Block.YELLOW_BED;
            case CYAN -> Block.CYAN_BED;
            case WHITE -> Block.WHITE_BED;
            case PINK -> Block.PINK_BED;
            case GRAY -> Block.GRAY_BED;
        };
    }

    /**
     * Gets the wool block that represents the given team.
     */
    public static @NotNull Material getWool(@NotNull Team team) {
        return switch (team) {
            case RED -> Material.RED_WOOL;
            case BLUE -> Material.BLUE_WOOL;
            case GREEN -> Material.LIME_WOOL;
            case YELLOW -> Material.YELLOW_WOOL;
            case CYAN -> Material.CYAN_WOOL;
            case WHITE -> Material.WHITE_WOOL;
            case PINK -> Material.PINK_WOOL;
            case GRAY -> Material.GRAY_WOOL;
        };
    }

    /**
     * Gets the color that represents the given team.
     */
    public static @NotNull NamedTextColor getColor(@NotNull Team team) {
        return switch (team) {
            case RED -> NamedTextColor.RED;
            case BLUE -> NamedTextColor.BLUE;
            case GREEN -> NamedTextColor.GREEN;
            case YELLOW -> NamedTextColor.YELLOW;
            case CYAN -> NamedTextColor.DARK_AQUA;
            case WHITE -> NamedTextColor.WHITE;
            case PINK -> NamedTextColor.LIGHT_PURPLE;
            case GRAY -> NamedTextColor.GRAY;
        };
    }

    /**
     * Gets the name that represents the given team.
     */
    public static @NotNull String getName(@NotNull Team team) {
        return switch (team) {
            case RED -> "Red";
            case BLUE -> "Blue";
            case GREEN -> "Green";
            case YELLOW -> "Yellow";
            case CYAN -> "Cyan";
            case WHITE -> "White";
            case PINK -> "Pink";
            case GRAY -> "Gray";
        };
    }

}
