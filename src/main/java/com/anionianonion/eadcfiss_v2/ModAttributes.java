package com.anionianonion.eadcfiss_v2;

import com.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import com.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttributesMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModAttributes {

    static final DeferredRegister<Attribute> ATTRIBUTES_REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, AnIonianOnionsDamageMegacompatMod.MOD_ID);

    public static void register(IEventBus eventBus) {

        for(var aaaAtribute : AdvancedARPGAttribute.getAdvancedAttributesRegistry().keySet()) {

            var namespace = aaaAtribute.getNamespace();
            if(!namespace.equals(AnIonianOnionsDamageMegacompatMod.MOD_ID)) continue;

            var path = aaaAtribute.getPath();

            ATTRIBUTES_REGISTRY.register(path, () -> new RangedAttribute(String.format("attribute.%s", path), 0, 0, Double.POSITIVE_INFINITY));

        }

        ATTRIBUTES_REGISTRY.register(eventBus);
    }
}
