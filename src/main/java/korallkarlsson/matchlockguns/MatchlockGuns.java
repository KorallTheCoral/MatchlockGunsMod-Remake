package korallkarlsson.matchlockguns;

import korallkarlsson.matchlockguns.entities.BulletEntity;
import korallkarlsson.matchlockguns.entities.BulletEntityRenderer;
import korallkarlsson.matchlockguns.items.BaseGunItem;
import korallkarlsson.matchlockguns.items.MatchlockGunItem;
import korallkarlsson.matchlockguns.items.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod(MatchlockGuns.MODID)
public class MatchlockGuns {
	public static final String MODID = "matchlockguns";
	private final Map<String, DynamicGunItemProperties> loadedItems = new HashMap<>();

	void loadContentPack(List<String> lines) {
		Map<String, Map<String, String>> entries = Util.parseNotation(lines);
		entries.forEach((String name, Map<String, String> map) -> {
			loadedItems.put(name, new DynamicGunItemProperties(name, map));
		});
	}

	void loadDynamicItems() {
		loadContentPack(Config.BASE_CONTENT_PACK.lines().toList());

		File dir = new File("matchlockguns");
		if(!dir.exists()) {
			boolean ok = dir.mkdir();
			if(!ok)
				return;
		}

		File[] files = dir.listFiles();
		if(files == null)
			return;

		for (File f : files) {
			if(!f.getName().endsWith(".matchpack"))
				continue;
			List<String> lines;
			try {
				lines = Files.readAllLines(f.toPath());
			} catch (IOException e) {
				continue;
			}

			loadContentPack(lines);
		}
	}

	@ObjectHolder(registryName = "item", value = "matchlock_arquebus")
	public static final Item MATCHLOCK_ARQUEBUS_ITEM = null;

	@ObjectHolder(registryName = "entity_type", value = "bullet")
	public static final EntityType<BulletEntity> BULLET_ENTITY_TYPE = null;

	static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
	// https://docs.minecraftforge.net/en/1.20.x/items/
	public static final RegistryObject<CreativeModeTab> MATCHLOCKGUNS_CREATIVE_TAB = CREATIVE_TABS.register(
			"maintab", () ->
					CreativeModeTab.builder().title(Component.literal("Matchlock Guns")).icon(() -> new ItemStack(MATCHLOCK_ARQUEBUS_ITEM)).build()
			);

	public MatchlockGuns() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::onRegister);
		modBus.addListener(this::buildCreativeTab);
		modBus.addListener(this::onRegisterEntityRenderers);
		modBus.addListener(this::clientSetup);
		loadDynamicItems();

		CREATIVE_TABS.register(modBus);
		ModItems.ITEMS.register(modBus);
	}

	public void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			//https://docs.minecraftforge.net/en/1.20.x/resources/client/models/itemproperties/
			ResourceLocation isLoadingResource = new ResourceLocation(MODID, "isloading");
			ResourceLocation loadingResource = new ResourceLocation(MODID, "loading");
			ResourceLocation isLitResource = new ResourceLocation(MODID, "islit");

			for (DynamicGunItemProperties entry : loadedItems.values()) {
				ItemProperties.register(entry.getItemRegistry().get(), loadingResource, BaseGunItem::loadingPropertyFunction);
				ItemProperties.register(entry.getItemRegistry().get(), isLoadingResource, BaseGunItem::isLoadingPropertyFunction);
				ItemProperties.register(entry.getItemRegistry().get(), isLitResource, MatchlockGunItem::isLitPropertyFunction);
			}

		});
	}



	public void onRegister(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.ITEMS, (helper) -> {
			for(DynamicGunItemProperties entry : loadedItems.values()) {
				helper.register(entry.getResourceLocation(), entry.createItem());
			}
		});

		event.register(ForgeRegistries.Keys.ENTITY_TYPES, (helper) -> {
			helper.register(new ResourceLocation(MODID, "bullet"), EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC).build(""));
		});
	}

	public void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {

		if(event.getTab() == MATCHLOCKGUNS_CREATIVE_TAB.get()) {
			for(RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
				event.accept(entry);
			}

			for(DynamicGunItemProperties dynitem : loadedItems.values()) {
				event.accept(dynitem.getItemRegistry());
			}
		}
	}

	public void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(BULLET_ENTITY_TYPE, BulletEntityRenderer::new);
	}
}
