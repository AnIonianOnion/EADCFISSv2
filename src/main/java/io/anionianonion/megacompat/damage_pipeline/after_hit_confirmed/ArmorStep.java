package io.anionianonion.megacompat.damage_pipeline.after_hit_confirmed;

import io.anionianonion.advanced_arpg_attributes_api.StatContainer;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.damage_pipeline_api.DamageContext;
import io.anionianonion.damage_pipeline_api.api.IDamageStep;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ArmorStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {
        if (!damageContext.getElement().equals("physical")) return initialDamage;

        Set<ResourceLocation> armorAttributes = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations( "self", "defense", "armor");

        float armor = AdvancedARPGAttributesAPI.getResult(livingDefender, armorAttributes);

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

