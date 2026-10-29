package io.anionianonion.megacompat.datagen;

import io.anionianonion.megacompat.AnIonianOnionsDamageMegacompatMod;
import io.anionianonion.megacompat.util.Init;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AnIonianOnionsDamageMegacompatMod.MOD_ID)
public class DataGenerators {

    //everything I know so far about why it's not registering the enchantments to the in_enchantment_table tag:
    /*
        - Registry keys in ModEnchantments and ModEnchantmentTags provider *both* use RLPatterns.Enchantments, which helps to format
        the enchantment name via Helper.makeEnchantmentRK(pattern, name)
        - We also store the Enchantment at the address of the ResourceKey in ModEnchantments via register(), which works fine without the in_enchanting_table tag registration, which means that ModDataPackEntries also works fine.
        - Both the mod constructor, and ModAttributes.register() fire during runData, which is confirmed by logging. Therefore,
        the attributes that the enchantments provide modifiers for should exist.
        - And it does, because the continue statements in ModEnchantments never run, even when paired with a null-checker logging statement.
        - Maybe it's the ModEnchantmentTagsProvider, but I created the constructor matching super, and implemented the override, so I don't that's the issue.

        So ModEnchantments registers the attribute-enchantments properly, which means that the mod constructor initializes AdvancedARPGAttributes properly during runData,
        and the RK addresses are matching properly and ModDatapackEntries is also working correctly. But the enchantments aren't stored properly, as it can't be used in the ModEnchantmentTagsProvider.

        But if ModEnchantments bootstraps the enchantments to the resourcekey address that's used by the ModEnchantmentTags provider, and everything else is working except for adding tags, and this class is the last step which registers the providers, then there's soemthing wrong with this class.
     */

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        //https://github.com/Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X/blob/a6424f3fcd7bb24e892c86ec699af13256cc4aa6/src/main/java/net/kaupenjoe/tutorialmod/datagen/DataGenerators.java#L38
        //he renamed the ModDatapackEntries from the video to ModDatapackProvider.
        //there also is only one GatherDataEvent in the video, and no Client static inner class or Server in the video.
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        //https://github.com/Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X/blob/30-enchantment/src/main/java/net/kaupenjoe/tutorialmod/datagen/DataGenerators.java
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        //I had true originally, which works, but I'm going to follow his episode 30 github.
        //https://github.com/Tutorials-By-Kaupenjoe/NeoForge-Tutorial-1.21.X/blob/30-enchantment/src/main/java/net/kaupenjoe/tutorialmod/datagen/DataGenerators.java

        //I later noticed that Kaupenjoe explicitly defined a blocksTagProvider variable, to be used by the contentGetter for itemTags, which is similar to what I need,
        //so I tried accessing what I could from modDatapackEntries, which only leaves the registryProvider, so I tried reusing the lookup provider from modDatapackEntries, and it worked.

        //maybe because I am using data-genned enchantments is why I need to explicitly reset it, and the enchantment tags depend on it, is why I need to explicitly use the same lookup provider.
        var modDatapackEntries = new ModDatapackEntries(packOutput, lookupProvider);
        dataGenerator.addProvider(event.includeServer(), modDatapackEntries);

        //https://moddingwithryan.com/tutorials/ml-1-21-datagen-lang/
        dataGenerator.addProvider(event.includeClient(), new ModLangProvider(packOutput));

        //changed lookupProvider to modDatapackEntries.getRegistryProvider()
        dataGenerator.addProvider(event.includeServer(), new ModEnchantmentTagsProvider(packOutput, modDatapackEntries.getRegistryProvider(), existingFileHelper));
    }
}
