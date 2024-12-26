package dev.goldenstack.bedwars.shop;

import dev.goldenstack.bedwars.team.Team;
import dev.goldenstack.bedwars.team.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An item that can be bought from the Shopkeeper.
 *
 * @param item the item getter (player-based, team-based, or static)
 * @param cost the cost of the item (using the item count and material)
 */
public record ShopItem(@NotNull ItemStack item, @NotNull ItemStack cost, @NotNull Consumer<Player> boughtTrigger) {

    public ShopItem(@NotNull ItemStack item, @NotNull ItemStack cost) {
        this(item, cost, player -> {
            ItemStack remaining = player.getInventory().addItemStack(item, TransactionOption.ALL);

            if (!remaining.isAir()) {
                player.dropItem(remaining);
            }
        });
    }

    public record Generator(@NotNull String name, @NotNull Function<@NotNull Player, @Nullable ShopItem> getter) {

    }

    public static @NotNull ShopItem.Generator once(@NotNull String name, @NotNull ShopItem item) {
        return new Generator(name, player -> {
            Map<String, Integer> tiers = player.getTag(ShopItems.ITEM_TIERS);
            int currentTier = tiers.getOrDefault(name, 0);

            if (currentTier == 1) {
                return new ShopItem(
                        ItemStack.of(Material.BARRIER).with(ItemComponent.ITEM_NAME, Component.text("You have already bought this item!", NamedTextColor.RED)),
                        ItemStack.of(Material.BARRIER),
                        item.boughtTrigger()
                );
            }

            return new ShopItem(item.item(), item.cost(), p -> {
                Map<String, Integer> newTiers = new HashMap<>(tiers);
                newTiers.put(name, 1);
                player.setTag(ShopItems.ITEM_TIERS, newTiers);

                item.boughtTrigger().accept(p);
            });
        });
    }

    public static @NotNull ShopItem.Generator tiered(@NotNull String name, @NotNull List<ShopItem> tierItems) {

        return new Generator(name, player -> {
            Map<String, Integer> tiers = player.getTag(ShopItems.ITEM_TIERS);
            int currentTier = tiers.getOrDefault(name, 0);

            if (currentTier == tierItems.size()) {
                return new ShopItem(
                        ItemStack.of(Material.BARRIER).with(ItemComponent.ITEM_NAME, Component.text("You have maxed out this tier!", NamedTextColor.RED)),
                        ItemStack.of(Material.BARRIER)
                );
            }

            var original = tierItems.get(currentTier);
            return new ShopItem(original.item, original.cost, p -> {
                Map<String, Integer> newTiers = new HashMap<>(tiers);
                newTiers.put(name, currentTier + 1);
                player.setTag(ShopItems.ITEM_TIERS, newTiers);

                if (currentTier > 0) {
                    // Take the previous item from the player
                    ItemStack previousItem = tierItems.get(currentTier - 1).item;

                    player.getInventory().takeItemStack(previousItem, TransactionOption.ALL);
                }

                original.boughtTrigger.accept(p);
            });
        });
    }

    /**
     * Creates a shop item that returns an item based on the player's team.
     */
    public static @NotNull ShopItem.Generator team(@NotNull String name, @NotNull Function<Team, ItemStack> item, @NotNull ItemStack cost) {
        return new Generator(name, player -> {
            Team team = player.getTag(Teams.TEAM);
            return team == null ? null : new ShopItem(item.apply(team), cost);
        });
    }

    /**
     * Creates a shop item that always returns the same value.
     */
    public static @NotNull ShopItem.Generator constant(@NotNull String name, @NotNull ItemStack item, @NotNull ItemStack cost) {
        return new Generator(name, player -> new ShopItem(item, cost));
    }

    /**
     * Creates a shop item that always returns the same value, but with a custom item adder.
     */
    public static @NotNull ShopItem.Generator added(@NotNull String name, @NotNull ItemStack item, @NotNull ItemStack cost, @NotNull Consumer<Player> adder) {
        return new Generator(name, player -> new ShopItem(item, cost, adder));
    }

    /**
     * A tab in the GUI.
     * These are different from pages in that they're always visible, and you can directly skip between any two.
     *
     * @param name  the name of the tab (used internally)
     * @param icon  the icon of the tab
     * @param items the shop items contained within the tab
     */
    public record Tab(@NotNull String name, @NotNull ItemStack icon, @NotNull List<ShopItem.Generator> items) {
    }

}
