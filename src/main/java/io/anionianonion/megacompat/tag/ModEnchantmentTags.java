package io.anionianonion.megacompat.tag;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantmentTags {

    public static TagKey<Enchantment> ELEMENT_CATEGORIES = create(String.format("%s:element_categories", AnIonianOnionsDamageMegacompatMod.MOD_ID));

    public static TagKey<Enchantment> create(String path) {
        return TagKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(path));
    }
}
