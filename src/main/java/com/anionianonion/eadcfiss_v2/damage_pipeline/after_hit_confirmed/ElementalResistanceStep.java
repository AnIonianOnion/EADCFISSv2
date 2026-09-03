package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IDamageStep;
import com.anionianonion.eadcfiss_v2.util.Helpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ElementalResistanceStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       DamageContext damageContext) {

        var element = damageContext.getElement();
        if(element.equals("physical")) return initialDamage;

        Set<ResourceLocation> resistanceAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), element, "resistance");
        float elementalResistance = AdvancedARPGAttributesAPI.getResult(defenderStatContainer, resistanceAttributes);

        Set<ResourceLocation> penetrationAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), element, "penetration");
        float elementalPenetration = AdvancedARPGAttributesAPI.getResult(attackerStatContainer, penetrationAttributes);

        Set<ResourceLocation> exposureAttributes = AdvancedARPGAttributesAPI.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), element, "exposure");
        float elementalExposure = AdvancedARPGAttributesAPI.getResult(defenderStatContainer, exposureAttributes);

        float finalResistance = elementalResistance - elementalPenetration - elementalExposure;
        return initialDamage * (1 - finalResistance);
    }

    @Override
    public String toString() {
        return "ElementalResistanceStep";
    }
}
