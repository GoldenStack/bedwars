package dev.goldenstack.bedwars.map;

import dev.goldenstack.bedwars.team.Team;
import dev.goldenstack.bedwars.team.Teams;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;

/**
 * Bedwars map constants and util functions.
 */
public final class BedwarsMaps {

    private BedwarsMaps() {}

    /**
     * Creates an instance for a map.
     * @param map the map to load
     * @return the instance containing the map, relevant beds, etc.
     */
    public static @NotNull Instance loadMap(@NotNull BedwarsMap map) {
        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();

        instance.setChunkSupplier(LightingChunk::new);
        instance.setTimeRate(0);
        instance.setTime(6000);

        instance.enableAutoChunkLoad(true);

        instance.setChunkLoader(new AnvilLoader(map.worldFile()));

        for (var entry : map.beds().entrySet()) {
            Teams.placeBed(instance, entry.getKey(), entry.getValue());
        }

        return instance;
    }

    /**
     * The example map - Lighthouse.
     */
    public static final @NotNull BedwarsMap LIGHTHOUSE = new BedwarsMap(
            "Lighthouse",
            Path.of("maps", "Lighthouse"),
            new Pos(0.5, 118, 0.5, 0, 0),
            Map.of(
                    Team.RED, new Team.BedPosition(new Vec(-23, 66, -75), Direction.NORTH),
                    Team.BLUE, new Team.BedPosition(new Vec(29, 66, -75), Direction.NORTH),
                    Team.GREEN, new Team.BedPosition(new Vec(78, 66, -26), Direction.EAST),
                    Team.YELLOW, new Team.BedPosition(new Vec(78, 66, 26), Direction.EAST),
                    Team.CYAN, new Team.BedPosition(new Vec(29, 66, 75), Direction.SOUTH),
                    Team.WHITE, new Team.BedPosition(new Vec(-23, 66, 75), Direction.SOUTH),
                    Team.PINK, new Team.BedPosition(new Vec(-72, 66, 26), Direction.WEST),
                    Team.GRAY, new Team.BedPosition(new Vec(-72, 66, -26), Direction.WEST)
            ),
            Map.of(
                    Team.RED, new Pos(-22.5, 65, -87.5, 0, 0),
                    Team.BLUE, new Pos(29.5, 65, -87.5, 0, 0),
                    Team.GREEN, new Pos(91.5, 65, -25.5, 90, 0),
                    Team.YELLOW, new Pos(91.5, 65, 26.5, 90, 0),
                    Team.CYAN, new Pos(29.5, 65, 88.5, 180, 0),
                    Team.WHITE, new Pos(-22.5, 65, 88.5, 180, 0),
                    Team.PINK, new Pos(-84.5, 65, 26.5, 270, 0),
                    Team.GRAY, new Pos(-84.5, 65, -25.5, 270, 0)
            )
    );

}
