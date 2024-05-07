package dev.goldenstack.bedwars.map;

import dev.goldenstack.bedwars.team.Team;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * A bedwars map.
 * @param name the displayed map name
 * @param worldFile the path to the Minecraft world file (Anvil format)
 * @param globalSpawn the global map spawn position
 * @param beds the bed position for each team
 * @param spawns the spawn position for each team
 * @param generators the item generators in the map
 */
public record BedwarsMap(@NotNull String name,
                         @NotNull Path worldFile,
                         @NotNull Pos globalSpawn,
                         @NotNull Map<Team, BedPosition> beds,
                         @NotNull Map<Team, Pos> spawns,
                         @NotNull List<Generator> generators) {
    /**
     * The position of a team's bed.
     * @param point the block of the foot of the bed
     * @param direction the direction from the foot of the bed to the head
     */
    public record BedPosition(@NotNull Point point, @NotNull Direction direction) {}

    /**
     * An item generator.
     * @param pos the position of the generator
     * @param type the type of the generator (indicates which upgrades affect it, etc)
     */
    public record Generator(@NotNull Vec pos, @NotNull Type type) {

        public enum Type {
            ISLAND,
            DIAMOND,
            EMERALD
        }

    }
}
