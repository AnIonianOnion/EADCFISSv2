package io.anionianonion.megacompat.util;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class Helper {

    public static void info(String log) {
        AnIonianOnionsDamageMegacompatMod.LOGGER.info(log);
    }

    //https://stackoverflow.com/questions/3904579/how-to-capitalize-the-first-letter-of-a-string-in-java
    public static String capitalize(String all_lowercased_word) {
        return all_lowercased_word.substring(0, 1).toUpperCase() + all_lowercased_word.substring(1);
    }

    public static String format(String replaced, String... replacements) {
        return String.format(replaced, (Object[]) replacements);
    }

    public static String format(String replaced, List<String> replacements) {
        return String.format(replaced, (Object[]) replacements.toArray());
    }

    public static ResourceLocation ofThisMod(String path) {
        return ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, path);
    }

    public static ResourceLocation makeRL(String replaced, String... replacements) {
        return ofThisMod(format(replaced, replacements));
    }

    public static ResourceKey<Enchantment> makeEnchantmentRK(String replaced, String... replacements) {
        return ResourceKey.create(Registries.ENCHANTMENT, Helper.makeRL(replaced, replacements));
    }

    public static TagKey<Item> makeItemTagKey(ResourceLocation rl) {
        return TagKey.create(Registries.ITEM, rl);
    }

    public static TagKey<Enchantment> makeEnchantmentTagKey(ResourceLocation rl) {
        return TagKey.create(Registries.ENCHANTMENT, rl);
    }
}
