package dev.goldenstack.bedwars;

import dev.goldenstack.bedwars.game.Game;
import dev.goldenstack.bedwars.map.BedwarsMaps;
import dev.goldenstack.bedwars.team.Team;
import dev.goldenstack.bedwars.team.Teams;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerRespawnEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        var game = new Game(BedwarsMaps.LIGHTHOUSE);

        MinecraftServer.getGlobalEventHandler()
                .addListener(AsyncPlayerConfigurationEvent.class, event -> {
                    final Player player = event.getPlayer();

                    Team randomTeam = Team.values()[ThreadLocalRandom.current().nextInt(Team.values().length)];

                    event.setSpawningInstance(game.getInstance());
                    player.setTag(Teams.TEAM, randomTeam);
                    player.setRespawnPoint(game.getMap().spawns().get(randomTeam));
                })
                .addListener(PlayerRespawnEvent.class, event -> game.handlePlayerSpawn(event.getPlayer()))
                .addListener(PlayerSpawnEvent.class, event -> game.handlePlayerSpawn(event.getPlayer()));

        minecraftServer.start("0.0.0.0", 25565);
    }
}