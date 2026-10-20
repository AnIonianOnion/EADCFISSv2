package io.anionianonion.megacompat.damage_pipeline.after_hit_confirmed;

import io.anionianonion.advanced_arpg_attributes_api.StatContainer;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.damage_pipeline_api.DamageContext;
import io.anionianonion.damage_pipeline_api.api.IDamageStep;
import io.anionianonion.megacompat.util.Helper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ElementalResistanceStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {

        var element = damageContext.getElement();
        if(element.equals("physical")) return initialDamage;

        Set<ResourceLocation> resistanceAttributes = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", element, "resistance");
        float elementalResistance = AdvancedARPGAttributesAPI.getResult(livingDefender, resistanceAttributes);
        Helper.info(element + " elementalRes: " + elementalResistance);

        Set<ResourceLocation> penetrationAttributes = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", element, "penetration");
        float elementalPenetration = AdvancedARPGAttributesAPI.getResult(livingAttacker, penetrationAttributes);

        Set<ResourceLocation> exposureAttributes = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", element, "exposure");
        float elementalExposure = AdvancedARPGAttributesAPI.getResult(livingDefender, exposureAttributes);

        float finalResistance = elementalResistance - elementalPenetration - elementalExposure;
        return initialDamage * (1 - finalResistance);
    }

    @Override
    public String toString() {
        return "ElementalResistanceStep";
    }
}
