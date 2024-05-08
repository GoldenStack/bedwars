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
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Contains information about shop items.
 */
public class ShopItems {

    public static final @NotNull Tag<Boolean> SHOP_INVENTORY = Tag.Boolean("ShopInventory");

    public static final @NotNull Tag<ShopItem> ITEM_ID = Tag.String("ShopItemId").map(ShopItems::nameToItem, ShopItem::name);

    public static final @NotNull Tag<ShopItem.Tab> TAB_ID = Tag.String("ShopTabId").map(ShopItems::nameToTab, ShopItem.Tab::name);

    public static final @NotNull List<ShopItem.Tab> TABS = List.of(
            new ShopItem.Tab(
                    "blocks",
                    ItemStack.of(Material.TERRACOTTA).with(ItemComponent.ITEM_NAME, Component.text("Blocks", NamedTextColor.GREEN)),
                    List.of(
                            ShopItem.team("wool", team -> ItemStack.of(Teams.data(team).wool(), 16), ItemStack.of(Material.IRON_INGOT, 4)),
                            ShopItem.team("concrete", team -> ItemStack.of(Teams.data(team).concrete(), 16), ItemStack.of(Material.IRON_INGOT, 12)),
                            ShopItem.constant("wood", ItemStack.of(Material.OAK_PLANKS, 16), ItemStack.of(Material.GOLD_INGOT, 4)),
                            ShopItem.constant("end_stone", ItemStack.of(Material.END_STONE, 12), ItemStack.of(Material.IRON_INGOT, 24)),
                            ShopItem.team("glass", team -> ItemStack.of(Teams.data(team).glass(), 4), ItemStack.of(Material.IRON_INGOT, 12)),
                            ShopItem.constant("ladder", ItemStack.of(Material.LADDER, 16), ItemStack.of(Material.IRON_INGOT, 12)),
                            ShopItem.constant("obsidian", ItemStack.of(Material.OBSIDIAN, 4), ItemStack.of(Material.EMERALD, 4))
                    )
            ),
            new ShopItem.Tab(
                    "melee",
                    ItemStack.of(Material.GOLDEN_SWORD).with(ItemComponent.ITEM_NAME, Component.text("Melee", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                            ShopItem.constant("stone_sword", ItemStack.of(Material.STONE_SWORD), ItemStack.of(Material.IRON_INGOT, 10)),
                            ShopItem.constant("iron_sword", ItemStack.of(Material.IRON_SWORD), ItemStack.of(Material.GOLD_INGOT, 7)),
                            ShopItem.constant("diamond_sword", ItemStack.of(Material.DIAMOND_SWORD), ItemStack.of(Material.EMERALD, 4)),
                            ShopItem.constant("knockback_stick", ItemStack.of(Material.STICK).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.KNOCKBACK, 1)), ItemStack.of(Material.GOLD_INGOT, 5))
                    )
            ),
            new ShopItem.Tab(
                    "armor",
                    ItemStack.of(Material.CHAINMAIL_BOOTS).with(ItemComponent.ITEM_NAME, Component.text("Armor", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                    )
            ),
            new ShopItem.Tab(
                    "tools",
                    ItemStack.of(Material.STONE_PICKAXE).with(ItemComponent.ITEM_NAME, Component.text("Tools", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                    )
            ),
            new ShopItem.Tab(
                    "ranged",
                    ItemStack.of(Material.BOW).with(ItemComponent.ITEM_NAME, Component.text("Ranged", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                    )
            ),
            new ShopItem.Tab(
                    "potions",
                    ItemStack.of(Material.BREWING_STAND).with(ItemComponent.ITEM_NAME, Component.text("Potions", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                    )
            ),
            new ShopItem.Tab(
                    "utility",
                    ItemStack.of(Material.TNT).with(ItemComponent.ITEM_NAME, Component.text("Utility", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                    )
            )
    );

    /**
     * Maps a name into an (optional) shop item.
     */
    public static @Nullable ShopItem nameToItem(@NotNull String name) {
        for (var tab : TABS) {
            for (var item : tab.items()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }

        return null;
    }

    /**
     * Maps a name into an (optional) shop tab.
     */
    public static @Nullable ShopItem.Tab nameToTab(@NotNull String name) {
        for (var tab : TABS) {
            if (tab.name().equals(name)) {
                return tab;
            }
        }

        return null;
    }

    /**
     * Renders the shop inventory for a given player.
     */
    public static @NotNull ContainerInventory renderInventory(@NotNull ShopItem.Tab tab, @NotNull Player player) {
        var shop = new ContainerInventory(InventoryType.CHEST_6_ROW, "Shopkeeper");

        shop.setTag(SHOP_INVENTORY, true);

        ItemStack empty = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .with(ItemComponent.HIDE_TOOLTIP, Unit.INSTANCE);

        // Top and bottom
        for (int i = 0; i < 9; i++) {
            shop.setItemStack(i, empty);
            shop.setItemStack(9 + i, empty);
            shop.setItemStack(45 + i, empty);
        }

        // Left and right
        for (int i = 0; i < 6; i++) {
            shop.setItemStack(i * 9, empty);
            shop.setItemStack(i * 9 + 8, empty);
        }

        Component lore = Component.text("Click to view!", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);

        for (int i = 0; i < TABS.size(); i++) {
            ShopItem.Tab current = TABS.get(i);

            ItemStack item = current.icon()
                    .with(ItemComponent.LORE, List.of(lore))
                    .withTag(TAB_ID, current);

            if (tab.name().equals(current.name())) {
                shop.setItemStack(i + 1 + 9,
                        ItemStack.of(Material.GREEN_STAINED_GLASS_PANE)
                        .with(ItemComponent.HIDE_TOOLTIP, Unit.INSTANCE)
                );
            }

            shop.setItemStack(i + 1, item);
        }

        for (var item : tab.items()) {
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
