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
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Contains information about shop items.
 */
public class ShopItems {

    public static final @NotNull Tag<Boolean> SHOP_INVENTORY = Tag.Boolean("ShopInventory");

    public static final @NotNull Tag<ShopItem.Generator> ITEM_ID = Tag.String("ShopItemId").map(ShopItems::nameToItem, ShopItem.Generator::name);

    public static final @NotNull Tag<ShopItem.Tab> TAB_ID = Tag.String("ShopTabId").map(ShopItems::nameToTab, ShopItem.Tab::name);

    public static final @NotNull Tag<Map<String, Integer>> ITEM_TIERS = Tag.<Map<String, Integer>>Transient("ItemTiers").defaultValue(Map::of);

    public static final @NotNull Tag<String> ARMOR = Tag.String("Armor").defaultValue("leather");

    public static final @NotNull List<ShopItem> AXES = List.of(
            new ShopItem(ItemStack.of(Material.WOODEN_AXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 1)), ItemStack.of(Material.IRON_INGOT, 10)),
            new ShopItem(ItemStack.of(Material.STONE_AXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 1)), ItemStack.of(Material.IRON_INGOT, 10)),
            new ShopItem(ItemStack.of(Material.IRON_AXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 2)), ItemStack.of(Material.GOLD_INGOT, 3)),
            new ShopItem(ItemStack.of(Material.DIAMOND_AXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 3)), ItemStack.of(Material.GOLD_INGOT, 6))
    );

    public static final @NotNull List<ShopItem> PICKAXES = List.of(
            new ShopItem(ItemStack.of(Material.WOODEN_PICKAXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 1)), ItemStack.of(Material.IRON_INGOT, 10)),
            new ShopItem(ItemStack.of(Material.IRON_PICKAXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 2)), ItemStack.of(Material.IRON_INGOT, 10)),
            new ShopItem(ItemStack.of(Material.GOLDEN_PICKAXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 3).with(Enchantment.SHARPNESS, 2)), ItemStack.of(Material.GOLD_INGOT, 3)),
            new ShopItem(ItemStack.of(Material.DIAMOND_PICKAXE).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.EFFICIENCY, 3)), ItemStack.of(Material.GOLD_INGOT, 6))
    );

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
                            ShopItem.once("chainmail_armor", new ShopItem(
                                    ItemStack.of(Material.CHAINMAIL_BOOTS).with(ItemComponent.ITEM_NAME, Component.text("Permanent Chainmail Armor")),
                                    ItemStack.of(Material.IRON_INGOT, 30),
                                    player -> {
                                        if (!player.getTag(ARMOR).equals("leather")) return;

                                        player.setLeggings(ItemStack.of(Material.CHAINMAIL_LEGGINGS));
                                        player.setBoots(ItemStack.of(Material.CHAINMAIL_BOOTS));
                                        player.setTag(ARMOR, "chainmail");
                                    }
                            )),
                            ShopItem.once("iron_armor", new ShopItem(
                                    ItemStack.of(Material.IRON_BOOTS).with(ItemComponent.ITEM_NAME, Component.text("Permanent Iron Armor")),
                                    ItemStack.of(Material.GOLD_INGOT, 12),
                                    player -> {
                                        if (player.getTag(ARMOR).equals("diamond")) return;

                                        player.setLeggings(ItemStack.of(Material.IRON_LEGGINGS));
                                        player.setBoots(ItemStack.of(Material.IRON_BOOTS));
                                        player.setTag(ARMOR, "iron");
                                    }
                            )),
                            ShopItem.once("diamond_armor", new ShopItem(
                                    ItemStack.of(Material.DIAMOND_BOOTS).with(ItemComponent.ITEM_NAME, Component.text("Permanent Diamond Armor")),
                                    ItemStack.of(Material.EMERALD, 6),
                                    player -> {
                                        player.setLeggings(ItemStack.of(Material.DIAMOND_LEGGINGS));
                                        player.setBoots(ItemStack.of(Material.DIAMOND_BOOTS));
                                        player.setTag(ARMOR, "diamond");
                                    }
                            ))
                    )
            ),
            new ShopItem.Tab(
                    "tools",
                    ItemStack.of(Material.STONE_PICKAXE).with(ItemComponent.ITEM_NAME, Component.text("Tools", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                            ShopItem.once("shears", new ShopItem(ItemStack.of(Material.SHEARS), ItemStack.of(Material.IRON_INGOT, 20))),
                            ShopItem.tiered("axe", AXES),
                            ShopItem.tiered("pickaxe", PICKAXES)
                    )
            ),
            new ShopItem.Tab(
                    "ranged",
                    ItemStack.of(Material.BOW).with(ItemComponent.ITEM_NAME, Component.text("Ranged", NamedTextColor.GREEN)).without(ItemComponent.ATTRIBUTE_MODIFIERS),
                    List.of(
                            ShopItem.constant("arrows", ItemStack.of(Material.ARROW, 6), ItemStack.of(Material.GOLD_INGOT, 2)),
                            ShopItem.constant("bow", ItemStack.of(Material.BOW, 1), ItemStack.of(Material.GOLD_INGOT, 12)),
                            ShopItem.constant("power_bow", ItemStack.of(Material.BOW, 1).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.POWER, 1)), ItemStack.of(Material.GOLD_INGOT, 20)),
                            ShopItem.constant("power_punch_bow", ItemStack.of(Material.BOW, 1).with(ItemComponent.ENCHANTMENTS, EnchantmentList.EMPTY.with(Enchantment.POWER, 1).with(Enchantment.PUNCH, 1)), ItemStack.of(Material.EMERALD, 6))
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
                            ShopItem.constant("iron", ItemStack.of(Material.IRON_INGOT, 512), ItemStack.of(Material.AIR)),
                            ShopItem.constant("gold", ItemStack.of(Material.GOLD_INGOT, 512), ItemStack.of(Material.AIR)),
                            ShopItem.constant("diamond", ItemStack.of(Material.DIAMOND, 512), ItemStack.of(Material.AIR)),
                            ShopItem.constant("emerald", ItemStack.of(Material.EMERALD, 512), ItemStack.of(Material.AIR))
                    )
            )
    );

    /**
     * Maps a name into an (optional) shop item.
     */
    public static @Nullable ShopItem.Generator nameToItem(@NotNull String name) {
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
        shop.setTag(TAB_ID, tab);

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
            ShopItem render = item.getter().apply(player);
            if (render != null) {
                shop.addItemStack(ShopItems.render(render).withTag(ITEM_ID, item));
            }
        }

        return shop;
    }

    /**
     * Renders a shop item into an ItemStack.
     */
    public static @NotNull ItemStack render(@NotNull ShopItem item) {
        return item.item().with(ItemComponent.LORE, (UnaryOperator<List<Component>>) lore -> {
            lore = new ArrayList<>(lore);
            lore.add(Component.empty());

            Material material = item.cost().material();
            String amount = item.cost().amount() + " ";
            Component cost;

            if (material == Material.IRON_INGOT) {
                cost = Component.text(amount + "iron", NamedTextColor.WHITE);
            } else if (material == Material.GOLD_INGOT) {
                cost = Component.text(amount + "gold", NamedTextColor.GOLD);
            } else if (material == Material.DIAMOND) {
                cost = Component.text(amount + "diamonds", NamedTextColor.AQUA);
            } else if (material == Material.EMERALD) {
                cost = Component.text(amount + "emeralds", NamedTextColor.GREEN);
            } else if (material == Material.BARRIER) {
                cost = Component.text("This item cannot be bought!", NamedTextColor.RED);
            } else if (material.isBlock()) {
                cost = Component.text(amount, NamedTextColor.GRAY).append(Component.translatable("block.minecraft." + item.cost().material().block().namespace().path()));
            } else if (material == Material.AIR) {
                cost = Component.text("Free!", NamedTextColor.GREEN);
            } else {
                cost = Component.text(amount, NamedTextColor.GRAY).append(Component.translatable("item.minecraft." + item.cost().material().namespace().path()));
            }

            lore.add(Component.text("Cost: ").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY).append(cost));
            return lore;
        });
    }

}
