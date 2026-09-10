package com.anionianonion.eadcfiss_v2;

import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.capability.StatContainerCapability;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.damage_pipeline_api.DamagePipeline;
import com.anionianonion.damage_pipeline_api.capability.DamageContextCapability;
import com.anionianonion.damage_pipeline_api.util.RandomHelpers;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

import static com.anionianonion.damage_pipeline_api.api.DamagePipelineAPI.*;

@Mod.EventBusSubscriber(modid = AnIonianOnionsDamageMegacompatMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {


    //decided the best way (I know of) to make our DamageContext's instance reusable almost anywhere, is to store it as a capability onto the player, rather than have it live only inside events. Because
    //Forge or Minecraft's damage pipeline is split into three events: LivingAttackEvent, LivingHurtEvent and LivingDamageEvent, and we need some way to access a global pipeline,
    // because we can't go through an entire pipeline for each split Minecraft/Forge's own pipeline / different damageContext each time for each event.

    //having multiple damage contexts in different events like onHit, and onSpellDamage made it confusing, so I decided to move it to only onHit.
    //However, SpellDamageEvent is separate and fires before everything else, so we must also declare tags in that event as well.
    //nevermind that last part. it's all handled here now.

    @SubscribeEvent
    public static void onHit(LivingAttackEvent e) {
        var damageSource = e.getSource();
        var directEntity = damageSource.getDirectEntity();
        var entity = damageSource.getEntity();

        info("entity " + entity + " attacked");
        info("direct entity " + directEntity + " attacked");

        //makes sure the thing that triggered the hit is a LivingEntity
        if(!(entity instanceof LivingEntity livingAttackerOrCaster)) return;

        var hand = livingAttackerOrCaster.getUsedItemHand();
        var itemInHand = livingAttackerOrCaster.getItemInHand(hand).getItem();

        LivingEntity livingDefender = e.getEntity();

        DamageContext damageContext = livingAttackerOrCaster.getCapability(DamageContextCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingAttackerOrCasterStatContainer = livingAttackerOrCaster.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        StatContainer livingDefenderStatContainer = livingDefender.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);

        if(damageContext == null || livingAttackerOrCasterStatContainer == null || livingDefenderStatContainer == null) return;

        //reset tags to recalculate from a blank slate.
        damageContext.reset();
        //there's no more need to setSource to minion upon minion damage

        //self-cast spell-type damage
        if(damageSource instanceof SpellDamageSource spellDamageSource &&
                !(RandomHelpers.isMinion(directEntity) || RandomHelpers.isMinion(livingAttackerOrCaster))
        ) {

            damageContext.addTag("spell");
            var baseDamageModifier = new AttributeModifier(UUID.randomUUID(), "base damage of spell", e.getAmount(), AttributeModifier.Operation.ADDITION);
            var spellElement = spellDamageSource.spell().getSchoolType().getId().getPath();
            String attributeId = String.format("%s:%s_spell_damage", AnIonianOnionsDamageMegacompatMod.MOD_ID, spellElement);
            livingAttackerOrCasterStatContainer.addModifier(baseDamageModifier, attributeId);
            bonusSpelLDamageModifiers.put(livingAttackerOrCaster.getUUID(), baseDamageModifier);

        }

        else {
            damageContext.addTag("attack");

            //should never fire in the case of minions because
                //if minion melee attack: directEntity is minion and entity is the minion's summoner. both are different
                //and if melee ranged attack, directEntity is the projectile, and entity is the minion. both are different

            //self-melee
            if(directEntity == entity) {
                damageContext.addTag("melee");
                determineAndAddMeleeWeaponDamageTagToContext(itemInHand, damageContext);
            }
            //minion melee
            else if(RandomHelpers.isMinion(directEntity)) {
                damageContext.addTag("melee");
                var minion = (LivingEntity) directEntity;
                var minionHand = minion.getUsedItemHand();
                var minionHandItem = minion.getItemInHand(minionHand).getItem();

                info(minionHandItem.toString());

                determineAndAddMeleeWeaponDamageTagToContext(minionHandItem, damageContext);
            }
            //minion ranged
            else if(RandomHelpers.isMinion(livingAttackerOrCaster) && directEntity instanceof Projectile projectile) {

                var minionHand = livingAttackerOrCaster.getUsedItemHand();
                var minionHandItem = livingAttackerOrCaster.getItemInHand(minionHand).getItem();

                info(minionHandItem.toString());

                damageContext.addTag("projectile");
                determineAndAddRangedWeaponDamageTagToContext(minionHandItem, damageContext);

                if(projectile instanceof Arrow arrow) {
                    var arrowSpeed = arrow.getDeltaMovement().length();
                    damageContext.setProjectileSpeed((float) arrowSpeed);
                }
            }
            //self ranged
            else if (directEntity instanceof Projectile projectile) {

                determineAndAddRangedWeaponDamageTagToContext(itemInHand, damageContext);
                if(projectile instanceof Arrow arrow) {
                    var arrowSpeed = arrow.getDeltaMovement().length();
                    damageContext.setProjectileSpeed((float) arrowSpeed);
                }
            }
            //*P - projectiles can be both attack and spell, so we can escalate its scope one level.
        }

        if(directEntity instanceof AoeEntity) damageContext.addTag("aoe");
        //*P -
        else if(directEntity instanceof Projectile) {
            damageContext.addTag("projectile");
        }

        //stop damage immunity cheese of the player when player attacks
        if(livingAttackerOrCaster instanceof ServerPlayer serverPlayer) {
            serverPlayer.invulnerableTime = 0;
        }

        if(livingAttackerOrCaster instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal("damage Context tags: " + damageContext.getTags()));
        }
        else {
            info("damageContext tags: " + damageContext.getTags());
        }


        boolean continuePipeline = DamagePipeline.didHitSucceed(e);
        e.setCanceled(!continuePipeline);
    }


    @SubscribeEvent
    public static void onDamage(LivingDamageEvent e) {
        float totalDamage = DamagePipeline.dealDamage(e);
        e.setAmount(totalDamage);

        //remove potentially added spell base damage modifier from spells
        var attacker = e.getSource().getEntity();
        if(!(attacker instanceof LivingEntity livingAttacker)) return;

        var livingAttackerStatContainer = livingAttacker.getCapability(StatContainerCapability.INSTANCE).resolve().orElse(null);
        if(livingAttackerStatContainer == null) return;

        var uuid = livingAttacker.getUUID();

        livingAttackerStatContainer.getAddedModifiers().values().remove(bonusSpelLDamageModifiers.get(uuid));
        livingAttackerStatContainer.getIncreaseModifiers().values().remove(bonusSpelLDamageModifiers.get(uuid));
        livingAttackerStatContainer.getMoreModifiers().values().remove(bonusSpelLDamageModifiers.get(uuid));

        bonusSpelLDamageModifiers.remove(e.getSource().getEntity().getUUID());
    }

    public static void info(String log) {
        AnIonianOnionsDamageMegacompatMod.LOGGER.info(log);
    }

    //livingEntity uuid to attribute modifier mapper.
    private static final HashMap<UUID, AttributeModifier> bonusSpelLDamageModifiers = new HashMap<>();

    @SubscribeEvent public static void onLivingEntityDeath(LivingDeathEvent e) {
        bonusSpelLDamageModifiers.remove(e.getEntity().getUUID());
    }
}
