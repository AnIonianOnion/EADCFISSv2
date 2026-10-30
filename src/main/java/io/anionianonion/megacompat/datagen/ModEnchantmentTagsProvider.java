package io.anionianonion.megacompat.datagen;

import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.util.Helper;
import io.anionianonion.megacompat.util.ModTags;
import io.anionianonion.megacompat.util.RLPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

//based on https://github.com/Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X/blob/30-enchantment/src/main/java/net/kaupenjoe/tutorialmod/datagen/ModBlockTagProvider.java
public class ModEnchantmentTagsProvider
        extends EnchantmentTagsProvider
{
    public ModEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AnIonianOnionsDamageMegacompatMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        List<ResourceKey<Enchantment>> enchantmentRKs = new ArrayList<>();

        var elementNames = ElementalsAPI.getAllElementNames();
        var weaponTypeIds = AdvancedARPGAttributesAPI.getValidWeaponTags();

        //these resource keys are all for enchantments
        for(var elementName : elementNames) {
            var incElementDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.INCREASED_ELEMENT_DAMAGE, elementName);
            enchantmentRKs.add(incElementDamageRK);

            var addedElementProjectileAttackDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE, elementName);
            enchantmentRKs.add(addedElementProjectileAttackDamageRK);

            var elementImmunityRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ELEMENT_IMMUNITY, elementName);
            enchantmentRKs.add(elementImmunityRK);

            var addedElementAttackDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_ATTACK_DAMAGE, elementName);
            enchantmentRKs.add(addedElementAttackDamageRK);

            for(var weaponTypeId : weaponTypeIds) {
                var addedWeaponElementAttackDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_WEAPON_ELEMENT_ATTACK_DAMAGE, weaponTypeId, elementName);
                enchantmentRKs.add(addedWeaponElementAttackDamageRK);
            }

            var addedElementSpellDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_SPELL_DAMAGE, elementName);
            enchantmentRKs.add(addedElementSpellDamageRK);
        }

        var elementNamesMinusPhysical = new HashSet<>(elementNames);
        elementNamesMinusPhysical.remove("physical");

        for(String elementName : elementNamesMinusPhysical) {
            var addedElementMeleeAttackDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_MELEE_ATTACK_DAMAGE, elementName);
            enchantmentRKs.add(addedElementMeleeAttackDamageRK);

            var addedElementResistanceRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_RESISTANCE, elementName);
            enchantmentRKs.add(addedElementResistanceRK);

            var addedElementPenetrationRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_PENETRATION, elementName);
            enchantmentRKs.add(addedElementPenetrationRK);
            tag(ModTags.PENETRATION_ENCHANTMENT_EXCLUSIVE)
                    .add(addedElementPenetrationRK);

            var addedElementExposureRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_EXPOSURE, elementName);
            enchantmentRKs.add(addedElementExposureRK);
            tag(ModTags.EXPOSURE_ENCHANTMENT_EXCLUSIVE)
                    .add(addedElementExposureRK);
        }

        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addAll(enchantmentRKs);
    }
}
