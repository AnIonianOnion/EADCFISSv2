package io.anionianonion.megacompat.datagen;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.util.Helper;
import io.anionianonion.megacompat.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Deprecated
//based on https://github.com/Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X/blob/30-enchantment/src/main/java/net/kaupenjoe/tutorialmod/datagen/ModItemTagProvider.java
public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, AnIonianOnionsDamageMegacompatMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.AXE_ENCHANTABLE)
                .addTag(Helper.makeItemTagKey(ResourceLocation.tryBuild("c", "tools/axes")))
                .add(Items.BOOK);

        tag(ModTags.STAFF_ENCHANTABLE)
                .addTag(Helper.makeItemTagKey(ResourceLocation.tryBuild("c", "tools/staff")))
                .add(Items.BOOK);

        tag(ModTags.PROJECTILE_WEAPONS_ENCHANTABLE)
                .addTag(Helper.makeItemTagKey(ResourceLocation.tryBuild("c", "ranged_weapons")))
                .add(Items.BOOK);

        tag(ModTags.MELEE_WEAPONS_ENCHANTABLE)
                .addTag(Helper.makeItemTagKey(ResourceLocation.tryBuild("c", "melee_weapons")))
                .add(Items.BOOK);
    }
}
