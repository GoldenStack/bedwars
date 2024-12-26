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
        Block foot = team.data().bed().withProperties(Map.of(
                "part", "foot",
                "facing", position.direction().toString().toLowerCase(Locale.ROOT)
        )).withTag(TEAM, team).withTag(IS_BED, true);

        Block head = team.data().bed().withProperties(Map.of(
                "part", "head",
                "facing", position.direction().toString().toLowerCase(Locale.ROOT)
        )).withTag(TEAM, team).withTag(IS_BED, true);

        Direction dir = position.direction();

        instance.setBlock(position.point(), foot);
        instance.setBlock(position.point().add(dir.normalX(), dir.normalY(), dir.normalZ()), head);
    }

}
