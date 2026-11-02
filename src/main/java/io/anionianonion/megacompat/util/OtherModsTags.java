package io.anionianonion.megacompat.util;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class OtherModsTags {

    public static TagKey<Item> PROJECTILE_WEAPONS = Helper.makeItemTagKey("c", "ranged_weapons");
    public static TagKey<Item> MELEE_WEAPONS = Helper.makeItemTagKey("c", "melee_weapons");
    public static TagKey<Item> STAVES =  Helper.makeItemTagKey("c", "tools/staff");
    public static TagKey<Item> TRIDENTS = Helper.makeItemTagKey("c", "tools/trident");
    public static TagKey<Item> BOWS = Helper.makeItemTagKey("c", "tools/bow");
    public static TagKey<Item> CROSSBOWS = Helper.makeItemTagKey("c", "tools/crossbow");
    public static TagKey<Item> WEAPONS = Helper.makeItemTagKey(AnIonianOnionsDamageMegacompatMod.MOD_ID, "weapons");
    public static TagKey<Item> ARMOR = Helper.makeItemTagKey(AnIonianOnionsDamageMegacompatMod.MOD_ID, "armor");
    public static TagKey<Item> LEG_ARMOR = Helper.makeItemTagKey("c", "armor/leggings");
}
