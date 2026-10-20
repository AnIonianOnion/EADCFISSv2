package io.anionianonion.megacompat.damage_pipeline.before_hit_confirmed;

import io.anionianonion.advanced_arpg_attributes_api.StatContainer;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.damage_pipeline_api.DamageContext;
import io.anionianonion.damage_pipeline_api.api.IPreHitDamageStep;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class SpellDodgeStep implements IPreHitDamageStep {

    @Override
    public boolean apply(StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                         LivingEntity livingAttacker, LivingEntity livingDefender,
                         DamageContext damageContext) {

        if(!(damageContext.getTags().contains("spell"))) return true;

        Set<ResourceLocation> spellDodgeChanceAttribute = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", "spell", "dodge", "chance");
        float spellDodgeChance = AdvancedARPGAttributesAPI.getResult(livingDefender, spellDodgeChanceAttribute);

        float dodgeRoll = (float) Math.random();

        //if the dodge roll is higher than the spell dodge chance, we continue the PreHit pipeline
        return spellDodgeChance < dodgeRoll;
    }
}
