package korallkarlsson.matchlockguns.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class WheellockGunItem extends BaseGunItem {

    public int maxWinding = 12;
    public WheellockGunItem(int durability, int gunpowderAmount, int maxLoads, int chargeTime, int numProjectiles, int damage, float accuracy, float power, boolean canDualWield, boolean useRamRod, float reliability, int cooldown) {
        super(durability, gunpowderAmount, maxLoads, chargeTime, numProjectiles, damage, accuracy, power, canDualWield, useRamRod, reliability, cooldown);
    }

    public static short getWinding(ItemStack item) {
        CompoundTag tag = item.getTag();
        if(tag == null)
            return 0;

        return tag.getShort("winding");
    }

    void setWinding(ItemStack item, short val) {
        item.getOrCreateTag().putShort("winding", val);
    }
    @Override
    public boolean canShoot(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        if(getWinding(mainItem) < maxWinding)
            return false;

        return super.canShoot(world, player, mainItem, offhandItem, hand);
    }

    @Override
    public InteractionResultHolder<ItemStack> extraInteraction(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        if(!player.isCrouching() || !offhandItem.isEmpty())
            return null;

        short wind = getWinding(mainItem);
        if(wind == maxWinding) {
            //world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 1f, 0.5f);
            return InteractionResultHolder.fail(mainItem);
        } else {
            if(wind == maxWinding - 1)
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.PLAYERS, 1.2f, 0.8f);
            else
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 1, 1);
            setWinding(mainItem, (short) (wind + 1));
            return InteractionResultHolder.consume(mainItem);
        }


    }

    @Override
    public void afterShoot(Level world, Player player, ItemStack mainItem, ItemStack offhandItem, InteractionHand hand) {
        setWinding(mainItem, (short) 0);
    }

    @Override
    public void appendHoverText(ItemStack item, Level world, List<Component> componentList, TooltipFlag flag) {
        super.appendHoverText(item, world, componentList, flag);

        if(getWinding(item) == maxWinding)
            componentList.add(Component.literal("\uD83C\uDF00"));
    }
}
