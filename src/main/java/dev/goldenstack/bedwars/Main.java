package dev.goldenstack.bedwars;

import dev.goldenstack.bedwars.map.BedwarsMaps;
import dev.goldenstack.bedwars.team.Team;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.utils.time.Tick;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        var map = BedwarsMaps.LIGHTHOUSE;

        var instance = BedwarsMaps.loadMap(map);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(map.globalSpawn());
            player.setGameMode(GameMode.CREATIVE);

            var team = Team.values()[ThreadLocalRandom.current().nextInt(Team.values().length)];
            MinecraftServer.getSchedulerManager().buildTask(() -> player.sendMessage("Team: " + team)).delay(20, Tick.SERVER_TICKS).schedule();
            player.setRespawnPoint(map.spawns().get(team));
        });

        minecraftServer.start("0.0.0.0", 25565);
    }
}