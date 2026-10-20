package io.anionianonion.megacompat;

import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.megacompat.enchantment.ModEnchantments;
import io.anionianonion.megacompat.util.Init;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(AnIonianOnionsDamageMegacompatMod.MOD_ID)
public class AnIonianOnionsDamageMegacompatMod {
    public static final String MOD_ID = "anionianonions_damage_megacompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AnIonianOnionsDamageMegacompatMod(IEventBus modEventBus, ModContainer modContainer) {

        Init.init();

        ModAttributes.register(modEventBus);

        modEventBus.addListener(AnIonianOnionsDamageMegacompatMod::attachAttributes);
    }

    private static void attachAttributes(EntityAttributeModificationEvent e) {
        for(var rl : AdvancedARPGAttributesAPI.getRegistry().keySet()) {

            for(var livingEntityType : e.getTypes()) {
                var attribute = BuiltInRegistries.ATTRIBUTE.get(rl);
                if(attribute == null) continue;

                var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
                if(e.has(livingEntityType, holder)) continue;

                e.add(livingEntityType, holder);
            }
        }

    }
}
