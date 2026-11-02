package com.example.realisticcattle.network;

import com.example.realisticcattle.RealisticCattle;
import com.example.realisticcattle.client.screen.PedigreeScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class CattleNetwork {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RealisticCattle.MOD_ID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static int id;
    private CattleNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(id++, OpenPedigreeMessage.class, OpenPedigreeMessage::encode,
                OpenPedigreeMessage::decode, (message, contextSupplier) -> OpenPedigreeMessage.handle(message, contextSupplier));
    }

    public static void openPedigree(net.minecraft.server.level.ServerPlayer player, String name, String breed,
                                    boolean purebred, String ancestry, String tree, String traits, String genome) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new OpenPedigreeMessage(name, breed, purebred, ancestry, tree, traits, genome));
    }

    public record OpenPedigreeMessage(String name, String breed, boolean purebred, String ancestry,
                                      String tree, String traits, String genome) {
        static void encode(OpenPedigreeMessage m, FriendlyByteBuf b) {
            b.writeUtf(m.name, 128); b.writeUtf(m.breed, 64); b.writeBoolean(m.purebred);
            b.writeUtf(m.ancestry, 2048); b.writeUtf(m.tree, 4096); b.writeUtf(m.traits, 2048); b.writeUtf(m.genome, 1024);
        }
        static OpenPedigreeMessage decode(FriendlyByteBuf b) {
            return new OpenPedigreeMessage(b.readUtf(128), b.readUtf(64), b.readBoolean(),
                    b.readUtf(2048), b.readUtf(4096), b.readUtf(2048), b.readUtf(1024));
        }
        static void handle(OpenPedigreeMessage m, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> contextSupplier) {
            net.minecraftforge.network.NetworkEvent.Context ctx = contextSupplier.get();
            ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> net.minecraft.client.Minecraft.getInstance().setScreen(
                            new PedigreeScreen(m.name, m.breed, m.purebred, m.ancestry, m.tree, m.traits, m.genome))));
            ctx.setPacketHandled(true);
        }
    }
}
