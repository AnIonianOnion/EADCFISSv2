package io.anionianonion.megacompat.util;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;

public class ModTags {

    public static TagKey<Item> PROJECTILE_WEAPONS_ENCHANTABLE = Helper.makeItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ranged_weapons"));

    public static TagKey<Item> MELEE_WEAPONS_ENCHANTABLE = Helper.makeItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "melee_weapons"));

    public static TagKey<Enchantment> PENETRATION_ENCHANTMENT_EXCLUSIVE = Helper.makeEnchantmentTagKey(Helper.ofThisMod("penetration_enchantment_exclusive"));
    public static TagKey<Enchantment> EXPOSURE_ENCHANTMENT_EXCLUSIVE = Helper.makeEnchantmentTagKey(Helper.ofThisMod("exposure_enchantment_exclusive"));

    public static HashMap<String, TagKey<Item>> WEAPONS_TO_TAGS = new HashMap<>();
}
