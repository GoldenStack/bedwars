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
import java.util.List;
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
                    Team.RED, new BedwarsMap.BedPosition(new Vec(-23, 66, -75), Direction.NORTH),
                    Team.BLUE, new BedwarsMap.BedPosition(new Vec(29, 66, -75), Direction.NORTH),
                    Team.GREEN, new BedwarsMap.BedPosition(new Vec(78, 66, -26), Direction.EAST),
                    Team.YELLOW, new BedwarsMap.BedPosition(new Vec(78, 66, 26), Direction.EAST),
                    Team.CYAN, new BedwarsMap.BedPosition(new Vec(29, 66, 75), Direction.SOUTH),
                    Team.WHITE, new BedwarsMap.BedPosition(new Vec(-23, 66, 75), Direction.SOUTH),
                    Team.PINK, new BedwarsMap.BedPosition(new Vec(-72, 66, 26), Direction.WEST),
                    Team.GRAY, new BedwarsMap.BedPosition(new Vec(-72, 66, -26), Direction.WEST)
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
            ),
            List.of(
                    new BedwarsMap.Generator(new Vec(-22.5, 65, -90.5), BedwarsMap.Generator.Type.ISLAND),
                    new BedwarsMap.Generator(new Vec(29.5, 65, -90.5), BedwarsMap.Generator.Type.ISLAND),
                    new BedwarsMap.Generator(new Vec(94.5, 65, -25.5), BedwarsMap.Generator.Type.ISLAND),
                    new BedwarsMap.Generator(new Vec(94.5, 65, 26.5), BedwarsMap.Generator.Type.ISLAND),
                    new BedwarsMap.Generator(new Vec(29.5, 65, 91.5), BedwarsMap.Generator.Type.ISLAND),
                    new BedwarsMap.Generator(new Vec(-22.5, 65, 91.5), BedwarsMap.Generator.Type.ISLAND),
                    new BedwarsMap.Generator(new Vec(-87.5, 65, 26.5), BedwarsMap.Generator.Type.ISLAND),
                    new BedwarsMap.Generator(new Vec(-87.5, 65, -25.5), BedwarsMap.Generator.Type.ISLAND),

                    new BedwarsMap.Generator(new Vec(55.5, 67, -51.5), BedwarsMap.Generator.Type.DIAMOND),
                    new BedwarsMap.Generator(new Vec(55.5, 67, 52.5), BedwarsMap.Generator.Type.DIAMOND),
                    new BedwarsMap.Generator(new Vec(-48.5, 67, 52.5), BedwarsMap.Generator.Type.DIAMOND),
                    new BedwarsMap.Generator(new Vec(-48.5, 67, -51.5), BedwarsMap.Generator.Type.DIAMOND),

                    new BedwarsMap.Generator(new Vec(13.5, 65, -13.5), BedwarsMap.Generator.Type.EMERALD),
                    new BedwarsMap.Generator(new Vec(-6.5, 65, 14.5), BedwarsMap.Generator.Type.EMERALD),
                    new BedwarsMap.Generator(new Vec(3.5, 87, -6.5), BedwarsMap.Generator.Type.EMERALD),
                    new BedwarsMap.Generator(new Vec(3.5, 87, 7.5), BedwarsMap.Generator.Type.EMERALD)
            )
    );

}
