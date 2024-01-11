package korallkarlsson.matchlockguns;

import korallkarlsson.matchlockguns.items.BaseGunItem;
import korallkarlsson.matchlockguns.items.MatchlockGunItem;
import korallkarlsson.matchlockguns.items.WheellockGunItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class DynamicGunItemProperties {


    final String name;
    int durability;
    int gunpowderAmount;
    int numShots;
    int chargeTime;
    int numProjectiles;
    int damage;
    float accuracy;
    float projectileSpeed;
    boolean canDualWield;
    boolean useRamRod;
    String type;
    float reliability;
    int cooldown;

    ResourceLocation resourceLocation;

    public DynamicGunItemProperties(String name, Map<String, String> map) {
        this.name = name;

        durability = Util.getIntEntry(map, "durability", 320);
        gunpowderAmount = Util.getIntEntry(map, "gunpowderAmount", 1);
        numShots = Util.getIntEntry(map, "maxShots", 1);
        chargeTime = Util.getIntEntry(map, "chargeTime", 30);
        numProjectiles = Util.getIntEntry(map, "numProjectiles", 1);
        damage = Util.getIntEntry(map, "damage", 12);
        accuracy = Util.getFloatEntry(map, "accuracy", 20);
        projectileSpeed = Util.getFloatEntry(map, "range", 4);
        canDualWield = Util.getBoolEntry(map, "canDualWield", false);
        useRamRod = Util.getBoolEntry(map, "useRamRod", true);
        type = Util.getStringEntry(map, "type", "flintlock");
        reliability = Util.getFloatEntry(map, "reliability", 0.99f);
        cooldown = Util.getIntEntry(map, "cooldown", 8);

        resourceLocation = new ResourceLocation(MatchlockGuns.MODID, name);
    }

    public BaseGunItem createItem() {
        return switch (type) {
            case "matchlock" -> new MatchlockGunItem(durability, gunpowderAmount, numShots, chargeTime, numProjectiles, damage, accuracy, projectileSpeed, canDualWield, useRamRod, reliability, cooldown);

            case "wheellock" -> new WheellockGunItem(durability, gunpowderAmount, numShots, chargeTime, numProjectiles, damage, accuracy, projectileSpeed, canDualWield, useRamRod, reliability, cooldown);

            default -> new BaseGunItem(durability, gunpowderAmount, numShots, chargeTime, numProjectiles, damage, accuracy, projectileSpeed, canDualWield, useRamRod, reliability, cooldown);
        };
    }

    public String getName() {
        return name;
    }

    ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    RegistryObject<Item> itemRegistry = null;

    public RegistryObject<Item> getItemRegistry() {
        if(itemRegistry == null)
            itemRegistry = RegistryObject.create(resourceLocation, ForgeRegistries.ITEMS);
        return itemRegistry;
    }

    public String getType() {
        return type;
    }
}
