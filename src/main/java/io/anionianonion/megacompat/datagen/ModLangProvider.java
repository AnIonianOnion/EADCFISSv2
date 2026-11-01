package io.anionianonion.megacompat.datagen;

import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.util.Helper;
import io.anionianonion.megacompat.util.ModTags;
import io.anionianonion.megacompat.util.RLPatterns;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ModLangProvider extends LanguageProvider {

    public ModLangProvider(PackOutput packOutput) {
        super(packOutput, AnIonianOnionsDamageMegacompatMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        var elementNames = ElementalsAPI.getAllElementNames();
        //https://moddingwithryan.com/tutorials/ml-1-21-datagen-lang/
        var weaponNames = AdvancedARPGAttributesAPI.getValidWeaponTags();

        for(var elementName : elementNames) {
            addEnchantmentTranslation(RLPatterns.Enchantments.INCREASED_ELEMENT_DAMAGE, "Increased %s Damage", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_DAMAGE, "%s Damage", elementName);

            addEnchantmentTranslation(RLPatterns.Enchantments.ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE, "Added %s Damage to Projectile Attacks", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_PROJECTILE_ATTACK_DAMAGE, "%s Damage to Projectile Attacks", elementName);

            addEnchantmentTranslation(RLPatterns.Enchantments.ELEMENT_IMMUNITY, "%s Immunity", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_IMMUNITY, "Immune to %s Damage", elementName);

            addEnchantmentTranslation(RLPatterns.Enchantments.ADDED_ELEMENT_ATTACK_DAMAGE, "Added %s Damage to Attacks", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_ATTACK_DAMAGE,"%s Damage to Attacks", elementName);

            for(var weaponName : weaponNames) {
                addEnchantmentTranslation(RLPatterns.Enchantments.ADDED_WEAPON_ELEMENT_ATTACK_DAMAGE, List.of(weaponName, elementName),
                        "Added %s Damage to %s Attacks", List.of(elementName, weaponName));
                addAttributeTranslation(RLPatterns.Attributes.WEAPON_ELEMENT_ATTACK_DAMAGE, List.of(weaponName, elementName),
                        "%s Damage to %s Attacks", List.of(elementName, weaponName));
            }
        }

        var elementNamesMinusPhysical = new HashSet<>(elementNames);
        elementNamesMinusPhysical.remove("physical");

        for(String elementName : elementNamesMinusPhysical) {
            addEnchantmentTranslation(RLPatterns.Enchantments.ADDED_ELEMENT_MELEE_ATTACK_DAMAGE, "Added %s Damage to Melee Attacks", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_MELEE_ATTACK_DAMAGE, "%s Damage to Melee Attacks", elementName);

            addEnchantmentTranslation(RLPatterns.Enchantments.ADDED_ELEMENT_RESISTANCE, "Added %s Resistance", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_RESISTANCE, "%s Resistance", elementName);

            addEnchantmentTranslation(RLPatterns.Enchantments.ADDED_ELEMENT_PENETRATION, "Added %s Penetration", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_PENETRATION, "%s Penetration", elementName);

            addEnchantmentTranslation(RLPatterns.Enchantments.ADDED_ELEMENT_EXPOSURE, "Added %s Exposure", elementName);
            addAttributeTranslation(RLPatterns.Attributes.ELEMENT_EXPOSURE, "%s Exposure", elementName);
        }

        for(String weaponName : weaponNames) {
            addEnchantmentTranslation(RLPatterns.Enchantments.INCREASED_WEAPON_DAMAGE, "Increased %s Damage", weaponName);
            addAttributeTranslation(RLPatterns.Attributes.WEAPON_DAMAGE, "%s Damage", weaponName);
        }
    }

    /*
    From this mod only. Not any other mod.
     */
    private void addEnchantmentTranslation(String translationKey, String translationPattern, String... translationKeyReplacements) {
        List<String> translationReplacements = new ArrayList<>();

        for(String replace : translationKeyReplacements) {
            translationReplacements.add(Helper.capitalize(replace));
        }

        add(
                String.format("enchantment.%s.", AnIonianOnionsDamageMegacompatMod.MOD_ID)
                        + Helper.format(translationKey.replace("/", "."), translationKeyReplacements),
                Helper.format(translationPattern, translationReplacements));
    }
    /*
    From this mod only. Not any other mod.
     */
    private void addEnchantmentTranslation(String translationKey, List<String> translationKeyReplacements, String translationPattern, List<String> translationReplacements) {
        List<String> finalList = new ArrayList<>();

        for(String replace : translationReplacements) {
            finalList.add(Helper.capitalize(replace));
        }

        add(
                String.format("enchantment.%s.", AnIonianOnionsDamageMegacompatMod.MOD_ID)
                        + Helper.format(translationKey.replace("/", "."), translationKeyReplacements),
                Helper.format(translationPattern, finalList));
    }
    /*
    From this mod only. Not any other mod.
     */
    private void addAttributeTranslation(String translationKey, String translationPattern, String... translationKeyReplacements) {
        List<String> translationReplacements = new ArrayList<>();

        for(String replace : translationKeyReplacements) {
            translationReplacements.add(Helper.capitalize(replace));
        }

        add(
                String.format("attribute.%s.", AnIonianOnionsDamageMegacompatMod.MOD_ID)
                        + Helper.format(translationKey, translationKeyReplacements) + ".name",
                Helper.format(translationPattern, translationReplacements));
    }
    /*
    From this mod only. Not any other mod.
     */
    private void addAttributeTranslation(String translationKey, List<String> translationKeyReplacements, String translationPattern, List<String> translationReplacements) {
        List<String> finalList = new ArrayList<>();

        for(String replace : translationReplacements) {
            finalList.add(Helper.capitalize(replace));
        }
        add(
                String.format("attribute.%s.", AnIonianOnionsDamageMegacompatMod.MOD_ID)
                        + Helper.format(translationKey, translationKeyReplacements) + ".name",
                Helper.format(translationPattern, finalList));
    }

}
