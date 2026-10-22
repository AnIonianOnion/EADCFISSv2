package io.anionianonion.megacompat.datagen;

import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

        List<ResourceKey<Enchantment>> increasedElementDamageEnchantmentRKs = new ArrayList<>();

        for(var elementName : ElementalsAPI.getAllElementNames()) {
            var resourceKey = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(AnIonianOnionsDamageMegacompatMod.MOD_ID, String.format("increased_element_dmg/%s", elementName)));
            increasedElementDamageEnchantmentRKs.add(resourceKey);
        }

        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addAll(increasedElementDamageEnchantmentRKs);
    }
}
