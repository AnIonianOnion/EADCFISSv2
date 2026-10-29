package io.anionianonion.megacompat.util;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {

    public static TagKey<Item> PROJECTILE_WEAPONS_ENCHANTABLE = TagKey.create(Registries.ITEM,
            ResourceLocation.parse(String.format("%s:projectile_weapons", AnIonianOnionsDamageMegacompatMod.MOD_ID)));
}
