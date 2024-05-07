package dev.goldenstack.bedwars.shop;

import dev.goldenstack.bedwars.team.Team;
import dev.goldenstack.bedwars.team.Teams;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * An item that can be bought from the Shopkeeper.
 * @param name the name of the item (used internally)
 * @param item the item getter (player-based, team-based, or static)
 * @param cost the cost of the item (using the item count and material)
 */
public record ShopItem(@NotNull String name, @NotNull Function<Player, ItemStack> item, @NotNull ItemStack cost) {

    /**
     * Creates a shop item that returns an item based on the player's team.
     */
    public static @NotNull ShopItem team(@NotNull String name, @NotNull Function<Team, ItemStack> item, @NotNull ItemStack cost) {
        return new ShopItem(name, player -> {
            Team team = player.getTag(Teams.TEAM);
            return team != null ? item.apply(team) : ItemStack.AIR;
        }, cost);
    }

    /**
     * Creates a shop item that always returns the same value.
     */
    public static @NotNull ShopItem constant(@NotNull String name, @NotNull ItemStack item, @NotNull ItemStack cost) {
        return new ShopItem(name, player -> item, cost);
    }

}
