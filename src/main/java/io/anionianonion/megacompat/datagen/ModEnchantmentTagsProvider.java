package io.anionianonion.megacompat.datagen;

import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.util.Helper;
import io.anionianonion.megacompat.util.RLPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

        //these resource keys are all for enchantments
        for(var elementName : ElementalsAPI.getAllElementNames()) {
            var incElementDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.INCREASED_ELEMENT_DAMAGE, elementName);
            enchantmentRKs.add(incElementDamageRK);

            var addedElementProjectileAttackDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENTAL_PROJECTILE_ATTACK_DAMAGE, elementName);
            enchantmentRKs.add(addedElementProjectileAttackDamageRK);

            var elementImmunityRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ELEMENT_IMMUNITY, elementName);
            enchantmentRKs.add(elementImmunityRK);

            var addedElementAttackDamageRK = Helper.makeEnchantmentRK(RLPatterns.Enchantments.ADDED_ELEMENT_ATTACK_DAMAGE, elementName);
            enchantmentRKs.add(addedElementAttackDamageRK);
        }

        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addAll(enchantmentRKs);
    }
}
