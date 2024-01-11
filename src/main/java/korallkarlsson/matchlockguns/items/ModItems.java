package korallkarlsson.matchlockguns.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import korallkarlsson.matchlockguns.MatchlockGuns;

public class ModItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MatchlockGuns.MODID);

	public static final RegistryObject<Item> BASIC_TOOL = ITEMS.register("basic_tool", () -> new CraftingTool(64));
	public static final RegistryObject<Item> ADVANCED_TOOL = ITEMS.register("advanced_tool", () -> new CraftingTool(256));
	public static final RegistryObject<Item> MATCHLOCK_MECHANISM = ITEMS.register("matchlock_mechanism", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> WHEELLOCK_MECHANISM = ITEMS.register("wheellock_mechanism", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> FLINTLOCK_MECHANISM = ITEMS.register("flintlock_mechanism", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> REVOLVER_MECHANISM = ITEMS.register("revolver_mechanism", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> GUN_BARREL = ITEMS.register("gun_barrel", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RIFLE_BARREL = ITEMS.register("rifle_barrel", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> MUSKET_STOCK = ITEMS.register("musket_stock", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PISTOL_GRIP = ITEMS.register("pistol_grip", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> MECHANICAL_PARTS = ITEMS.register("mechanical_parts", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PYRITE = ITEMS.register("pyrite", () -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> SMALL_CARTRIDGE = ITEMS.register("small_cartridge", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> LARGE_CARTRIDGE = ITEMS.register("large_cartridge", () -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> ROUND = ITEMS.register("round", () -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> RAMROD = ITEMS.register("ramrod", () -> new Item(new Item.Properties()));
}
