package com.example.realisticcattle.item;

import com.example.realisticcattle.entity.CattleEntity;
import com.example.realisticcattle.entity.CattleSex;
import com.example.realisticcattle.genetics.CattleBreed;
import com.example.realisticcattle.genetics.CattleLineage;
import com.example.realisticcattle.genetics.CattleGenome;
import com.example.realisticcattle.genetics.CattleTraits;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

/** Breed-specific egg. All eggs create the same cattle entity but initialize its breed and founder genetics. */
public class CattleBreedSpawnEggItem extends ForgeSpawnEggItem {
    private final CattleBreed breed;

   public CattleBreedSpawnEggItem(java.util.function.Supplier<? extends EntityType<? extends Mob>> type,
                               int background, int spots,
                               Properties properties, CattleBreed breed) {
    super(type, background, spots, properties);
    this.breed = breed;
}

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Vec3 spawnPos = Vec3.atBottomCenterOf(pos);
        if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                || !level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
            return InteractionResult.FAIL;
        }
        CattleEntity cattle = com.example.realisticcattle.registry.ModEntities.CATTLE.get().create(level);
        if (cattle == null) return InteractionResult.FAIL;
        RandomSource random = level.getRandom();
        cattle.setBreed(breed);
        cattle.setSex(random.nextBoolean() ? CattleSex.FEMALE : CattleSex.MALE);
        cattle.setGenome(CattleGenome.randomFor(breed, random));
        cattle.setTraits(CattleTraits.random(random, breed));
        cattle.setLineage(CattleLineage.founder(breed));
        cattle.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, level.getRandom().nextFloat() * 360.0F, 0.0F);
        if (!level.noCollision(cattle, cattle.getBoundingBox())) return InteractionResult.FAIL;
        cattle.finalizeSpawn((net.minecraft.server.level.ServerLevel) level,
                level.getCurrentDifficultyAt(pos), MobSpawnType.SPAWN_EGG, null, context.getItemInHand().getTag());
        if (level.addFreshEntity(cattle)) {
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.COW_AMBIENT,
                    SoundSource.NEUTRAL, 1.0F, 0.9F + level.getRandom().nextFloat() * 0.2F);
            context.getItemInHand().shrink(1);
            if (context.getPlayer() != null) context.getPlayer().awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    public CattleBreed getBreed() { return breed; }
}
