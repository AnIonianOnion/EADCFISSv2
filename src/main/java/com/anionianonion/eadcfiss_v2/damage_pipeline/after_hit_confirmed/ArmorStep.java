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

public class ArmorStep implements IDamageStep {

    public float apply(float initialDamage,
                       LivingEntity attacker, LivingEntity defender,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       Entity directEntity, DamageContext damageContext) {
        if (!damageContext.getElement().equals("physical")) return initialDamage;

        AdvancedARPGAttributesAPI api = new AdvancedARPGAttributesAPI();

        Set<ResourceLocation> armorAttributes = api.getFilteredAttributes( Helpers.getSelfOrMinion(damageContext), "defense", "armor");

        float armor = api.getResult(defenderStatContainer, armorAttributes);

        if (initialDamage <= 0) return 0;

        float denom = armor + 5 * initialDamage;
        if (denom <= 0) return initialDamage;

        float reduction = Math.min(armor / denom, 0.90f);
        return initialDamage * (1 - reduction);
    }

    @Override
    public String toString() {
        return "ArmorStep";
    }
}

