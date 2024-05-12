package dev.goldenstack.bedwars.team;

import dev.goldenstack.bedwars.map.BedwarsMap;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
    public static void placeBed(@NotNull Instance instance, @NotNull Team team, @NotNull BedwarsMap.BedPosition position) {
        Block foot = Teams.data(team).bed().withProperties(Map.of(
                "part", "foot",
                "facing", position.direction().toString().toLowerCase(Locale.ROOT)
        )).withTag(TEAM, team).withTag(IS_BED, true);

        Block head = Teams.data(team).bed().withProperties(Map.of(
                "part", "head",
                "facing", position.direction().toString().toLowerCase(Locale.ROOT)
        )).withTag(TEAM, team).withTag(IS_BED, true);

        Direction dir = position.direction();

        instance.setBlock(position.point(), foot);
        instance.setBlock(position.point().add(dir.normalX(), dir.normalY(), dir.normalZ()), head);
    }

    public record TeamData(@NotNull String name,
                           @NotNull TextColor color,
                           @NotNull Block bed,
                           @NotNull Material wool,
                           @NotNull Material concrete,
                           @NotNull Material glass) {
    }

    private static final @NotNull Map<Team, TeamData> DATA = Map.of(
            Team.RED, new TeamData("Red", NamedTextColor.RED, Block.RED_BED, Material.RED_WOOL, Material.RED_CONCRETE, Material.RED_STAINED_GLASS),
            Team.BLUE, new TeamData("Blue", NamedTextColor.BLUE, Block.BLUE_BED, Material.BLUE_WOOL, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS),
            Team.GREEN, new TeamData("Green", NamedTextColor.GREEN, Block.LIME_BED, Material.LIME_WOOL, Material.LIME_CONCRETE, Material.LIME_STAINED_GLASS),
            Team.YELLOW, new TeamData("Yellow", NamedTextColor.YELLOW, Block.YELLOW_BED, Material.YELLOW_WOOL, Material.YELLOW_CONCRETE, Material.YELLOW_STAINED_GLASS),
            Team.CYAN, new TeamData("Cyan", NamedTextColor.AQUA, Block.CYAN_BED, Material.CYAN_WOOL, Material.CYAN_CONCRETE, Material.CYAN_STAINED_GLASS),
            Team.WHITE, new TeamData("White", NamedTextColor.WHITE, Block.WHITE_BED, Material.WHITE_WOOL, Material.WHITE_CONCRETE, Material.WHITE_STAINED_GLASS),
            Team.PINK, new TeamData("Pink", NamedTextColor.LIGHT_PURPLE, Block.PINK_BED, Material.PINK_WOOL, Material.PINK_CONCRETE, Material.PINK_STAINED_GLASS),
            Team.GRAY, new TeamData("Gray", NamedTextColor.GRAY, Block.GRAY_BED, Material.GRAY_WOOL, Material.GRAY_CONCRETE, Material.GRAY_STAINED_GLASS)
    );

    public static @NotNull TeamData data(@NotNull Team team) {
        return DATA.get(team);
    }

}
