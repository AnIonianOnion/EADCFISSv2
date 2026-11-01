package io.anionianonion.megacompat.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;

public class ModTags {

    //hypothesis: enchantable tags are separate tags that contains a set of the weapon or armor type, as well as a book.
    public static TagKey<Item> PROJECTILE_WEAPONS_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("projectile_weapons_enchantable"));
    public static TagKey<Item> MELEE_WEAPONS_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("melee_weapons_enchantable"));
    public static TagKey<Item> AXE_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("axe_items_enchantable"));
    public static TagKey<Item> STAFF_ENCHANTABLE = Helper.makeItemTagKey(Helper.ofThisMod("staff_items_enchantable"));

    public static TagKey<Enchantment> PENETRATION_ENCHANTMENT_EXCLUSIVE = Helper.makeEnchantmentTagKey(Helper.ofThisMod("penetration_enchantment_exclusive"));
    public static TagKey<Enchantment> EXPOSURE_ENCHANTMENT_EXCLUSIVE = Helper.makeEnchantmentTagKey(Helper.ofThisMod("exposure_enchantment_exclusive"));

    public static HashMap<String, TagKey<Item>> WEAPON_TYPE_IDS_TO_TAGS = new HashMap<>();
}
