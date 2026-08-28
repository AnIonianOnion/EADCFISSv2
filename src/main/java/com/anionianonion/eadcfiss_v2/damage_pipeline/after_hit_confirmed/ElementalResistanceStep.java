package com.anionianonion.eadcfiss_v2.damage_pipeline.after_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
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
                       LivingEntity attacker, LivingEntity defender,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       Entity directEntity, DamageContext damageContext) {
        var element = damageContext.getElement();
        if(element.equals("physical")) return initialDamage;

        AdvancedARPGAttributesAPI api = new AdvancedARPGAttributesAPI();

        Set<ResourceLocation> resistanceAttributes = api.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), element, "resistance");
        float elementalResistance = api.getResult(defenderStatContainer, resistanceAttributes);

        Set<ResourceLocation> penetrationAttributes = api.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), element, "penetration");
        float elementalPenetration = api.getResult(attackerStatContainer, penetrationAttributes);

        Set<ResourceLocation> exposureAttributes = api.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), element, "exposure");
        float elementalExposure = api.getResult(defenderStatContainer, exposureAttributes);

        float finalResistance = elementalResistance - elementalPenetration - elementalExposure;
        return initialDamage * (1 - finalResistance);
    }

    @Override
    public String toString() {
        return "ElementalResistanceStep";
    }
}
