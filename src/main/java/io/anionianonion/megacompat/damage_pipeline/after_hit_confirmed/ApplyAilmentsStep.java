package io.anionianonion.megacompat.damage_pipeline.after_hit_confirmed;

import io.anionianonion.advanced_arpg_attributes_api.StatContainer;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.damage_pipeline_api.DamageContext;
import io.anionianonion.damage_pipeline_api.api.IDamageStep;
import io.anionianonion.elementals_api.AilmentApplier;
import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.elementals_api.attachments.ModAttachments;
import io.anionianonion.elementals_api.data_classes.Ailment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class ApplyAilmentsStep implements IDamageStep {

    @Override
    public float apply(float initialDamage,
                       StatContainer attackerStatContainer, StatContainer defenderStatContainer,
                       LivingEntity livingAttacker, LivingEntity livingDefender,
                       DamageContext damageContext) {

        var ailmentModifiersContainer = livingAttacker.getData(ModAttachments.AILMENT_MODIFIERS);
        //AilmentModifiersContainer.logContainer(ailmentModifiersContainer);

        var ailmentIds = ailmentModifiersContainer.getAilmentsToInflictForWhichElement().get(damageContext.getElement());

        for(var ailmentId : ailmentIds) {
            var finalAilmentId = ailmentId;
            if(ailmentModifiersContainer.getAilmentReplacements().containsKey(ailmentId)) finalAilmentId = ailmentModifiersContainer.getAilmentReplacements().get(ailmentId);

            Ailment replacedAilment = ElementalsAPI.getAilment(ailmentId);
            if(replacedAilment.isGuaranteedInflictChance()) AilmentApplier.applyAilment(finalAilmentId, livingAttacker, livingDefender, (int) initialDamage);
            else if(replacedAilment.canBeInflictedFromCrit() && damageContext.isCrit()) AilmentApplier.applyAilment(finalAilmentId, livingAttacker, livingDefender, (int) initialDamage);
            else {
                var ailmentRoll = Math.random();

                Set<ResourceLocation> relatedAilmentAttributesRLs = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", ailmentId, "ailment", "chance");
                var ailmentChance = AdvancedARPGAttributesAPI.getResult(livingAttacker, relatedAilmentAttributesRLs);

                if(ailmentChance >= ailmentRoll) AilmentApplier.applyAilment(finalAilmentId, livingAttacker, livingDefender, (int) initialDamage);
            }
        }
        return initialDamage;
    }

    @Override
    public String toString() {
        return "ApplyAilmentsStep";
    }
}
