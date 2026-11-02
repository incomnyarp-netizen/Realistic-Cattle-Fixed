package com.example.realisticcattle.entity;

import com.example.realisticcattle.genetics.CattleBreed;
import com.example.realisticcattle.genetics.CattlePhenotype;
import com.example.realisticcattle.genetics.CattleGenome;
import com.example.realisticcattle.genetics.CattleLineage;
import com.example.realisticcattle.genetics.CattleBreedComposition;
import com.example.realisticcattle.genetics.CattleTraits;
import com.example.realisticcattle.entity.ai.SeekHerdGoal;
import com.example.realisticcattle.entity.ai.HornRubGoal;
import com.example.realisticcattle.entity.ai.MutualGroomGoal;
import com.example.realisticcattle.registry.ModEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.util.RandomSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

/** First server-side entity prototype. Custom geometry/renderer is a separate client milestone. */
public class CattleEntity extends Animal implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.cattle.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.cattle.walk");
    private static final RawAnimation EAT = RawAnimation.begin().thenLoop("animation.cattle.eat");
    private static final RawAnimation HORN_RUB = RawAnimation.begin().thenLoop("animation.cattle.horn_rub");
    private static final RawAnimation GROOM = RawAnimation.begin().thenLoop("animation.cattle.social_groom");
    private static final RawAnimation NURSE = RawAnimation.begin().thenLoop("animation.calf.nurse");
    private static final RawAnimation MOTHER_GROOM = RawAnimation.begin().thenLoop("animation.cattle.mother_groom");
    private static final RawAnimation BIRTH = RawAnimation.begin().thenPlay("animation.cattle.birth");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> BREED_ID =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SEX_ID =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PHENOTYPE_ID =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HORNED =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> HUNGER =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> THIRST =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COMFORT =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SOCIAL =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PREGNANCY_TICKS =
            SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CALM = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SOCIALITY = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CURIOSITY = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FEAR = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STUBBORN = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MATERNAL = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTIVITY = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRUST = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SOCIAL_ACTION = SynchedEntityData.defineId(CattleEntity.class, EntityDataSerializers.INT);
    public static final int ACTION_NONE = 0;
    public static final int ACTION_HORN_RUB = 1;
    public static final int ACTION_GROOM = 2;
    public static final int ACTION_NURSE = 3;
    public static final int ACTION_MOTHER_CARE = 4;
    public static final int ACTION_BIRTH = 5;
    /** Seven in-game days keeps gestation meaningful without requiring real-world months. */
    public static final int GESTATION_TICKS = 7 * 24_000;

    private int needTickCounter;
    private int birthAnimationTicks;
    // Small persistent social memory: remembers a limited number of familiar herd-mates.
    private final Set<UUID> familiarCompanions = new LinkedHashSet<>();
    private static final int MAX_FAMILIARS = 8;
    @Nullable private UUID motherUuid;
    private int fatherBreedId = CattleBreed.HOLSTEIN.ordinal();
    private int fatherPhenotypeId = CattlePhenotype.HOLSTEIN_BLACK_WHITE.ordinal();
    private boolean fatherHorned = true;
    private CattleGenome genome = CattleGenome.fromPhenotype(CattlePhenotype.HOLSTEIN_BLACK_WHITE, true);
    private CattleGenome fatherGenome = CattleGenome.fromPhenotype(CattlePhenotype.HOLSTEIN_BLACK_WHITE, true);
    private CattleTraits traits = CattleTraits.random(RandomSource.create(), CattleBreed.HOLSTEIN);
    private CattleTraits fatherTraits = CattleTraits.random(RandomSource.create(), CattleBreed.HOLSTEIN);
    private CattleLineage lineage = CattleLineage.founder(CattleBreed.HOLSTEIN);
    private CattleLineage fatherLineage = CattleLineage.founder(CattleBreed.HOLSTEIN);
    private String fatherName = "Неизвестно";

    public CattleEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "locomotion", 5, state -> {
            if (state.isMoving()) return state.setAndContinue(WALK);
            return state.setAndContinue(IDLE);
        }));
        controllers.add(new AnimationController<>(this, "behavior", 5, state -> {
            return switch (getSocialAction()) {
                case ACTION_HORN_RUB -> state.setAndContinue(HORN_RUB);
                case ACTION_GROOM -> state.setAndContinue(GROOM);
                case ACTION_NURSE -> state.setAndContinue(NURSE);
                case ACTION_MOTHER_CARE -> state.setAndContinue(MOTHER_GROOM);
                case ACTION_BIRTH -> state.setAndContinue(BIRTH);
                default -> state.setAndContinue(IDLE);
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        if (reason != MobSpawnType.SPAWN_EGG && reason != MobSpawnType.COMMAND) {
            setSex(this.random.nextFloat() < 0.5F ? CattleSex.FEMALE : CattleSex.MALE);
        }
        if (reason != MobSpawnType.SPAWN_EGG && reason != MobSpawnType.COMMAND) {
            setGenome(CattleGenome.randomFor(getBreed(), this.random));
            setTraits(CattleTraits.random(this.random, getBreed()));
            setLineage(CattleLineage.founder(getBreed()));
        }
        return result;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 0.9D));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT, Items.HAY_BLOCK, com.example.realisticcattle.registry.ModItems.DAIRY_FEED.get(), com.example.realisticcattle.registry.ModItems.MINERAL_SUPPLEMENT.get()), false));
        this.goalSelector.addGoal(3, new com.example.realisticcattle.entity.ai.CalfNursingGoal(this));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.05D));
        this.goalSelector.addGoal(5, new MutualGroomGoal(this));
        this.goalSelector.addGoal(6, new HornRubGoal(this));
        this.goalSelector.addGoal(7, new SeekHerdGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.75D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BREED_ID, CattleBreed.HOLSTEIN.ordinal());
        this.entityData.define(SEX_ID, CattleSex.FEMALE.ordinal());
        this.entityData.define(PHENOTYPE_ID, CattlePhenotype.HOLSTEIN_BLACK_WHITE.ordinal());
        this.entityData.define(HORNED, true);
        this.entityData.define(HUNGER, 100);
        this.entityData.define(THIRST, 100);
        this.entityData.define(COMFORT, 100);
        this.entityData.define(SOCIAL, 100);
        this.entityData.define(PREGNANCY_TICKS, 0);
        CattlePersonality p = CattlePersonality.random(this.random);
        this.entityData.define(CALM, p.calmness());
        this.entityData.define(SOCIALITY, p.sociability());
        this.entityData.define(CURIOSITY, p.curiosity());
        this.entityData.define(FEAR, p.fearfulness());
        this.entityData.define(STUBBORN, p.stubbornness());
        this.entityData.define(MATERNAL, p.maternalInstinct());
        this.entityData.define(ACTIVITY, p.activity());
        this.entityData.define(TRUST, p.trust());
        this.entityData.define(SOCIAL_ACTION, ACTION_NONE);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && birthAnimationTicks > 0 && --birthAnimationTicks == 0) clearSocialAction();
        if (!this.level().isClientSide && isPregnant()) {
            int next = getPregnancyTicks() + 1;
            entityData.set(PREGNANCY_TICKS, next);
            if (next >= GESTATION_TICKS) deliverCalf();
        }
        if (!this.level().isClientSide && ++needTickCounter >= 200) {
            needTickCounter = 0;
            entityData.set(HUNGER, Math.max(0, entityData.get(HUNGER) - 1));
            entityData.set(THIRST, Math.max(0, entityData.get(THIRST) - 1));
            // A herd is beneficial, never mandatory. A solitary animal keeps a healthy social baseline
            // and loses social comfort only slowly; nearby companions improve its mood.
            int companions = countNearbyCompanions(10.0D);
            int social = getSocial();
            int socialTarget = companions == 0 ? 65 : (companions == 1 ? 82 : 96);
            int socialStep = companions == 0 ? 1 : 2;
            if (social < socialTarget) social = Math.min(socialTarget, social + socialStep);
            else if (social > socialTarget) social = Math.max(socialTarget, social - 1);
            entityData.set(SOCIAL, clamp(social));
        }
    }

    public CattleBreed getBreed() {
        int id = entityData.get(BREED_ID);
        CattleBreed[] values = CattleBreed.values();
        return values[Math.max(0, Math.min(id, values.length - 1))];
    }

    public void setBreed(CattleBreed breed) { entityData.set(BREED_ID, breed.ordinal()); }
    public CattlePhenotype getPhenotype() { return CattlePhenotype.byId(entityData.get(PHENOTYPE_ID)); }
    public void setPhenotype(CattlePhenotype phenotype) {
        entityData.set(PHENOTYPE_ID, phenotype.ordinal());
        this.genome = CattleGenome.fromPhenotype(phenotype, isHorned());
    }
    public CattleGenome getGenome() { return genome; }
    public CattleTraits getTraits() { return traits; }
    public void setTraits(CattleTraits value) { if (value != null) traits = value; }
    public void setGenome(CattleGenome value) {
        this.genome = value;
        entityData.set(PHENOTYPE_ID, value.express(getBreed()).ordinal());
        entityData.set(HORNED, value.isHorned());
    }
    public boolean isHorned() { return entityData.get(HORNED); }
    public void setHorned(boolean horned) {
        entityData.set(HORNED, horned);
        this.genome = this.genome.withHorned(horned);
    }
    public CattleLineage getLineage() { return lineage; }
    public void setLineage(CattleLineage value) { if (value != null) lineage = value; }
    public boolean isPurebred() {
        return CattleBreedComposition.matches(getBreed().name(), lineage.ancestry(), 0.001)
                && getGenome().hasBreedStandardPhenotype(getBreed());
    }
    public String getDisplayNameForPedigree() {
        return this.hasCustomName() ? this.getCustomName().getString() : getBreed().name() + " " + getSex().name();
    }
    public CattleSex getSex() { return CattleSex.fromId(entityData.get(SEX_ID)); }
    public void setSex(CattleSex sex) { entityData.set(SEX_ID, sex.ordinal()); this.refreshDimensions(); }
    public int getHunger() { return entityData.get(HUNGER); }
    public int getThirst() { return entityData.get(THIRST); }
    public int getComfort() { return entityData.get(COMFORT); }
    public int getSocial() { return entityData.get(SOCIAL); }
    public int getPregnancyTicks() { return entityData.get(PREGNANCY_TICKS); }
    public int getSocialAction() { return entityData.get(SOCIAL_ACTION); }
    public void setMotherUuid(@Nullable UUID id) { this.motherUuid = id; }
    @Nullable public UUID getMotherUuid() { return motherUuid; }
    public boolean isCalf() { return isBaby(); }
    public void setSocialAction(int action) { if (!this.level().isClientSide) entityData.set(SOCIAL_ACTION, action); }
    public void clearSocialAction() { setSocialAction(ACTION_NONE); }
    public void improveSocialComfort(int amount) { entityData.set(SOCIAL, clamp(getSocial() + amount)); }
    public void improveTrust(int amount) { entityData.set(TRUST, clamp(entityData.get(TRUST) + amount)); }

    /** Number of nearby adult cattle, excluding this entity. */
    public int countNearbyCompanions(double radius) {
        return this.level().getEntitiesOfClass(CattleEntity.class, this.getBoundingBox().inflate(radius),
                other -> other != this && other.isAlive() && !other.isBaby()).size();
    }

    public CattleEntity findNearbyCompanion(double radius) {
        return this.level().getEntitiesOfClass(CattleEntity.class, this.getBoundingBox().inflate(radius),
                other -> other != this && other.isAlive() && !other.isBaby()).stream()
                .min((a, b) -> Double.compare(this.distanceToSqr(a), this.distanceToSqr(b)))
                .orElse(null);
    }

    /** True when another adult cattle is close enough to count as company. */
    public boolean hasCompanionsNearby() { return countNearbyCompanions(10.0D) > 0; }

    /** Records a positive social contact; memory survives saving/reloading the world. */
    public void rememberCompanion(CattleEntity other) {
        if (other == null || other == this || this.level().isClientSide) return;
        familiarCompanions.add(other.getUUID());
        while (familiarCompanions.size() > MAX_FAMILIARS) {
            familiarCompanions.remove(familiarCompanions.iterator().next());
        }
    }

    public boolean knowsCompanion(CattleEntity other) {
        return other != null && familiarCompanions.contains(other.getUUID());
    }

    public int getFamiliarCount() { return familiarCompanions.size(); }

    /** Familiar animals are preferred for calm social interactions, without excluding strangers. */
    public CattleEntity findPreferredCompanion(double radius) {
        return this.level().getEntitiesOfClass(CattleEntity.class, this.getBoundingBox().inflate(radius),
                other -> other != this && other.isAlive() && !other.isBaby()).stream()
                .min((a, b) -> {
                    int knownA = knowsCompanion(a) ? 0 : 1;
                    int knownB = knowsCompanion(b) ? 0 : 1;
                    int familiarity = Integer.compare(knownA, knownB);
                    return familiarity != 0 ? familiarity : Double.compare(this.distanceToSqr(a), this.distanceToSqr(b));
                }).orElse(null);
    }
    private CattlePhenotype getFatherPhenotype() { return CattlePhenotype.byId(fatherPhenotypeId); }
    private boolean getFatherHorned() { return fatherHorned; }
    public boolean isPregnant() { return getSex() == CattleSex.FEMALE && getPregnancyTicks() > 0; }
    public int getPregnancyPercent() { return isPregnant() ? Math.min(100, getPregnancyTicks() * 100 / GESTATION_TICKS) : 0; }

    /** BreedGoal calls this after courtship. Conception is separated from birth. */
    @Override
    public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        if (this.level().isClientSide || !(partner instanceof CattleEntity other)) return;
        CattleEntity mother = this.getSex() == CattleSex.FEMALE ? this
                : (other.getSex() == CattleSex.FEMALE ? other : null);
        CattleEntity father = this.getSex() == CattleSex.MALE ? this
                : (other.getSex() == CattleSex.MALE ? other : null);
        if (mother == null || mother.isPregnant()) return;
        mother.fatherBreedId = father == null ? mother.getBreed().ordinal() : father.getBreed().ordinal();
        mother.fatherPhenotypeId = father == null ? mother.getPhenotype().ordinal() : father.getPhenotype().ordinal();
        mother.fatherHorned = father == null ? mother.isHorned() : father.isHorned();
        mother.fatherGenome = father == null ? mother.getGenome() : father.getGenome();
        mother.fatherTraits = father == null ? mother.getTraits() : father.getTraits();
        mother.fatherLineage = father == null ? mother.getLineage() : father.getLineage();
        mother.fatherName = father == null ? mother.getDisplayNameForPedigree() : father.getDisplayNameForPedigree();
        mother.entityData.set(PREGNANCY_TICKS, 1);
        mother.setAge(6000);
        other.setAge(6000);
        mother.setInLoveTime(0);
        other.setInLoveTime(0);
        level.broadcastEntityEvent(mother, (byte) 18);
    }

    private void deliverCalf() {
        if (!(this.level() instanceof ServerLevel serverLevel) || getSex() != CattleSex.FEMALE) return;
        CattleEntity calf = ModEntities.CATTLE.get().create(serverLevel);
        if (calf == null) { entityData.set(PREGNANCY_TICKS, GESTATION_TICKS - 1); return; }
        CattleBreed fatherBreed = CattleBreed.values()[Math.max(0, Math.min(fatherBreedId, CattleBreed.values().length - 1))];
        CattleBreed inherited = this.random.nextBoolean() ? getBreed() : fatherBreed;
        calf.setBreed(inherited);
        calf.setGenome(CattleGenome.inherit(this.getGenome(), this.fatherGenome, this.random));
        calf.setTraits(CattleTraits.inherit(this.getTraits(), this.fatherTraits, this.random));
        calf.setLineage(CattleLineage.child(fatherName, fatherBreed, fatherGenome, fatherLineage,
                this.getDisplayNameForPedigree(), this.getBreed(), this.getGenome(), this.getLineage()));
        calf.setSex(this.random.nextBoolean() ? CattleSex.MALE : CattleSex.FEMALE);
        calf.setMotherUuid(this.getUUID());
        calf.setPersonality(CattlePersonality.inherit(getPersonality(), getPersonality(), this.random));
        calf.setBaby(true);
        Vec3 offset = this.position().add(this.getLookAngle().scale(-1.1D));
        calf.moveTo(offset.x, offset.y, offset.z, this.getYRot(), 0.0F);
        if (serverLevel.noCollision(calf) && serverLevel.addFreshEntity(calf)) {
            this.level().gameEvent(this, GameEvent.ENTITY_PLACE, this.position());
            rememberCompanion(calf);
            calf.rememberCompanion(this);
            setNeeds(getHunger() - 8, getThirst() - 3, getComfort(), getSocial());
            setSocialAction(ACTION_BIRTH);
            birthAnimationTicks = 60;
            this.setAge(6000);
        }
        entityData.set(PREGNANCY_TICKS, 0);
    }
    public CattlePersonality getPersonality() {
        return new CattlePersonality(entityData.get(CALM), entityData.get(SOCIALITY), entityData.get(CURIOSITY),
                entityData.get(FEAR), entityData.get(STUBBORN), entityData.get(MATERNAL),
                entityData.get(ACTIVITY), entityData.get(TRUST));
    }
    public void setPersonality(CattlePersonality p) {
        entityData.set(CALM, clamp(p.calmness())); entityData.set(SOCIALITY, clamp(p.sociability()));
        entityData.set(CURIOSITY, clamp(p.curiosity())); entityData.set(FEAR, clamp(p.fearfulness()));
        entityData.set(STUBBORN, clamp(p.stubbornness())); entityData.set(MATERNAL, clamp(p.maternalInstinct()));
        entityData.set(ACTIVITY, clamp(p.activity())); entityData.set(TRUST, clamp(p.trust()));
    }

    public void setNeeds(int hunger, int thirst, int comfort, int social) {
        entityData.set(HUNGER, clamp(hunger));
        entityData.set(THIRST, clamp(thirst));
        entityData.set(COMFORT, clamp(comfort));
        entityData.set(SOCIAL, clamp(social));
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.isBaby()) return EntityDimensions.scalable(0.78F, 0.92F);
        return getSex() == CattleSex.MALE
                ? EntityDimensions.scalable(1.48F, 1.62F)
                : EntityDimensions.scalable(1.35F, 1.55F);
    }

    private static int clamp(int value) { return Math.max(0, Math.min(100, value)); }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return stack.is(Items.WHEAT) || stack.is(Items.HAY_BLOCK);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        CattleEntity calf = ModEntities.CATTLE.get().create(level);
        if (calf != null) {
            calf.setBreed(this.getBreed());
            calf.setPhenotype(this.getPhenotype());
            calf.setHorned(this.isHorned());
            calf.setSex(this.random.nextBoolean() ? CattleSex.MALE : CattleSex.FEMALE);
            if (partner instanceof CattleEntity other) {
                CattleEntity mother = this.getSex() == CattleSex.FEMALE ? this
                        : (other.getSex() == CattleSex.FEMALE ? other : null);
                if (mother != null) calf.setMotherUuid(mother.getUUID());
                calf.setGenome(CattleGenome.inherit(this.getGenome(), other.getGenome(), this.random));
                calf.setTraits(CattleTraits.inherit(this.getTraits(), other.getTraits(), this.random));
                CattleEntity sire = this.getSex() == CattleSex.MALE ? this : other;
                CattleEntity dam = mother;
                if (dam != null) calf.setLineage(CattleLineage.child(sire.getDisplayNameForPedigree(), sire.getBreed(), sire.getGenome(), sire.getLineage(),
                        dam.getDisplayNameForPedigree(), dam.getBreed(), dam.getGenome(), dam.getLineage()));
                calf.setPersonality(CattlePersonality.inherit(this.getPersonality(), other.getPersonality(), this.random));
            }
            calf.setBaby(true);
        }
        return calf;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Breed", entityData.get(BREED_ID));
        tag.putInt("Sex", entityData.get(SEX_ID));
        tag.putInt("Hunger", entityData.get(HUNGER));
        tag.putInt("Thirst", entityData.get(THIRST));
        tag.putInt("Comfort", entityData.get(COMFORT));
        tag.putInt("Social", entityData.get(SOCIAL));
        tag.putInt("PregnancyTicks", entityData.get(PREGNANCY_TICKS));
        if (motherUuid != null) tag.putUUID("MotherUUID", motherUuid);
        tag.putInt("FatherBreed", fatherBreedId);
        tag.putInt("Phenotype", getPhenotype().ordinal());
        tag.putBoolean("Horned", isHorned());
        tag.putInt("FatherPhenotype", fatherPhenotypeId);
        tag.putBoolean("FatherHorned", fatherHorned);
        tag.put("Genome", genome.save());
        tag.put("Traits", traits.save());
        tag.put("FatherTraits", fatherTraits.save());
        tag.put("FatherGenome", fatherGenome.save());
        tag.put("Lineage", lineage.save());
        tag.put("FatherLineage", fatherLineage.save());
        tag.putString("FatherName", fatherName);
        CattlePersonality p = getPersonality();
        tag.putInt("PersonalityCalm", p.calmness()); tag.putInt("PersonalitySocial", p.sociability());
        tag.putInt("PersonalityCuriosity", p.curiosity()); tag.putInt("PersonalityFear", p.fearfulness());
        tag.putInt("PersonalityStubborn", p.stubbornness()); tag.putInt("PersonalityMaternal", p.maternalInstinct());
        tag.putInt("PersonalityActivity", p.activity()); tag.putInt("PersonalityTrust", p.trust());
        ListTag familiarList = new ListTag();
        for (UUID id : familiarCompanions) familiarList.add(StringTag.valueOf(id.toString()));
        tag.put("FamiliarCompanions", familiarList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(BREED_ID, tag.getInt("Breed"));
        entityData.set(SEX_ID, tag.getInt("Sex"));
        entityData.set(HUNGER, clamp(tag.getInt("Hunger")));
        entityData.set(THIRST, clamp(tag.getInt("Thirst")));
        entityData.set(COMFORT, clamp(tag.getInt("Comfort")));
        entityData.set(SOCIAL, clamp(tag.getInt("Social")));
        entityData.set(PREGNANCY_TICKS, Math.max(0, tag.getInt("PregnancyTicks")));
        motherUuid = tag.hasUUID("MotherUUID") ? tag.getUUID("MotherUUID") : null;
        fatherBreedId = Math.max(0, Math.min(tag.getInt("FatherBreed"), CattleBreed.values().length - 1));
        setPhenotype(CattlePhenotype.byId(tag.getInt("Phenotype")));
        setHorned(!tag.contains("Horned") || tag.getBoolean("Horned"));
        fatherPhenotypeId = CattlePhenotype.byId(tag.getInt("FatherPhenotype")).ordinal();
        fatherHorned = !tag.contains("FatherHorned") || tag.getBoolean("FatherHorned");
        if (tag.contains("Genome", 10)) setGenome(CattleGenome.load(tag.getCompound("Genome")));
        else setGenome(CattleGenome.fromPhenotype(getPhenotype(), isHorned()));
        if (tag.contains("Traits", 10)) traits = CattleTraits.load(tag.getCompound("Traits"));
        else traits = CattleTraits.random(RandomSource.create(getUUID().getMostSignificantBits()), getBreed());
        if (tag.contains("FatherTraits", 10)) fatherTraits = CattleTraits.load(tag.getCompound("FatherTraits"));
        else fatherTraits = traits;
        if (tag.contains("FatherGenome", 10)) fatherGenome = CattleGenome.load(tag.getCompound("FatherGenome"));
        else fatherGenome = CattleGenome.fromPhenotype(getFatherPhenotype(), fatherHorned);
        lineage = tag.contains("Lineage", 10) ? CattleLineage.load(tag.getCompound("Lineage"), getBreed()) : CattleLineage.founder(getBreed());
        fatherLineage = tag.contains("FatherLineage", 10) ? CattleLineage.load(tag.getCompound("FatherLineage"), CattleBreed.values()[fatherBreedId]) : CattleLineage.founder(CattleBreed.values()[fatherBreedId]);
        fatherName = tag.contains("FatherName") ? tag.getString("FatherName") : "Неизвестно";
        familiarCompanions.clear();
        if (tag.contains("FamiliarCompanions", 9)) {
            ListTag list = tag.getList("FamiliarCompanions", 8);
            for (int i = 0; i < list.size() && familiarCompanions.size() < MAX_FAMILIARS; i++) {
                try { familiarCompanions.add(UUID.fromString(list.getString(i))); }
                catch (IllegalArgumentException ignored) { /* Ignore malformed legacy entry. */ }
            }
        }
        if (tag.contains("PersonalityCalm")) setPersonality(new CattlePersonality(
                tag.getInt("PersonalityCalm"), tag.getInt("PersonalitySocial"), tag.getInt("PersonalityCuriosity"),
                tag.getInt("PersonalityFear"), tag.getInt("PersonalityStubborn"), tag.getInt("PersonalityMaternal"),
                tag.getInt("PersonalityActivity"), tag.getInt("PersonalityTrust")));
    }
}
