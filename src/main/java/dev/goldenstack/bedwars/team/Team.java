package dev.goldenstack.bedwars.team;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * A bedwars team.
 */
public enum Team {
    RED(new Data("Red", NamedTextColor.RED, Block.RED_BED, Material.RED_WOOL, Material.RED_CONCRETE, Material.RED_STAINED_GLASS)),
    BLUE(new Data("Blue", NamedTextColor.BLUE, Block.BLUE_BED, Material.BLUE_WOOL, Material.BLUE_CONCRETE, Material.BLUE_STAINED_GLASS)),
    GREEN(new Data("Green", NamedTextColor.GREEN, Block.LIME_BED, Material.LIME_WOOL, Material.LIME_CONCRETE, Material.LIME_STAINED_GLASS)),
    YELLOW(new Data("Yellow", NamedTextColor.YELLOW, Block.YELLOW_BED, Material.YELLOW_WOOL, Material.YELLOW_CONCRETE, Material.YELLOW_STAINED_GLASS)),
    CYAN(new Data("Cyan", NamedTextColor.AQUA, Block.CYAN_BED, Material.CYAN_WOOL, Material.CYAN_CONCRETE, Material.CYAN_STAINED_GLASS)),
    WHITE(new Data("White", NamedTextColor.WHITE, Block.WHITE_BED, Material.WHITE_WOOL, Material.WHITE_CONCRETE, Material.WHITE_STAINED_GLASS)),
    PINK(new Data("Pink", NamedTextColor.LIGHT_PURPLE, Block.PINK_BED, Material.PINK_WOOL, Material.PINK_CONCRETE, Material.PINK_STAINED_GLASS)),
    GRAY(new Data("Gray", NamedTextColor.GRAY, Block.GRAY_BED, Material.GRAY_WOOL, Material.GRAY_CONCRETE, Material.GRAY_STAINED_GLASS));

    private final Data data;

    Team(Data data) {
        this.data = data;
    }

    public @NotNull Data data() {
        return this.data;
    }

    public record Data(@NotNull String name, @NotNull TextColor color, @NotNull Block bed,
                       @NotNull Material wool, @NotNull Material concrete, @NotNull Material glass) {
    }
}
