package com.anionianonion.eadcfiss_v2;

import com.anionianonion.advanced_arpg_attributes_api.commands.SeeModifiersCommand;
import com.anionianonion.eadcfiss_v2.commands.GetAAAttributesCommand;
import com.anionianonion.eadcfiss_v2.commands.GetAllSpellsCommand;
import com.anionianonion.eadcfiss_v2.commands.SummonMinionCommand;
import com.anionianonion.eadcfiss_v2.util.Init;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AnIonianOnionsDamageMegacompatMod.MOD_ID)
public class AnIonianOnionsDamageMegacompatMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "anionianonions_damage_megacompat";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AnIonianOnionsDamageMegacompatMod(FMLJavaModLoadingContext context)
    {
        Init.init();

        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        ModAttributes.register(modEventBus);
        modEventBus.addListener(this::addAttributesToLivingEntities);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) ->
                {
                    GetAllSpellsCommand.register(e.getDispatcher());
                    SummonMinionCommand.register(e.getDispatcher());
                    GetAAAttributesCommand.register(e.getDispatcher());
                }
        );
    }

    private void addAttributesToLivingEntities(EntityAttributeModificationEvent event) {

        /*
        //registers attributes to only players
        for (var attribute : ModAttributes.ATTRIBUTES_REGISTRY.getEntries()) {
            event.add(EntityType.PLAYER, attribute.get());
        }
         */

        //all living entities
        for (var attribute : ModAttributes.ATTRIBUTES_REGISTRY.getEntries()) {
            for(var type : event.getTypes()) {
                event.add(type, attribute.get());
            }
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
