package io.anionianonion.megacompat;

import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {

    static final DeferredRegister<Attribute> ATTRIBUTES_REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, AnIonianOnionsDamageMegacompatMod.MOD_ID);

    public static void register(IEventBus eventBus) {

        for(var aaaAtribute : AdvancedARPGAttributesAPI.getRegistry().keySet()) {
            var namespace = aaaAtribute.getNamespace();
            if(!namespace.equals(AnIonianOnionsDamageMegacompatMod.MOD_ID)) continue;

            var path = aaaAtribute.getPath();
            ATTRIBUTES_REGISTRY.register(path, () -> new RangedAttribute(String.format("attribute.%s.name", path), 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        }
        ATTRIBUTES_REGISTRY.register(eventBus);
    }
}
