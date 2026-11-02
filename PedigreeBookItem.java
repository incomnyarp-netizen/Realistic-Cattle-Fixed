package com.example.realisticcattle.item;

import com.example.realisticcattle.entity.CattleEntity;
import com.example.realisticcattle.network.CattleNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Opens the graphical herdbook for the inspected animal. */
public class PedigreeBookItem extends Item {
    public PedigreeBookItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof CattleEntity cattle)) return InteractionResult.PASS;
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        CattleNetwork.openPedigree(serverPlayer, cattle.getDisplayNameForPedigree(), cattle.getBreed().name(),
                cattle.isPurebred(), cattle.getLineage().ancestryText(), cattle.getLineage().familyTreeText(4),
                cattle.getTraits().describe(), cattle.getGenome().describe());
        return InteractionResult.CONSUME;
    }

}
