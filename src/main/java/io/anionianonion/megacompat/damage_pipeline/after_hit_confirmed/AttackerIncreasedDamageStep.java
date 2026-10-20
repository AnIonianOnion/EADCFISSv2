package io.anionianonion.megacompat.damage_pipeline.after_hit_confirmed;

import io.anionianonion.advanced_arpg_attributes_api.StatContainer;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.damage_pipeline_api.DamageContext;
import io.anionianonion.damage_pipeline_api.api.IDamageStep;
import net.minecraft.world.entity.LivingEntity;

public class AttackerIncreasedDamageStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {

        var filteredAttributes = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations(damageContext.getTags());
        return initialDamage * (1 + AdvancedARPGAttributesAPI.getData(livingAttacker, filteredAttributes)[1]);
    }
}
