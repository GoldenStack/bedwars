package dev.goldenstack.bedwars.shop;

import dev.goldenstack.bedwars.team.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.ContainerInventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Contains information about shop items.
 */
public class ShopItems {

    public static final @NotNull ShopItem WOOL = ShopItem.team("wool", team -> ItemStack.of(Teams.data(team).wool(), 16), ItemStack.of(Material.IRON_INGOT, 4));

    public static final @NotNull ShopItem CONCRETE = ShopItem.team("concrete", team -> ItemStack.of(Teams.data(team).concrete(), 16), ItemStack.of(Material.IRON_INGOT, 12));

    public static final @NotNull ShopItem WOOD = ShopItem.constant("wood", ItemStack.of(Material.OAK_PLANKS, 16), ItemStack.of(Material.GOLD_INGOT, 4));

    public static final @NotNull ShopItem END_STONE = ShopItem.constant("end_stone", ItemStack.of(Material.END_STONE, 12), ItemStack.of(Material.IRON_INGOT, 24));

    public static final @NotNull ShopItem GLASS = ShopItem.team("glass", team -> ItemStack.of(Teams.data(team).glass(), 4), ItemStack.of(Material.IRON_INGOT, 12));

    public static final @NotNull ShopItem LADDER = ShopItem.constant("ladder", ItemStack.of(Material.LADDER, 16), ItemStack.of(Material.IRON_INGOT, 12));

    public static final @NotNull ShopItem OBSIDIAN = ShopItem.constant("obsidian", ItemStack.of(Material.OBSIDIAN, 4), ItemStack.of(Material.EMERALD, 4));

    public static final @NotNull Tag<Boolean> SHOP_INVENTORY = Tag.Boolean("ShopInventory");

    public static final @NotNull Tag<ShopItem> ITEM_ID = Tag.String("ShopItemId").map(ShopItems::fromName, ShopItem::name);

    public static final @NotNull List<ShopItem> ITEMS = List.of(
            WOOL, CONCRETE, WOOD, END_STONE, GLASS, LADDER, OBSIDIAN
    );

    /**
     * Maps a name into an (optional) shop item.
     */
    public static @Nullable ShopItem fromName(@NotNull String name) {
        for (var item : ITEMS) {
            if (item.name().equals(name)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Renders the shop inventory for a given player.
     */
    public static @NotNull ContainerInventory renderInventory(@NotNull Player player) {
        var shop = new ContainerInventory(InventoryType.CHEST_6_ROW, "Shopkeeper");

        shop.setTag(SHOP_INVENTORY, true);

        ItemStack empty = ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
                .set(ItemComponent.ITEM_NAME, Component.empty())
                .build();

        // Top and bottom
        for (int i = 0; i < 9; i++) {
            shop.setItemStack(i, empty);
            shop.setItemStack(45 + i, empty);
        }

        // Left and right
        for (int i = 0; i < 6; i++) {
            shop.setItemStack(i * 9, empty);
            shop.setItemStack(i * 9 + 8, empty);
        }

        for (var item : ITEMS) {
            shop.addItemStack(ShopItems.render(item, player));
        }

        return shop;
    }

    /**
     * Renders a shop item for the provided player.
     */
    public static @NotNull ItemStack render(@NotNull ShopItem item, @NotNull Player player) {
        return item.item().apply(player).with(ItemComponent.LORE, (UnaryOperator<List<Component>>) lore -> {
            lore = new ArrayList<>(lore);
            lore.add(Component.empty());

            Component cost;
            if (item.cost().material() == Material.IRON_INGOT) {
                cost = Component.text(item.cost().amount() + " iron", NamedTextColor.WHITE);
            } else if (item.cost().material() == Material.GOLD_INGOT) {
                cost = Component.text(item.cost().amount() + " gold", NamedTextColor.GOLD);
            } else if (item.cost().material() == Material.DIAMOND) {
                cost = Component.text(item.cost().amount() + " diamonds", NamedTextColor.AQUA);
            } else if (item.cost().material() == Material.EMERALD) {
                cost = Component.text(item.cost().amount() + " emeralds", NamedTextColor.GREEN);
            } else {
                cost = Component.text(item.cost().amount() + String.valueOf(item.cost().material()), NamedTextColor.GRAY);
            }

            lore.add(Component.text("Cost: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY).append(cost));
            return lore;
        }).withTag(ITEM_ID, item);
    }

}
