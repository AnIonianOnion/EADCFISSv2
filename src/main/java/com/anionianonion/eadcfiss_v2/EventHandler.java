package com.anionianonion.eadcfiss_v2;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.DamagePipeline;
import com.anionianonion.damage_pipeline_api.capability.DamageContextCapability;
import com.anionianonion.elementals_api.api.ElementalsAPI;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
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
        //This event triggers before all the other damage events, and in this event, we know that damage will for sure be calculated. So we must not let LivingDamageEvent recalculate the damage amount again.
        var damageSource = e.getSpellDamageSource().get();

        //only handles damage from living entities (entities with a health bar and StatContainer), and not entities like arrows.
        if(!(damageSource.getEntity() instanceof LivingEntity livingCaster)) return;

        var livingDefender = e.getEntity();

        DamageContext damageContext = livingCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingCasterStatContainer = livingCaster.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingCasterStatContainer == null || livingDefenderStatContainer == null) return;

        //default settings, because we know it's a spell, and assuming the damage is dealt by the player themself, unless it's stated otherwise.
        damageContext.clearTags();
        damageContext.addTag("spell");
        damageContext.setSource("self");

        var spell = e.getSpellDamageSource().spell();

        //technically the spell school, but it's alr.
        var spellElement = spell.getSchoolType().getId().getPath();

        //ResourceLocation spellResourceLocation = spell.getSpellResource();
        //var spellId = spellResourceLocation.getNamespace() + ":" + spellResourceLocation.getPath();
        var spellId = spell.getSpellId();

        if(minionSpellIds.contains(spellId)) {
            damageContext.setSource("minion");
        }

        var baseDamageModifier = new AttributeModifier(UUID.randomUUID(), "base damage of spell", e.getOriginalAmount(), AttributeModifier.Operation.ADDITION);
        String attributeId = String.format("%s:%s_spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, spellElement);
        livingCasterStatContainer.addModifier(baseDamageModifier, attributeId);

        float totalDamage = 0;
        ElementalsAPI eapi = new ElementalsAPI();
        for(var element : eapi.getAllElements()) {
            damageContext.setElement(element);

            totalDamage += DamagePipeline.dealDamage(livingCaster, livingDefender, livingCasterStatContainer, livingDefenderStatContainer, damageSource.getDirectEntity(), damageContext);
        }
        livingCasterStatContainer.removeModifier(baseDamageModifier, attributeId);

        e.setAmount(totalDamage);
    }

    private static final List<String> minionSpellIds = new ArrayList<>(List.of("irons_spellbooks:summon_polar_bear", "irons_spellbooks:summon_vex", "irons_spellbooks:raise_dead", "irons_spellbooks:summon_swords"));

    //having multiple damage contexts in different events like onHit, and onSpellDamage made it confusing, so I decided to move it to only onHit.
    //However, SpellDamageEvent is separate and fires before everything else, so we must also declare tags in that event as well.
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

        //we want to clear our tags at the end of the cycle instead of lazily handling at the beginning of the new cycle, due to us adding tags in the SpellDamageEvent.
        //and we don't want that to be deleted. But I think it will also work if we clear it conditionally if it doesn't have the spell tag.
        //if(!damageContext.getTags().contains("spell")) damageContext.clearTags();

        //assumes the attacker/caster is themself unless it's their minion/pet/totem/whatever, then we can set it again
        //but we must also not reset it from the SpellDamageEvent, so I think a good condition is to only set it if it's null.
        //This causes a problem to arise, where we must also find a good place to reset it to null.
            //If it's an attack, set it to null at the end of LivingDamageEvent, after we've dealt the damage.
            //If it's a spell, set it to null when damage is dealt, so probably at the end of the SpellDamageEvent?

        //actually might be more convenient if we do clear all tags again
        damageContext.clearTags();
        //if(damageContext.getSource() == null)
        damageContext.setSource("self");

        if(damageSource instanceof SpellDamageSource spellDamageSource) {
            damageContext.addTag("spell");

            //current implementation
            var spellId = spellDamageSource.spell().getSpellId();
            if(minionSpellIds.contains(spellId)) {
                damageContext.setSource("minion");
            }

            //current implementation works, but there's a few problems with it
            //1. minions will always be considered spell damage. When minions like zombies perform an attack, those attacks will only benefit from spell damage.
            //2. minions like skeletons shoot arrows, which means that the arrows will count as the direct entity instead of the minion.
                //it seems that SummonManager.getOwner() will maybe let us solve this problem?

            info("spellDamageSource directEntity: " + spellDamageSource.get().getDirectEntity().toString());
            info("spellDamageSource entity: " + spellDamageSource.get().getEntity().toString());

            //new implementation
        }
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

        if(damageSource instanceof SpellDamageSource) return; //Spell damage is and can only be handled by our onSpellDamageEvent.

        DamageContext damageContext = livingAttacker.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingAttackerStatContainer = livingAttacker.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingAttackerStatContainer == null || livingDefenderStatContainer == null) return;

        float totalDamage = 0;
        ElementalsAPI eapi = new ElementalsAPI();
        for(var element : eapi.getAllElements()) {
            damageContext.setElement(element);
            totalDamage += DamagePipeline.dealDamage(livingAttacker, livingDefender, livingAttackerStatContainer, livingDefenderStatContainer, damageSource.getDirectEntity(), damageContext);
        }

        e.setAmount(totalDamage);
    }

    public static void info(String log) {
        AnIonianOnionsDamageMegacompatMod.LOGGER.info(log);
    }
}
