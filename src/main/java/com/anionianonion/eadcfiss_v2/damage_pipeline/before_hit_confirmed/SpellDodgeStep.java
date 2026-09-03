package com.anionianonion.eadcfiss_v2.damage_pipeline.before_hit_confirmed;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.api.IPreHitDamageStep;
import com.anionianonion.eadcfiss_v2.util.Helpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class SpellDodgeStep implements IPreHitDamageStep {

    @Override
    public boolean apply(StatContainer attackerStatContainer, StatContainer defenderStatContainer, DamageContext damageContext) {

        if(!(damageContext.getTags().contains("spell"))) return true;

        Set<ResourceLocation> spellDodgeChanceAttribute = AdvancedARPGAttributesAPI.getFilteredAttributes(Helpers.getSelfOrMinion(damageContext), "spell", "dodge", "chance");
        float spellDodgeChance = AdvancedARPGAttributesAPI.getResult(defenderStatContainer, spellDodgeChanceAttribute);

        float dodgeRoll = (float) Math.random();

        //if the dodge roll is higher than the spell dodge chance, we continue the PreHit pipeline
        return spellDodgeChance < dodgeRoll;
    }
}
