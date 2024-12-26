package dev.goldenstack.bedwars.shop;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.*;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The shopkeeper entity.
 */
@SuppressWarnings("UnstableApiUsage")
public class Shopkeeper extends LivingEntity {

    // NPCs with `hideName` are added here to hide their names
    private static final Team NPC_TEAM = MinecraftServer.getTeamManager().createBuilder("npc-hidden-name")
            .updateTeamPacket()
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build();

    private final @NotNull EventNode<Event> eventNode;
    private final @NotNull String username;
    private final @Nullable PlayerSkin skin;

    public Shopkeeper(@NotNull String username, @Nullable PlayerSkin skin, boolean hideName, @NotNull Consumer<Player> handler) {
        super(EntityType.PLAYER, UUID.randomUUID());
        this.setNoGravity(true);

        this.eventNode = EventNode.all("npc-interact." + this.getEntityId());
        MinecraftServer.getGlobalEventHandler().addChild(this.eventNode);

        this.username = username;
        this.skin = skin;

        if (hideName) {
            this.setTeam(NPC_TEAM);

            // Add this NPC's username to the team because Minestom doesn't know the client thinks it's a player
            NPC_TEAM.addMember(this.username);
        }

        // Hnadle right (PlayerEntityInteractEvent) and left (EntityAttackEvent) clicks on this NPC
        this.eventNode.addListener(PlayerEntityInteractEvent.class, event -> {
            if (event.getTarget() != this) return;
            if (event.getHand() != PlayerHand.MAIN) return; // event fired for both hands, only process one.

            handler.accept(event.getPlayer());
        }).addListener(EntityAttackEvent.class, event -> {
            if (event.getTarget() != this) return;
            if (!(event.getEntity() instanceof Player player)) return;

            handler.accept(player);
        });
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        // Add the "textures" property if there's a skin
        List<PlayerInfoUpdatePacket.Property> properties = this.skin == null ? List.of() :
                List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature()));

        // Create the entry to 'register' this player for the client
        var entry = new PlayerInfoUpdatePacket.Entry(super.getUuid(), username, properties, false, 0, GameMode.SURVIVAL, null, null, 0);
        player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));

        // Spawn the NPC entity itself
        super.updateNewViewer(player);

        // Enable skin layers. This is a simplification of code from PlayerMeta
        player.sendPacket(new EntityMetaDataPacket(getEntityId(), Map.of(17, Metadata.Byte((byte) 127))));
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);

        // Unregister this NPC from the client because players have extra meta
        player.sendPacket(new PlayerInfoRemovePacket(super.getUuid()));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void remove() {
        this.eventNode.getParent().removeChild(this.eventNode);
        super.remove();
    }
}