package com.anionianonion.eadcfiss_v2;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.DamagePipeline;
import com.anionianonion.damage_pipeline_api.capability.DamageContextCapability;
import com.anionianonion.eadcfiss_v2.util.Helpers;
import com.anionianonion.elementals_api.api.ElementalsAPI;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static com.anionianonion.damage_pipeline_api.api.DamagePipelineAPI.determineAndAddWeaponDamageTagToContext;

@Mod.EventBusSubscriber(modid = AnIonianOnionsDamageMegacompatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {


    //decided the best way (I know of) to make our DamageContext's instance reusable almost anywhere, is to store it as a capability onto the player, rather than have it live only inside events. Because
    //Forge or Minecraft's damage pipeline is split into three events: LivingAttackEvent, LivingHurtEvent and LivingDamageEvent, and we need some way to access a global pipeline,
    // because we can't go through an entire pipeline for each split Minecraft/Forge's own pipeline / different damageContext each time for each event.


    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent e) {
        //This event triggers before all the other damage events, and in this event, we know that damage will for sure be calculated. So we must not let LivingDamageEvent recalculate the damage amount again.
        var damageSource = e.getSpellDamageSource().get();

        //only handles damage from living entities (entities with a health bar and StatContainer), and not entities like arrows.
        if(!(damageSource.getEntity() instanceof LivingEntity livingCaster)) return;

        var livingDefender = e.getEntity();
        var directEntity = damageSource.getDirectEntity();

        DamageContext damageContext = livingCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingCasterStatContainer = livingCaster.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingCasterStatContainer == null || livingDefenderStatContainer == null) return;

        //default settings, because we know it's a spell, and assuming the damage is dealt by the player themself, unless it's stated otherwise.
        damageContext.clearTags();
        damageContext.setSource("self");

        var spell = e.getSpellDamageSource().spell();

        //technically the spell school, but it's alr.
        var spellElement = spell.getSchoolType().getId().getPath();

        float totalDamage = 0;

        //totalDamage calculation branches for minions and non-minions
        //if the LivingEntity directEntity is what triggered a hit, then we know that a spell isn't what triggered that hit. therefore it's an attack, and also melee.
        if(Helpers.isMinion(directEntity)) {
            damageContext.setSource("minion");

            damageContext.addTag("attack");
            damageContext.addTag("melee");

            var minionStatContainer = directEntity.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            if(minionStatContainer == null) return;

            for(var element : ElementalsAPI.getAllElementNames()) {
                damageContext.setElement(element);

                totalDamage += DamagePipeline.dealDamage(livingCasterStatContainer, minionStatContainer, livingDefenderStatContainer, damageContext);
            }
        }
        else if (Helpers.isMinion(livingCaster)) {

            var owner = SummonManager.getOwner(livingCaster);
            if(!(owner instanceof LivingEntity summoner)) return;

            var summonerDamageContext = summoner.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
            var summonerStatContainer = summoner.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

            if(summonerDamageContext == null || summonerStatContainer == null) return;

            //it's probably safe to assume that IMagicEntities can only use spells as projectiles/direct entities
            if(livingCaster instanceof IMagicEntity) summonerDamageContext.addTag("spell");
            else summonerDamageContext.addTag("attack");

            summonerDamageContext.setSource("minion");

            for(var element : ElementalsAPI.getAllElementNames()) {
                damageContext.setElement(element);

                totalDamage += DamagePipeline.dealDamage(summonerStatContainer, livingCasterStatContainer, livingDefenderStatContainer, summonerDamageContext);
            }

        }
        else {
            damageContext.addTag("spell");

            var baseDamageModifier = new AttributeModifier(UUID.randomUUID(), "base damage of spell", e.getOriginalAmount(), AttributeModifier.Operation.ADDITION);
            String attributeId = String.format("%s:%s_spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, spellElement);
            livingCasterStatContainer.addModifier(baseDamageModifier, attributeId);

            for(var element : ElementalsAPI.getAllElementNames()) {
                damageContext.setElement(element);

                totalDamage += DamagePipeline.dealDamage(null, livingCasterStatContainer, livingDefenderStatContainer, damageContext);
            }
            livingCasterStatContainer.removeModifier(baseDamageModifier, attributeId);
        }

        e.setAmount(totalDamage);
    }

    //private static final List<String> minionSpellIds = new ArrayList<>(List.of("irons_spellbooks:summon_polar_bear", "irons_spellbooks:summon_vex", "irons_spellbooks:raise_dead", "irons_spellbooks:summon_swords"));

    //having multiple damage contexts in different events like onHit, and onSpellDamage made it confusing, so I decided to move it to only onHit.
    //However, SpellDamageEvent is separate and fires before everything else, so we must also declare tags in that event as well.
    @SubscribeEvent
    public static void onHit(LivingAttackEvent e) {
        var damageSource = e.getSource();
        var directEntity = damageSource.getDirectEntity();
        var entity = damageSource.getEntity();

        //makes sure the thing that triggered the hit is a LivingEntity
        if(!(entity instanceof LivingEntity livingAttackerOrCaster)) return;

        //stop damage immunity cheese of the player when player attacks
        if(livingAttackerOrCaster instanceof ServerPlayer serverPlayer) {
            serverPlayer.invulnerableTime = 0;
        }

        LivingEntity livingDefender = e.getEntity();

        DamageContext damageContext = livingAttackerOrCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingAttackerOrCasterStatContainer = livingAttackerOrCaster.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingAttackerOrCasterStatContainer == null || livingDefenderStatContainer == null) return;

        //reset tags to recalculate from a blank slate.
        damageContext.clearTags();
        //assumes hit comes from self, unless specified otherwise
        damageContext.setSource("self");

        if(Helpers.isMinion(directEntity) || Helpers.isMinion(livingAttackerOrCaster)) {
            damageContext.setSource("minion");
        }

        if(damageSource instanceof SpellDamageSource) damageContext.addTag("spell");
        //
        else {
            damageContext.addTag("attack"); //probably safe to assume it is an attack, because we made sure to check that the damageSource.getEntity() is a living entity,
            //and the damageSource isn't a spell, so it's probably an attack

            var hand = livingAttackerOrCaster.getUsedItemHand();
            var itemInHand = livingAttackerOrCaster.getItemInHand(hand).getItem();
            determineAndAddWeaponDamageTagToContext(itemInHand, damageContext);

            if(damageSource.getDirectEntity() == damageSource.getEntity()) {
                damageContext.addTag("melee");
            }
            //*P - projectiles can be both attack and spell, so we can escalate its scope one level.
        }

        if(damageSource.getDirectEntity() instanceof AoeEntity) damageContext.addTag("aoe");
        //*P -
        else if(damageSource.getDirectEntity() instanceof Projectile) damageContext.addTag("projectile");

        boolean continuePipeline;

        if(Helpers.isMinion(directEntity)) {

            var minionStatContainer = directEntity.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            if(minionStatContainer == null) return;

            continuePipeline = DamagePipeline.didHitSucceed(livingAttackerOrCasterStatContainer, minionStatContainer, livingDefenderStatContainer, damageContext);
        }
        else if(Helpers.isMinion(livingAttackerOrCaster)) {

            var summoner = SummonManager.getOwner(livingAttackerOrCaster);
            if(!(summoner instanceof LivingEntity livingSummoner)) return;

            var summonerStatContainer = livingSummoner.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            continuePipeline = DamagePipeline.didHitSucceed(summonerStatContainer, livingAttackerOrCasterStatContainer, livingDefenderStatContainer, damageContext);
        }
        else {
            continuePipeline = DamagePipeline.didHitSucceed(null, livingAttackerOrCasterStatContainer, livingDefenderStatContainer, damageContext);
        }

        if(!continuePipeline) e.setCanceled(true);
    }


    @SubscribeEvent
    public static void onAttackDamage(LivingDamageEvent e) {
        var damageSource = e.getSource();
        var livingDefender = e.getEntity();
        var directEntity = damageSource.getDirectEntity();

        if(!(damageSource.getEntity() instanceof LivingEntity livingAttacker)) return;
        if(damageSource instanceof SpellDamageSource) return; //Spell damage is and can only be handled by our onSpellDamageEvent.

        DamageContext damageContext = livingAttacker.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingAttackerStatContainer = livingAttacker.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingAttackerStatContainer == null || livingDefenderStatContainer == null) return;

        StatContainer finalOriginStatContainer = null;
        StatContainer finalAttackerStatContainer;

        if(Helpers.isMinion(directEntity)) {
            finalAttackerStatContainer = directEntity.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            if(finalAttackerStatContainer == null) return;

            finalOriginStatContainer = livingAttackerStatContainer;
        }
        else {
            if(Helpers.isMinion(livingAttacker) && SummonManager.getOwner(livingAttacker) instanceof LivingEntity livingSummoner) {
                finalOriginStatContainer = livingSummoner.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
            }
            finalAttackerStatContainer = livingAttackerStatContainer;
        }

        float totalDamage = 0;

        for(var element : ElementalsAPI.getAllElementNames()) {
            damageContext.setElement(element);
            totalDamage += DamagePipeline.dealDamage(finalOriginStatContainer,
                    finalAttackerStatContainer,
                    livingDefenderStatContainer,
                    damageContext);
        }

        e.setAmount(totalDamage);
    }

    public static void info(String log) {
        AnIonianOnionsDamageMegacompatMod.LOGGER.info(log);
    }
}
