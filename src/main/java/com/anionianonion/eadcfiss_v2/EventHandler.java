package com.anionianonion.eadcfiss_v2;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.DamagePipeline;
import com.anionianonion.damage_pipeline_api.capability.DamageContextCapability;
import com.anionianonion.elementals_api.api.ElementalsAPI;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.anionianonion.damage_pipeline_api.api.DamagePipelineAPI.determineAndAddWeaponDamageTagToContext;

@Mod.EventBusSubscriber(modid = AnIonianOnionsDamageMegacompatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {


    //decided the best way (I know of) to make our DamageContext's instance reusable almost anywhere, is to store it as a capability onto the player, rather than have it live only inside events. Because
    //Forge or Minecraft's damage pipeline is split into three events: LivingAttackEvent, LivingHurtEvent and LivingDamageEvent, and we need some way to access a global pipeline,
    // because we can't go through an entire pipeline for each split Minecraft/Forge's own pipeline / different damageContext each time for each event.

    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent e) {
        //This event triggers before all the other damage events
        var damageSource = e.getSpellDamageSource().get();
        if(!(damageSource.getEntity() instanceof LivingEntity livingCaster)) return;

        var livingDefender = e.getEntity();

        DamageContext damageContext = livingCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingCasterStatContainer = livingCaster.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingCasterStatContainer == null || livingDefenderStatContainer == null) return;

        var spell = e.getSpellDamageSource().spell();
        //technically the spell school, but it's alr.
        var spellElement = spell.getSchoolType().getId().getPath();
        ResourceLocation spellResourceLocation = spell.getSpellResource();
        //var spellId = spellResourceLocation.getNamespace() + ":" + spellResourceLocation.getPath();

        var baseDamageModifier = new AttributeModifier(UUID.randomUUID(), "base damage of spell", e.getOriginalAmount(), AttributeModifier.Operation.ADDITION);
        String attributeId = String.format("%s:%s_spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, spellElement);
        livingCasterStatContainer.addModifier(baseDamageModifier, attributeId);

        float totalDamage = 0;
        ElementalsAPI eapi = new ElementalsAPI();
        for(var element : eapi.getAllElements()) {
            damageContext.setElement(element);

            totalDamage += DamagePipeline.dealDamage(livingCaster, livingDefender, damageContext);
        }
        livingCasterStatContainer.removeModifier(baseDamageModifier, attributeId);

        e.setAmount(totalDamage);
    }

    private static final List<String> minionSpellIds = new ArrayList<>(List.of("irons_spellbooks:summon_polar_bear", "irons_spellbooks:summon_vex", "irons_spellbooks:raise_dead", "irons_spellbooks:summon_swords"));

    @SubscribeEvent
    public static void onHit(LivingAttackEvent e) {
        var damageSource = e.getSource();
        if(!(damageSource.getEntity() instanceof LivingEntity livingAttackerOrCaster)) return;

        //stop damage immunity cheese of the player when player attacks
        if(livingAttackerOrCaster instanceof ServerPlayer serverPlayer) {
            serverPlayer.invulnerableTime = 0;
        }

        LivingEntity livingDefender = e.getEntity();

        DamageContext damageContext = livingAttackerOrCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingAttackerOrCasterStatContainer = livingAttackerOrCaster.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingAttackerOrCasterStatContainer == null || livingDefenderStatContainer == null) return;

        damageContext.clearTags();

        //assumes the attacker/caster is themself unless it's their minion/pet/totem/whatever, then we can set it again
        damageContext.setSource("self");

        if(damageSource instanceof SpellDamageSource spellDamageSource) {
            damageContext.addTag("spell");

            //adds minion tag if spell summons minions.
            var spellId = spellDamageSource.spell().getSpellId();
            info(spellId);
            if(minionSpellIds.contains(spellId)) {
                damageContext.setSource("minion");
            }

        }
        else {
            damageContext.addTag("attack"); //probably safe to assume it is an attack, because we made sure to check that the damageSource.getEntity() is a living entity,
            //and the damageSource isn't a spell, so it's probably an attack

            var hand = livingAttackerOrCaster.getUsedItemHand();
            var itemInHand = livingAttackerOrCaster.getItemInHand(hand).getItem();
            determineAndAddWeaponDamageTagToContext(itemInHand, damageContext);

            if(damageSource.getDirectEntity() == damageSource.getEntity()) {
                damageContext.addTag("melee");
            }
        }

        if(damageSource.getDirectEntity() instanceof AoeEntity) damageContext.addTag("aoe");
        else if(damageSource.getDirectEntity() instanceof Projectile) damageContext.addTag("projectile");

        if(livingAttackerOrCaster instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal("LivingAttackEvent (onHit): "));
            serverPlayer.sendSystemMessage(Component.literal(damageContext.getTags().toString()));
            if(damageSource.getDirectEntity() != null) serverPlayer.sendSystemMessage(Component.literal(damageSource.getDirectEntity().toString()));

        }

        boolean continuePipeline = DamagePipeline.didHitSucceed(livingAttackerOrCaster, livingDefender, damageContext);
        if(!continuePipeline) e.setCanceled(true);
    }


    @SubscribeEvent
    public static void onAttackDamage(LivingDamageEvent e) {
        var damageSource = e.getSource();
        var livingDefender = e.getEntity();

        if(!(damageSource.getEntity() instanceof LivingEntity livingAttacker)) return;
        if(damageSource instanceof SpellDamageSource) return;

        DamageContext damageContext = livingAttacker.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingAttackerStatContainer = livingAttacker.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingAttackerStatContainer == null || livingDefenderStatContainer == null) return;

        float totalDamage = 0;
        ElementalsAPI eapi = new ElementalsAPI();
        for(var element : eapi.getAllElements()) {
            damageContext.setElement(element);
            totalDamage += DamagePipeline.dealDamage(livingAttacker, livingDefender, damageContext);
        }

        e.setAmount(totalDamage);
    }

    public static void info(String log) {
        AnIonianOnionsDamageMegacompatMod.LOGGER.info(log);
    }
}
