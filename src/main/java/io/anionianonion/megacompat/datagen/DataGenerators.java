package io.anionianonion.megacompat.datagen;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AnIonianOnionsDamageMegacompatMod.MOD_ID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        //https://github.com/Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X/blob/a6424f3fcd7bb24e892c86ec699af13256cc4aa6/src/main/java/net/kaupenjoe/tutorialmod/datagen/DataGenerators.java#L38
        //he renamed the ModDatapackEntries from the video to ModDatapackProvider.
        //there also is only one GatherDataEvent and no Client static inner class or Server.
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        dataGenerator.addProvider(true, new ModDatapackEntries(packOutput, lookupProvider));
    }
}
