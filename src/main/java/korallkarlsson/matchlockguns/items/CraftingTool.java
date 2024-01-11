package korallkarlsson.matchlockguns.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CraftingTool extends Item {
	public CraftingTool(int durability) {
		super(new Properties().stacksTo(1).durability(durability));
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
		int damage = itemStack.getDamageValue();
		if(damage == itemStack.getMaxDamage() - 1)
			return ItemStack.EMPTY;
		else {
			ItemStack newStack = new ItemStack(this);
			newStack.setDamageValue(damage + 1);
			return newStack;
		}
		//return new ItemStack(this);
		//return super.getCraftingRemainingItem(itemStack);
	}
}
