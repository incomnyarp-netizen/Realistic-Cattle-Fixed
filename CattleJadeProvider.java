package com.example.realisticcattle.integration.jade;

import com.example.realisticcattle.entity.CattleEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class CattleJadeProvider implements IWailaPlugin, IEntityComponentProvider {
    public static final CattleJadeProvider INSTANCE = new CattleJadeProvider();
    private static final ResourceLocation UID = new ResourceLocation("realisticcattle", "cattle_info");

    @Override
    public void register(IWailaCommonRegistration registration) { }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(INSTANCE, CattleEntity.class);
    }

    @Override
    public ResourceLocation getUid() { return UID; }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!(accessor.getEntity() instanceof CattleEntity cattle)) return;
        tooltip.add(Component.literal("Порода: " + cattle.getBreed().name()));
        tooltip.add(Component.literal("Племенной статус: " + (cattle.isPurebred() ? "§aЧистокровный" : "§eНе чистокровный")));
        tooltip.add(Component.literal("Родословная: " + cattle.getLineage().ancestryText()));
        tooltip.add(Component.literal("Генотип: " + cattle.getGenome().describe()));
        var traits = cattle.getTraits();
        tooltip.add(Component.literal("Наследуемые признаки: " + traits.describe()));
        tooltip.add(Component.literal("Телосложение: размер " + traits.bodySize() + "/100 · грудь " + traits.chestWidth() + "/100 · ноги " + traits.legLength() + "/100"));
        tooltip.add(Component.literal("Мускулатура: " + traits.muscling() + "/100 · потенциал молочности: " + traits.milkPotential() + "/100"));
    }
}
