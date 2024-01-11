package korallkarlsson.matchlockguns.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MatchlockGunItem extends BaseGunItem{
    public MatchlockGunItem(int durability, int gunpowderAmount, int maxLoads, int chargeTime, int numProjectiles, int damage, float accuracy, float power, boolean canDualWield, boolean useRamRod, float reliability, int cooldown) {
        super(durability, gunpowderAmount, maxLoads, chargeTime, numProjectiles, damage, accuracy, power, canDualWield, useRamRod, reliability, cooldown);
    }

    public static float isLitPropertyFunction(ItemStack item, ClientLevel world, LivingEntity entity, int id) {
        return isLit(item)? 1f : 0f;
    }

    public static boolean isLit(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if(tag == null)
            return false;

        return tag.getBoolean("isLit");
    }

    void setLit(ItemStack stack, boolean val) {
        stack.getOrCreateTag().putBoolean("isLit", val);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack item, @NotNull Level world, @NotNull Entity entity, int i, boolean b) {
        super.inventoryTick(item, world, entity, i, b);
        if(!world.isClientSide() && entity instanceof LivingEntity livingEntity) {
            if(livingEntity.getRandom().nextInt() % 1000 == 0 || livingEntity.isInWaterRainOrBubble()) {
                if(isLit(item))
                    world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1, 1);

                setLit(item, false);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> extraInteraction(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        if(offhandItem.getItem() == Items.FLINT_AND_STEEL) {
            if(!isLit(mainItem) && !player.isInWaterRainOrBubble()) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
                setLit(mainItem, true);
                if(!world.isClientSide()) {
                    offhandItem.hurtAndBreak(1, player, (p) -> {
                        p.broadcastBreakEvent(getOppositeHand(hand));
                    });
                }
                return InteractionResultHolder.consume(mainItem);
            }

            return InteractionResultHolder.success(mainItem);
        }

        return null;
    }

    @Override
    public boolean canShoot(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        if(!isLit(mainItem))
            return false;
        return super.canShoot(world, player, mainItem, offhandItem, hand);
    }

    @Override
    public void appendHoverText(ItemStack item, Level world, List<Component> componentList, TooltipFlag flag) {
        super.appendHoverText(item, world, componentList, flag);

        if(isLit(item))
            componentList.add(Component.literal("\uD83D\uDD25").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
    }
}
