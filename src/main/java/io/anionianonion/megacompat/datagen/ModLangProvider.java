package io.anionianonion.megacompat.datagen;

import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.util.Helper;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLangProvider extends LanguageProvider {

    public ModLangProvider(PackOutput packOutput) {
        super(packOutput, AnIonianOnionsDamageMegacompatMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {

        //https://moddingwithryan.com/tutorials/ml-1-21-datagen-lang/
        //todo: Enchantment names
        for(var elementName : ElementalsAPI.getAllElementNames()) {
            add(String.format("enchantment.%s.increased_element_dmg.increased_%s_damage_enchantment", AnIonianOnionsDamageMegacompatMod.MOD_ID, elementName), String.format("Increased %s Damage", Helper.capitalize(elementName)));
            add(String.format("enchantment.%s.added_element_projectile_attack_dmg.added_%s_damage_enchantment", AnIonianOnionsDamageMegacompatMod.MOD_ID, elementName), String.format("Added %s Projectile Attack Damage", Helper.capitalize(elementName)));
            add(String.format("enchantment.%s.immunity.%s_immunity_enchantment", AnIonianOnionsDamageMegacompatMod.MOD_ID, elementName), String.format("%s Immunity", Helper.capitalize(elementName)));
        }
        //todo: Attribute names
    }
}
