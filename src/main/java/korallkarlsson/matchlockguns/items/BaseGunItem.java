package korallkarlsson.matchlockguns.items;

import korallkarlsson.matchlockguns.MatchlockGuns;
import korallkarlsson.matchlockguns.entities.BulletEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BaseGunItem extends Item {

    public static final int AFTER_RELOAD_COOLDOWN = 10;
    public static final int JAM_COOLDOWN = 65;

    final int maxLoads;
    final int numProjectiles;
    final int damage;
    final float accuracy;
    final boolean canDualWeild;
    final int gunpowderAmount;
    final int chargeTime;
    final boolean useRamRod;
    final float power;
    final float reliabilty;
    final int cooldown;

    @Override
    public String toString() {
        return "BaseGunItem: [gunpowder %s] [maxShots %s] [chargeTime %s] [numProjectiles %s] [damage %s] [accuracy %s] [rage %s] [canDualWield %s] [ramRod %s]".formatted(
                gunpowderAmount,
                maxLoads,
                chargeTime,
                numProjectiles,
                damage,
                accuracy,
                power,
                canDualWeild,
                useRamRod
        );
    }

    //https://docs.minecraftforge.net/en/1.20.x/resources/client/models/itemproperties/
    public static float loadingPropertyFunction(ItemStack item, ClientLevel world, LivingEntity entity, int id) {
        if(entity == null)
            return 0;

        if(!entity.isUsingItem() || entity.getUseItem() != item)
            return 0;

        int totalTicks = entity.getTicksUsingItem() + entity.getUseItemRemainingTicks();
        return (float) entity.getTicksUsingItem() / (float) totalTicks;
    }

    public static float isLoadingPropertyFunction(ItemStack item, ClientLevel world, LivingEntity entity, int id) {
        if(entity == null)
            return 0;

        if(!entity.isUsingItem() || entity.getUseItem() != item)
            return 0;

        return 1;
    }

    public BaseGunItem(int durability, int gunpowderAmount, int maxLoads, int chargeTime, int numProjectiles, int damage, float accuracy, float power, boolean canDualWield, boolean useRamRod, float reliabilty, int cooldown) {
        super(new Properties().stacksTo(1).durability(durability));

        this.maxLoads = maxLoads;
        this.numProjectiles = numProjectiles;
        this.damage = damage;
        this.accuracy = accuracy;
        this.canDualWeild = canDualWield;
        this.gunpowderAmount = gunpowderAmount;
        this.chargeTime = chargeTime;
        this.useRamRod = useRamRod;
        this.power = power;
        this.reliabilty = reliabilty;
        this.cooldown = cooldown;
    }

    enum LoadingState {
        INITIAL,
        GUNPOWDER,
        ROUND
    }

    public LoadingState getLoadingState(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null)
            return LoadingState.INITIAL;

        switch (tag.getByte("load_state")) {
            case 1:
                return LoadingState.GUNPOWDER;
            case 2:
                return LoadingState.ROUND;

            case 0:
            default:
                return LoadingState.INITIAL;
        }
    }

    void setLoadingState(ItemStack stack, LoadingState state) {
        if(!useRamRod)
            assert state != LoadingState.ROUND;
        stack.getOrCreateTag().putByte("load_state", (byte) state.ordinal());
    }

    public short getLoadedShots(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null)
            return 0;

        return tag.getShort("loaded_shots");
    }

    void setLoadedShots(ItemStack stack, short n) {
        assert n >= 0;
        assert n <= maxLoads;
        stack.getOrCreateTag().putShort("loaded_shots", n);
    }

    static InteractionHand getOppositeHand(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public InteractionResultHolder<ItemStack> extraInteraction(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        return null;
    }

    public boolean canShoot(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        //return !player.isInWaterRainOrBubble();
        return !player.isInWaterOrBubble();
    }

    public void afterShoot(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {

    }

    public static float randomOffset(RandomSource rand) {
        return (rand.nextFloat() - 0.5f) * 2f;
    }
    final void shoot(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        Vec3 pos = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        float inaccuracy = 1 / accuracy;
        RandomSource rand = player.getRandom();

        for(int i = 0; i < numProjectiles; i++) {
            Vec3 d = dir.add(randomOffset(rand) * inaccuracy, randomOffset(rand) * inaccuracy, randomOffset(rand) * inaccuracy).normalize();
            BulletEntity bullet = new BulletEntity(world, player, pos.x, pos.y, pos.z, this.damage, d.scale(power));
            world.addFreshEntity(bullet);
        }
    }

    public final boolean fireGun(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        assert world instanceof ServerLevel;

        float rand = player.getRandom().nextFloat();
        if(rand > reliabilty) { // Jamming
            player.getCooldowns().addCooldown(this, JAM_COOLDOWN);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1, 1.5f);
            return false;
        }

        setLoadedShots(mainItem, (short) (getLoadedShots(mainItem) - 1));
        player.getCooldowns().addCooldown(this, cooldown);
        mainItem.hurtAndBreak(1, player, (p) -> {
            p.broadcastBreakEvent(hand);
        });

        Vec3 eyePos = player.getEyePosition().add(player.getLookAngle());
        ((ServerLevel) world).sendParticles(ParticleTypes.SMOKE, eyePos.x, eyePos.y, eyePos.z, 3, 0.01, 0.01, 0.01, 0.025);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1, 1);
        shoot(world, player, mainItem, offhandItem, hand);
        afterShoot(world, player, mainItem, offhandItem, hand);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        ItemStack mainItem = player.getItemInHand(hand);
        ItemStack offhandItem = player.getItemInHand(getOppositeHand(hand));

        InteractionResultHolder<ItemStack> extra = extraInteraction(world, player, mainItem, offhandItem, hand);
        if(extra != null)
            return extra;

        LoadingState state = getLoadingState(mainItem);
        switch (state) {
            case INITIAL -> {
                int numShots = getLoadedShots(mainItem);
                if(offhandItem.is(Items.GUNPOWDER)) {
                    int ngunpowder = offhandItem.getCount();
                    if(ngunpowder >= gunpowderAmount && numShots < maxLoads) {
                        offhandItem.shrink(gunpowderAmount);
                        setLoadingState(mainItem, LoadingState.GUNPOWDER);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 1, 1);
                        return InteractionResultHolder.consume(mainItem);
                    } else {
                        return InteractionResultHolder.fail(mainItem);
                    }
                } else if((offhandItem.is(ModItems.SMALL_CARTRIDGE.get()) && gunpowderAmount == 1) || (offhandItem.is(ModItems.LARGE_CARTRIDGE.get()) && gunpowderAmount == 2)) {
                    if(numShots < maxLoads) {
                        offhandItem.shrink(1);
                        setLoadingState(mainItem, LoadingState.ROUND);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 1, 1);
                        return InteractionResultHolder.consume(mainItem);
                    } else {
                        return InteractionResultHolder.fail(mainItem);
                    }
                } else if(numShots > 0) {
                    if(!offhandItem.isEmpty()) {
                        if(!canDualWeild)
                            return InteractionResultHolder.fail(mainItem);
                        if(offhandItem.is(ModItems.RAMROD.get()) || offhandItem.is(Items.GUNPOWDER) || offhandItem.is(ModItems.ROUND.get()))
                            return InteractionResultHolder.pass(mainItem);

                        if(offhandItem.getItem() instanceof BaseGunItem GunItem) {
                            if(
                                    GunItem.getLoadedShots(offhandItem) > numShots &&
                                    GunItem.getLoadingState(offhandItem) == LoadingState.INITIAL &&
                                    GunItem.canShoot(world, player, offhandItem, mainItem, getOppositeHand(hand))
                            ) {
                                return InteractionResultHolder.pass(mainItem);
                            }
                        }
                    }

                    if(!canShoot(world, player, mainItem, offhandItem, hand)) {
                        return InteractionResultHolder.fail(mainItem);
                    }

                    if(!world.isClientSide())
                        fireGun(world, player, mainItem, offhandItem, hand);
                    return InteractionResultHolder.consume(mainItem);
                }
            }

            case GUNPOWDER -> {
                if(offhandItem.is(ModItems.ROUND.get())) {
                    offhandItem.shrink(1);
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1, 1);
                    setLoadingState(mainItem, LoadingState.ROUND);
                    return InteractionResultHolder.consume(mainItem);
                } else {
                    return InteractionResultHolder.fail(mainItem);
                }
            }

            case ROUND -> {
                if((useRamRod && offhandItem.is(ModItems.RAMROD.get())) || (!useRamRod && offhandItem.isEmpty())) {
                    player.startUsingItem(hand);
                    return InteractionResultHolder.consume(mainItem);
                }
                return InteractionResultHolder.fail(mainItem);
            }

        }

        return InteractionResultHolder.fail(mainItem);
    }

    @Override
    public Component getName(ItemStack item) {
        Component c = super.getName(item);
        c.getSiblings().add(Component.literal(" (" + getLoadedShots(item) + "/" + maxLoads + ")"));

        return c;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repairIngredient) {
        if(repairIngredient.is(Tags.Items.INGOTS_IRON))
            return true;
        return super.isValidRepairItem(toRepair, repairIngredient);
    }

    @Override
    public void appendHoverText(ItemStack item, Level world, List<Component> componentList, TooltipFlag flag) {
        //int numshots = getLoadedShots(item);
        //componentList.add(Component.literal(numshots + "/" + maxLoads));


        LoadingState state = getLoadingState(item);
        if(state.ordinal() != 0) {
            componentList.add(Component.literal(state.ordinal() + "/" + LoadingState.values().length));
        }
    }

    public ItemStack finishUseHook(ItemStack stack, Level world, LivingEntity entity) {
        return null;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, @NotNull LivingEntity entity) {
        ItemStack extra = finishUseHook(stack, world, entity);
        if(extra != null)
            return extra;

        short numShots = getLoadedShots(stack);
        LoadingState state = getLoadingState(stack);
        if(numShots < maxLoads && state == LoadingState.ROUND) {
            setLoadingState(stack, LoadingState.INITIAL);
            setLoadedShots(stack, (short) (numShots + 1));
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5f, 1);
            if(!world.isClientSide() && entity instanceof Player player)
                player.getCooldowns().addCooldown(this, AFTER_RELOAD_COOLDOWN);
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return chargeTime;
    }

}
