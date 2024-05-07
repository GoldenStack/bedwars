package dev.goldenstack.bedwars;

import dev.goldenstack.bedwars.game.Game;
import dev.goldenstack.bedwars.map.BedwarsMaps;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerRespawnEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class Main {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        var game = new Game(BedwarsMaps.LIGHTHOUSE);

        MinecraftServer.getGlobalEventHandler()
                .addListener(AsyncPlayerConfigurationEvent.class, event -> {
                    event.setSpawningInstance(game.getInstance());
                    game.handlePlayerJoin(event.getPlayer());
                })
                .addListener(PlayerSpawnEvent.class, event -> {
                    game.handlePlayerSpawn(event.getPlayer());
                    game.handlePlayerAdd(event.getPlayer());
                })
                .addListener(PlayerRespawnEvent.class, event -> game.handlePlayerSpawn(event.getPlayer()));

        minecraftServer.start("0.0.0.0", 25565);
    }
}