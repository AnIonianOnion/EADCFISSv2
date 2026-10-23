package io.anionianonion.megacompat.util;

import io.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import io.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import io.anionianonion.advanced_arpg_attributes_api.attachments.ModAttachments;
import io.anionianonion.elementals_api.api.ElementalsAPI;
import io.anionianonion.elementals_api.data_classes.Ailment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import java.util.Objects;
import java.util.Set;

public class RegisterAilmentsHelper {

    static void regScorch() {
        Ailment scorch = new Ailment("scorch");
        scorch.setCanBeInflictedFromCrit(true);
        scorch.setMaxStacksCount(1);
        scorch.setAbsoluteMaxStacksCount(2);
        scorch.setDurationInSeconds(40);
        scorch.setEffectStrengthFunction(ailmentInstance -> {

            var attacker = ailmentInstance.attacker;
            var defender = ailmentInstance.defender;

            var damage = ailmentInstance.sourceDamage;
            var threshold = defender.getMaxHealth() * 0.5;

            var nonDamagingAilmentEffectStrengthAttributeRLs = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", "nondamaging", "scorch", "ailment", "effect_strength");
            var data = AdvancedARPGAttributesAPI.getData(attacker, nonDamagingAilmentEffectStrengthAttributeRLs);

            return (float) (0.5f * Math.pow((damage / threshold), 0.4) * (1f + data[1]) * (1f + data[2]));
        });
        scorch.setDefenderOnApply(ailmentInstance -> {
            var defender = ailmentInstance.defender;

            var statContainer = defender.getData(ModAttachments.STAT_CONTAINER.get());

            for(var elementId : ElementalsAPI.getAllElementNames()) {
                Set<ResourceLocation> attributeRLs = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", elementId, "resistance");
                for(var rl : attributeRLs) {
                    var attribute = BuiltInRegistries.ATTRIBUTE.get(rl);
                    if(attribute == null) continue;

                    var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
                    if(defender.getAttribute(holder) == null) continue;

                    AdvancedARPGAttribute aaattribute = AdvancedARPGAttributesAPI.getRegistry().get(rl);

                    //https://forums.minecraftforge.net/topic/120285-attribute-modifier-uuids-clarification/
                    var modifier = new AttributeModifier(
                            rl,
                            -ailmentInstance.getEffectStrength(),
                            AttributeModifier.Operation.ADD_VALUE);

                    Objects.requireNonNull(defender.getAttribute(holder)).addOrReplacePermanentModifier(
                           modifier
                    );

                    Helper.info("rl to String: " + rl);
                }
            }

            AdvancedARPGAttributesAPI.logDataFromStatContainer(statContainer);
        });
        scorch.setDefenderOnExpire(ailmentInstance -> {
            var defender = ailmentInstance.defender;

            for(var elementId : ElementalsAPI.getAllElementNames()) {
                Set<ResourceLocation> attributeRLs = AdvancedARPGAttributesAPI.getFilteredAttributesResourceLocations("self", elementId, "resistance");
                for(var rl : attributeRLs) {
                    var attribute = BuiltInRegistries.ATTRIBUTE.get(rl);
                    if(attribute == null) continue;

                    var holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
                    if(defender.getAttribute(holder) == null) continue;

                    AdvancedARPGAttribute aaattribute = AdvancedARPGAttributesAPI.getRegistry().get(rl);

                    //todo: don't remove all modifiers, and only by UUID
                    Objects.requireNonNull(defender.getAttribute(holder)).removeModifier(rl);
                    Helper.info(rl + " modifer removed");
                }
            }
        });
        ElementalsAPI.regAilment(scorch);
    }

    static void regIgnite() {
        Ailment ignite = new Ailment("ignite");
        ignite.setDamagingAilment(true);
        ignite.setDurationInSeconds(4);
        ignite.setGuaranteeInflictChance(false);
        ignite.setMaxStacksCount(1);
        ignite.setAbsoluteMaxStacksCount(2);
        ignite.setRatioOfDPStoHitDamage(0.9f);
        ignite.setDefenderOnTick(ailmentInstance -> {
            float totalDPS = ailmentInstance.getRatioOfDPStoHitDamage() * ailmentInstance.getFinalDPS();
            if(ailmentInstance.getRemainingDurationInTicks() % 20 == 0) ElementalsAPI.hurtWithAilment(ailmentInstance.attacker, ailmentInstance.defender, totalDPS);
        });
        ElementalsAPI.regAilment(ignite);
        ElementalsAPI.pairAilmentToElement("ignite", "fire");
    }

    static void regBleed() {
        Ailment bleed = new Ailment("bleed");
        //bleed.setRatioOfDPStoHitDamage(0.7f);
        bleed.setDefenderOnTick((ailmentInstance) -> {
            var defender = ailmentInstance.defender;
            double targetVelocity = defender.getDeltaMovement().horizontalDistance();
            //if not moving, multiplier for bleed is 0.7x of the hit damage from the ailment instance.
            //if moving, it increases to 2.1x.
            float movementMultiplier = (float) (targetVelocity > 0.0625 && (defender.xOld != defender.getX() || defender.zOld != defender.getZ()) ? 2.1 : 0.7);

            float totalDPS = ailmentInstance.getFinalDPS() * movementMultiplier;

            if(ailmentInstance.getRemainingDurationInTicks() % 20 == 0) ElementalsAPI.hurtWithAilment(ailmentInstance.attacker, defender, totalDPS);
        });
        ElementalsAPI.regAilment(bleed);
        ElementalsAPI.pairAilmentToElement("bleed", "physical");
    }
}
