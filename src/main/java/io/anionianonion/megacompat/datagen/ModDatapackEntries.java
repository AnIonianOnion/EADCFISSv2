package io.anionianonion.megacompat.datagen;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.enchantment.ModEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

//https://www.youtube.com/watch?v=r7sEVrQsGtY&t=9m32s
public class ModDatapackEntries extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap);

    public ModDatapackEntries(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, BUILDER, Set.of(AnIonianOnionsDamageMegacompatMod.MOD_ID));
    }
}
