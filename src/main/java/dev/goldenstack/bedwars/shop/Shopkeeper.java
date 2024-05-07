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

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The shopkeeper entity.
 *
 */
@SuppressWarnings("UnstableApiUsage")
public class Shopkeeper extends LivingEntity {
    private static final Team NPC_TEAM = MinecraftServer.getTeamManager().createBuilder("npc-hidden-name")
            .updateTeamPacket()
            .nameTagVisibility(TeamsPacket.NameTagVisibility.NEVER)
            .build();

    private final @NotNull EventNode<Event> npcEventNode;
    private final @NotNull String username;
    private final @Nullable PlayerSkin skin;
    private final @NotNull Consumer<Player> handler;

    public Shopkeeper(@NotNull String username, @Nullable PlayerSkin skin, boolean hideName, @NotNull Consumer<Player> handler) {
        super(EntityType.PLAYER, UUID.randomUUID());
        this.setNoGravity(true);

        this.npcEventNode = EventNode.all("npc-interact." + this.getEntityId());
        MinecraftServer.getGlobalEventHandler().addChild(this.npcEventNode);

        this.username = username;
        this.skin = skin;

        if (hideName) {
            this.setTeam(NPC_TEAM);
            // add manually to team as Minestom will try adding by the entity UUID. This can't be fixed on the minestom side
            // as it is not aware of the username of the NPC D:
            NPC_TEAM.addMember(this.username);
        }

        this.handler = handler;

        this.npcEventNode.addListener(PlayerEntityInteractEvent.class, event -> {
            if (event.getTarget() != this) return;
            if (event.getHand() != Player.Hand.MAIN) return; // event fired for both hands, only process one.

            handler.accept(event.getPlayer());
        }).addListener(EntityAttackEvent.class, event -> {
            if (event.getTarget() != this) return;
            if (!(event.getEntity() instanceof Player player)) return;

            handler.accept(player);
        });
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        var properties = new ArrayList<PlayerInfoUpdatePacket.Property>();
        if (this.skin != null) {
            properties.add(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature()));
        }
        var entry = new PlayerInfoUpdatePacket.Entry(super.getUuid(), username, properties, false, 0, GameMode.SURVIVAL, null, null);
        player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));

        // Spawn the player entity
        super.updateNewViewer(player);

        // Enable skin layers
        player.sendPackets(new EntityMetaDataPacket(getEntityId(), Map.of(17, Metadata.Byte((byte) 127))));
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);

        player.sendPacket(new PlayerInfoRemovePacket(super.getUuid()));
    }

    @Override
    public void remove() {
        this.npcEventNode.getParent().removeChild(this.npcEventNode);
        super.remove();
    }
}