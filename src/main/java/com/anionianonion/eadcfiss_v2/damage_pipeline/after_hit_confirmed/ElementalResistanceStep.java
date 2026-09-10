package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
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

        Set<ResourceLocation> resistanceAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes("self", element, "resistance");
        float elementalResistance = AdvancedARPGAttributesAPI.getResult(livingDefender, resistanceAttributes);

        Set<ResourceLocation> penetrationAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes("self", element, "penetration");
        float elementalPenetration = AdvancedARPGAttributesAPI.getResult(livingAttacker, penetrationAttributes);

        Set<ResourceLocation> exposureAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes("self", element, "exposure");
        float elementalExposure = AdvancedARPGAttributesAPI.getResult(livingDefender, exposureAttributes);

        float finalResistance = elementalResistance - elementalPenetration - elementalExposure;
        return initialDamage * (1 - finalResistance);
    }

    @Override
    public String toString() {
        return "ElementalResistanceStep";
    }
}
