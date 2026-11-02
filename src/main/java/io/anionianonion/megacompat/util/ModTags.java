package io.anionianonion.megacompat.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;

public class ModTags {

    //hypothesis: enchantable tags are separate tags that contains a set of the weapon or armor type, as well as a book.
    //I was kinda correct, but it's not implicit.
    // You have to manually define the enchantable tag as containing the weapon or armor type as well as a book.
    // When defining the enchantments, enchantable tags should be used as the supported item tag, while the primary tags should be the weapon or armor types as an example.
    public static TagKey<Item> PROJECTILE_WEAPONS_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("enchantable/projectile_weapons"));
    public static TagKey<Item> MELEE_WEAPONS_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("enchantable/melee_weapons"));
    public static TagKey<Item> AXE_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("enchantable/axe"));
    public static TagKey<Item> STAFF_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("enchantable/staff"));

    public static TagKey<Enchantment> PENETRATION_ENCHANTMENT_EXCLUSIVE = Helper.makeEnchantmentTagKey(Helper.ofThisMod("exclusive_set/penetration"));
    public static TagKey<Enchantment> EXPOSURE_ENCHANTMENT_EXCLUSIVE = Helper.makeEnchantmentTagKey(Helper.ofThisMod("exclusive_set/exposure"));

    public static HashMap<String, TagKey<Item>> WEAPON_TYPE_IDS_TO_ENCHANTABLE_TAGS = new HashMap<>();
    public static HashMap<String, TagKey<Item>> WEAPON_TYPE_IDS_TO_ACTUAL_TAGS = new HashMap<>();
}
