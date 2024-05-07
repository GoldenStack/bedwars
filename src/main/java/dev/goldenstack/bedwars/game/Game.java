package dev.goldenstack.bedwars.game;

import dev.goldenstack.bedwars.map.BedwarsMap;
import dev.goldenstack.bedwars.map.BedwarsMaps;
import dev.goldenstack.bedwars.team.Team;
import dev.goldenstack.bedwars.team.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Bedwars game.
 */
public class Game {

    private static final @NotNull Map<UUID, Game> GAMES = new ConcurrentHashMap<>();

    /**
     * The Game tag. Serialized with UUIDs.
     */
    public static final @NotNull Tag<Game> GAME = Tag.UUID("Game").map(GAMES::get, Game::getUuid);

    private final @NotNull UUID uuid;
    private final @NotNull BedwarsMap map;
    private final @NotNull Instance instance;

    private final @NotNull Set<Team> aliveTeams;

    public Game(@NotNull BedwarsMap map) {
        this.uuid = UUID.randomUUID();
        this.map = map;
        this.instance = BedwarsMaps.loadMap(map);

        this.aliveTeams = new HashSet<>(Set.of(Team.values()));

        GAMES.put(uuid, this);
        instance.setTag(GAME, this);

        registerEvents();
    }

    /**
     * Handles a player spawning in the world (both on an instance change and on respawn)
     * @param player the player to spawn
     */
    public void handlePlayerSpawn(@NotNull Player player) {
        Team team = player.getTag(Teams.TEAM);
        if (team == null) return;

        player.sendPacket(new ChangeGameStatePacket(ChangeGameStatePacket.Reason.ENABLE_RESPAWN_SCREEN, 1));

        player.getInventory().clear();
        player.getInventory().addItemStack(ItemStack.of(Teams.getWool(player.getTag(Teams.TEAM)), 32));

        boolean teamAlive = getAliveTeams().contains(team);

        player.setGameMode(teamAlive ? GameMode.SURVIVAL : GameMode.SPECTATOR);
    }

    /**
     * Registers events for this game.
     */
    public void registerEvents() {
        instance.eventNode().addListener(PlayerBlockBreakEvent.class, event -> {
            Block block = event.getBlock();
            Player player = event.getPlayer();

            Team team = block.getTag(Teams.TEAM);
            Team playerTeam = player.getTag(Teams.TEAM);
            if (team == null || playerTeam == null) {
                event.setCancelled(true);
                return;
            }

            boolean bed = block.hasTag(Teams.IS_BED);

            if (bed) {
                if (team == playerTeam) {
                    player.sendMessage(Component.text("You cannot break your own bed!", NamedTextColor.RED));
                    event.setCancelled(true);
                    return;
                }

                Direction direction = map.beds().get(team).direction();
                Point offset = new Pos(direction.normalX(), direction.normalY(), direction.normalZ());

                if ("foot".equals(block.getProperty("part"))) {
                    instance.setBlock(event.getBlockPosition().add(offset), Block.AIR);
                } else if ("head".equals(block.getProperty("part"))) {
                    instance.setBlock(event.getBlockPosition().sub(offset), Block.AIR);
                }

                instance.sendMessage(Component.textOfChildren(
                        Component.text(Teams.getName(team) + " Team", Teams.getColor(team)),
                        Component.text("'s bed was destroyed by "),
                        Component.text(Teams.getName(playerTeam) + " Team", Teams.getColor(playerTeam))
                ));

                aliveTeams.remove(team);
            }
        }).addListener(PlayerBlockPlaceEvent.class, event -> {
            Team team = event.getPlayer().getTag(Teams.TEAM);
            event.setBlock(event.getBlock().withTag(Teams.TEAM, team));
        }).addListener(PlayerMoveEvent.class, event -> {
            if (event.getNewPosition().y() <= 0) {
                event.getPlayer().damage(DamageType.OUT_OF_WORLD, Float.MAX_VALUE);
            }
        });
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull BedwarsMap getMap() {
        return map;
    }

    public @NotNull Instance getInstance() {
        return instance;
    }

    public @NotNull Set<Team> getAliveTeams() {
        return aliveTeams;
    }
}
