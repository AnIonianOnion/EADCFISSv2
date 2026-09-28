package com.anionianonion.eadcfiss_v2.util;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.elementals_api.api.ElementalsAPI;
import com.anionianonion.elementals_api.data_classes.Ailment;
import com.anionianonion.elementals_api.util.RandomHelpers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
            var threshold = defender.getMaxHealth();

            var nonDamagingAilmentEffectStrengthAttributeRLs = AdvancedARPGAttributesAPI.getFilteredAttributes("nondamaging", "ailment", "effect_strength");
            var data = AdvancedARPGAttributesAPI.getData(attacker, nonDamagingAilmentEffectStrengthAttributeRLs);

            return //(float) (0.5f * Math.pow((damage / threshold), 0.4) * (1f + data[1]) * (1f + data[2]));
            0.5f;
        });
        scorch.setDefenderOnApply(ailmentInstance -> {
            var defender = ailmentInstance.defender;

            var statContainer = defender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            if(statContainer == null) return;

            for(var elementId : ElementalsAPI.getAllElementNames()) {
                Set<ResourceLocation> attributeRLs = AdvancedARPGAttributesAPI.getFilteredAttributes("self", elementId, "resistance");
                for(var rl : attributeRLs) {
                    var attribute = ForgeRegistries.ATTRIBUTES.getValue(rl);
                    if(attribute == null) continue;
                    if(defender.getAttribute(attribute) == null) continue;

                    //https://forums.minecraftforge.net/topic/120285-attribute-modifier-uuids-clarification/
                    var modifier = new AttributeModifier(
                            UUID.fromString("920a8a9d-e1ea-4a7d-b3de-1d8f150b47a3"),
                            "scorch",
                            -ailmentInstance.getEffectStrength(),
                            AttributeModifier.Operation.ADDITION);

                    Objects.requireNonNull(defender.getAttribute(attribute)).addTransientModifier(
                           modifier
                    );

                    statContainer.addModifier(modifier, rl.toString());
                    Helper.info("rl to String: " + rl);
                }
            }

            AdvancedARPGAttributesAPI.logDataFromStatContainer(statContainer);
        });
        scorch.setDefenderOnExpire(ailmentInstance -> {
            var defender = ailmentInstance.defender;

            for(var elementId : ElementalsAPI.getAllElementNames()) {
                Set<ResourceLocation> attributeRLs = AdvancedARPGAttributesAPI.getFilteredAttributes("self", elementId, "resistance");
                for(var rl : attributeRLs) {
                    var attribute = ForgeRegistries.ATTRIBUTES.getValue(rl);
                    if(attribute == null) continue;
                    if(defender.getAttribute(attribute) == null) continue;
                    Objects.requireNonNull(defender.getAttribute(attribute)).removeModifier(
                            UUID.fromString("920a8a9d-e1ea-4a7d-b3de-1d8f150b47a3")
                    );
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
            float totalDPS = ailmentInstance.getRatioOfDPStoHitDamage() * ailmentInstance.getFinalDPSPerStack();
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

            float totalDPS = ailmentInstance.getFinalDPSPerStack() * movementMultiplier;

            if(ailmentInstance.getRemainingDurationInTicks() % 20 == 0) ElementalsAPI.hurtWithAilment(ailmentInstance.attacker, defender, totalDPS);
        });
        ElementalsAPI.regAilment(bleed);
        ElementalsAPI.pairAilmentToElement("bleed", "physical");
    }
}
