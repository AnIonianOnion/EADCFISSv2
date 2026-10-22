package io.anionianonion.megacompat.util;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.resources.ResourceLocation;

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

    public static ResourceLocation ofThisMod(String path) {
        return ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, path);
    }

    public static ResourceLocation makeRL(String replaced, String... replacements) {
        return ofThisMod(format(replaced, replacements));

    }
}
