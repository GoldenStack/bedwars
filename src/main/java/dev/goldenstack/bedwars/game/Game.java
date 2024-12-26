package dev.goldenstack.bedwars.game;

import dev.goldenstack.bedwars.map.BedwarsMap;
import dev.goldenstack.bedwars.map.BedwarsMaps;
import dev.goldenstack.bedwars.shop.ShopItem;
import dev.goldenstack.bedwars.shop.ShopItems;
import dev.goldenstack.bedwars.shop.Shopkeeper;
import dev.goldenstack.bedwars.team.Team;
import dev.goldenstack.bedwars.team.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.DyedItemColor;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A Bedwars game.
 */
@SuppressWarnings("UnstableApiUsage")
public class Game {

    private static final @NotNull Map<UUID, Game> GAMES = new ConcurrentHashMap<>();

    /**
     * The Game tag. Serialized with UUIDs.
     */
    public static final @NotNull Tag<Game> GAME = Tag.UUID("Game").map(GAMES::get, Game::getUuid);

    public enum State {
        LOBBY,
        START,
        END
    }

    public static final @NotNull PlayerSkin SKIN = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTcxNTEwNjQzMjA3OSwKICAicHJvZmlsZUlkIiA6ICI3MGNkYjNiZjhhN2E0ODYxYWY0ZWEzY2U1MDcwY2ViOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lc3RvbSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kZmQyMDllOWU5NjBmODA0OWQ3ZWFmNDUyZmM4NWJmMGJlY2Y5YjE1MGI0NjMyMDE2NjI4NWM3YWIxMzIzMTdlIgogICAgfSwKICAgICJDQVBFIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMzQwYzBlMDNkZDI0YTExYjE1YThiMzNjMmE3ZTllMzJhYmIyMDUxYjI0ODFkMGJhN2RlZmQ2MzVjYTdhOTMzIgogICAgfQogIH0KfQ==",
            "jRs7ZqkawRByf3031AFZ1Sso3KKeD5DDMlxI2yb7by+sjVe243+O+KR3VDokn8f5ubuPzjqSVzJEk5kTmFoaTcPBTEpMHRXI+GkBUQmjW94D7V+zq2tvKxeELQYM6B7ZpHgXZBpMSk/42QAH3YKTGehDyZNVdbwtBqO684e3I1VQCl81o59xSdY4JQcnZCRCi/xEhtNKgoC+j43PSQ1JHqpXjD95vWZRWhXM8QbHmL2F9SiYnvnekuEPtrTrixnFjlq8sL0MaKMQm/pNBytwrJDj1oVV0qnzFMl6iy6khFXFKIfXpV7tjG9HyJhoBv1MHbzkMK0USlolPpeIK7Ni6DQxR6VfhQ6I/jlTNV9j1gJgAM5VSsRpR5b54+JPuLNz+dXO5G+LzWh0vjhSfpKZ6voDYkhToEXOeyZOobHXx2VEWJJP4/w9EbxtAlVC11XBxWEyPW20IWCgB5bdQaTGzOqNfRxytNREAUYBTyOReLOayXhnLNgx3a9u9/GqLng2hAWjruUEeXkGntG4IMwus4pMsM2pfVFpjJafJtaIBYR/RhXzpB2u6EyopO+tyK+uDWtvo2NxC8eKOeJdjrwTLUvQG7mJo0RLYFCUUEMiuvystCyVMPPzYVonZr1QHwpdgpORWbcbh60OqAAjwLMfH4MF1/k5M3E7AOqbaV3plz8="
    );

    private final @NotNull UUID uuid;
    private final @NotNull BedwarsMap map;
    private final @NotNull Instance instance;

    private final @NotNull Set<Team> aliveTeams;

    private final @NotNull Map<Team, Set<UUID>> teams;

    private State state;

    public Game(@NotNull BedwarsMap map) {
        this.uuid = UUID.randomUUID();
        this.map = map;
        this.instance = BedwarsMaps.loadMap(map);

        this.aliveTeams = new HashSet<>(Set.of(Team.values()));

        this.teams = new HashMap<>();
        aliveTeams.forEach(team -> teams.put(team, new HashSet<>()));

        GAMES.put(uuid, this);
        instance.setTag(GAME, this);

        this.state = State.LOBBY;

        registerEvents();

        for (var position : map.shopkeepers()) {
            Shopkeeper shopkeeper = new Shopkeeper("Shopkeeper", SKIN, false,
                    player -> player.openInventory(ShopItems.renderInventory(ShopItems.TABS.getFirst(), player)));
            shopkeeper.setInstance(instance, position);
        }
    }

    public void handlePlayerJoin(@NotNull Player player) {
        player.setTag(GAME, this);
        player.setRespawnPoint(getMap().globalSpawn());
    }

    public void handlePlayerAdd(@NotNull Player player) {
        Set<Player> players = instance.getPlayers();

        if (!players.isEmpty() && state == State.LOBBY) {
            this.state = State.START;
            for (Player user : players) {
                // Results in incorrect team distribution
                Team team = Team.values()[ThreadLocalRandom.current().nextInt(Team.values().length)];

                user.setTag(Teams.TEAM, team);
                teams.get(team).add(user.getUuid());

                player.setRespawnPoint(map.spawns().get(team));
                player.teleport(map.spawns().get(team));
                handlePlayerSpawn(player);
            }
        }
    }

    /**
     * Handles a player spawning in the world (both on an instance change and on respawn)
     *
     * @param player the player to spawn
     */
    public void handlePlayerSpawn(@NotNull Player player) {
        Team team = player.getTag(Teams.TEAM);
        if (team == null) return;

        player.sendPacket(new ChangeGameStatePacket(ChangeGameStatePacket.Reason.ENABLE_RESPAWN_SCREEN, 1));

        player.getInventory().clear();

        boolean teamAlive = getAliveTeams().contains(team);

        player.setGameMode(teamAlive ? GameMode.SURVIVAL : GameMode.SPECTATOR);

        Map<String, Integer> itemTiers = new HashMap<>(player.getTag(ShopItems.ITEM_TIERS));

        // Reduce tiers
        for (var reduced : List.of("axe", "pickaxe")) {
            itemTiers.computeIfPresent(reduced, (name, tier) -> Math.max(0, tier - 1));
        }
        player.setTag(ShopItems.ITEM_TIERS, Map.copyOf(itemTiers));

        // Tools

        if (itemTiers.getOrDefault("shears", 0) == 1) {
            player.getInventory().addItemStack(ItemStack.of(Material.SHEARS));
        }

        int axe = itemTiers.getOrDefault("axe", 0);
        if (axe > 0) {
            ShopItems.AXES.get(axe - 1).boughtTrigger().accept(player);
        }

        int pickaxe = itemTiers.getOrDefault("pickaxe", 0);
        if (pickaxe > 0) {
            ShopItems.PICKAXES.get(axe - 1).boughtTrigger().accept(player);
        }

        // Apply armor
        DyedItemColor color = new DyedItemColor(team.data().color());

        player.setHelmet(ItemStack.of(Material.LEATHER_HELMET).with(ItemComponent.DYED_COLOR, color));
        player.setChestplate(ItemStack.of(Material.LEATHER_CHESTPLATE).with(ItemComponent.DYED_COLOR, color));

        switch (player.getTag(ShopItems.ARMOR)) {
            case "leather" -> {
                player.setLeggings(ItemStack.of(Material.LEATHER_LEGGINGS).with(ItemComponent.DYED_COLOR, color));
                player.setBoots(ItemStack.of(Material.LEATHER_BOOTS).with(ItemComponent.DYED_COLOR, color));
            }
            case "chainmail" -> ShopItems.nameToItem("chainmail_armor").getter().apply(player).boughtTrigger().accept(player);
            case "iron" -> ShopItems.nameToItem("iron_armor").getter().apply(player).boughtTrigger().accept(player);
            case "diamond" -> ShopItems.nameToItem("diamond_armor").getter().apply(player).boughtTrigger().accept(player);
        }

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
                        Component.text(team.data().name() + " Team's bed", team.data().color()),
                        Component.text(" was destroyed by "),
                        Component.text(playerTeam.data().name() + " Team", playerTeam.data().color())
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
        }).addListener(InstanceTickEvent.class, event -> {
            long age = instance.getWorldAge();

            for (var generator : map.generators()) {
                switch (generator.type()) {
                    case ISLAND -> {
                        if (age % (20 * 1) == 0)
                            new ItemEntity(ItemStack.of(Material.IRON_INGOT)).setInstance(instance, generator.pos());
                        if (age % (20 * 5) == 0)
                            new ItemEntity(ItemStack.of(Material.GOLD_INGOT)).setInstance(instance, generator.pos());
                    }
                    case DIAMOND -> {
                        if (age % (20 * 15) == 0)
                            new ItemEntity(ItemStack.of(Material.DIAMOND)).setInstance(instance, generator.pos());
                    }
                    case EMERALD -> {
                        if (age % (20 * 60) == 0)
                            new ItemEntity(ItemStack.of(Material.EMERALD)).setInstance(instance, generator.pos());
                    }
                }
            }
        }).addListener(PickupItemEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                ItemStack result = player.getInventory().addItemStack(event.getItemStack(), TransactionOption.ALL);
                if (!result.isAir()) {
                    event.setCancelled(true);
                    event.getItemEntity().setItemStack(result);
                }
            }
        }).addListener(ItemDropEvent.class, event -> {
            ItemEntity itemEntity = new ItemEntity(event.getItemStack());
            itemEntity.setPickupDelay(Duration.of(10, TimeUnit.SERVER_TICK));
            itemEntity.setVelocity(event.getPlayer().getPosition().direction().mul(6));
            itemEntity.setInstance(event.getPlayer().getInstance(), event.getPlayer().getPosition().add(0, event.getPlayer().getEyeHeight() * 0.75, 0));
        });

        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, event -> {
            AbstractInventory inventory = event.getInventory();
            Player player = event.getPlayer();

            // Check that this is actually a shop click
            if (inventory == null || !inventory.hasTag(ShopItems.SHOP_INVENTORY)) return;
            if (player.getTag(GAME) != this) return;

            event.setCancelled(true);

            ClickType type = event.getClickType();
            boolean acceptedClick = type == ClickType.LEFT_CLICK || type == ClickType.RIGHT_CLICK || type == ClickType.SHIFT_CLICK;
            int slot = event.getSlot();

            // Limit the click to only slots within the shop
            if (!acceptedClick || slot >= inventory.getSize()) return;

            ShopItem.Generator item = inventory.getItemStack(slot).getTag(ShopItems.ITEM_ID);
            if (item == null) {
                ShopItem.Tab tab = inventory.getItemStack(slot).getTag(ShopItems.TAB_ID);
                if (tab != null) {
                    player.openInventory(ShopItems.renderInventory(tab, player));
                }

                return;
            }

            ShopItem shop = item.getter().apply(player);
            if (shop == null) return;

            if (player.getInventory().takeItemStack(shop.cost(), TransactionOption.ALL_OR_NOTHING)) {
                shop.boughtTrigger().accept(player);
            }

            // Update inventory by reopening
            player.openInventory(ShopItems.renderInventory(inventory.getTag(ShopItems.TAB_ID), player));
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
