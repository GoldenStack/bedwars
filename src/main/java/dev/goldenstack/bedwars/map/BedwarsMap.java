package dev.goldenstack.bedwars.map;

import dev.goldenstack.bedwars.team.Team;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;

/**
 * A bedwars map.
 * @param name the displayed map name
 * @param worldFile the path to the Minecraft world file (Anvil format)
 * @param globalSpawn the global map spawn position
 * @param beds the bed position for each team
 * @param spawns the spawn position for each team
 */
public record BedwarsMap(@NotNull String name,
                         @NotNull Path worldFile,
                         @NotNull Pos globalSpawn,
                         @NotNull Map<Team, Team.BedPosition> beds,
                         @NotNull Map<Team, Pos> spawns) {
}
