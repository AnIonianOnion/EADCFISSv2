package io.anionianonion.megacompat;

import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.BooleanAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES_REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, AnIonianOnionsDamageMegacompatMod.MOD_ID);

    public static void register(IEventBus eventBus) {

        AnIonianOnionsDamageMegacompatMod.LOGGER.info("Hello, runData from ModAttributes.register!");
        for(var aaaAtributeRL : AdvancedARPGAttributesAPI.getRegistry().keySet()) {
            var namespace = aaaAtributeRL.getNamespace();
            if(!namespace.equals(AnIonianOnionsDamageMegacompatMod.MOD_ID)) continue;

            var path = aaaAtributeRL.getPath();
            if(!path.contains("immunity")) {
                ATTRIBUTES_REGISTRY.register(path, () -> new RangedAttribute(String.format("attribute.%s.%s.name", namespace, path), 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
            }
            else {
                ATTRIBUTES_REGISTRY.register(path, () -> new BooleanAttribute(String.format("attribute.%s.%s.name", namespace, path), false));
            }
        }
        ATTRIBUTES_REGISTRY.register(eventBus);
    }
}
