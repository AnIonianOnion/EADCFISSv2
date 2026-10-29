package io.anionianonion.megacompat;

import io.anionianonion.advanced_arpg_attributes_api.StatContainer;
import io.anionianonion.damage_pipeline_api.DamageContext;
import io.anionianonion.damage_pipeline_api.DamagePipeline;
import io.anionianonion.damage_pipeline_api.attachments.ModAttachments;
import io.anionianonion.damage_pipeline_api.util.RandomHelpers;
import io.anionianonion.elementals_api.AilmentDamageSource;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.UUID;

import static io.anionianonion.damage_pipeline_api.api.DamagePipelineAPI.*;
import static io.anionianonion.megacompat.util.Helper.info;

@EventBusSubscriber(modid = AnIonianOnionsDamageMegacompatMod.MOD_ID)
public class EventHandler {


    //decided the best way (I know of) to make our DamageContext's instance reusable almost anywhere, is to store it as a capability onto the player, rather than have it live only inside events. Because
    //Forge or Minecraft's damage pipeline is split into three events: LivingAttackEvent, LivingHurtEvent and LivingDamageEvent, and we need some way to access a global pipeline,
    // because we can't go through an entire pipeline for each split Minecraft/Forge's own pipeline / different damageContext each time for each event.

    //having multiple damage contexts in different events like onHit, and onSpellDamage made it confusing, so I decided to move it to only onHit.
    //However, SpellDamageEvent is separate and fires before everything else, so we must also declare tags in that event as well.
    //nevermind that last part. it's all handled here now.

    @SubscribeEvent
    public static void onHit(LivingIncomingDamageEvent e) {

        //new problems of DoTs
            //can't clear damage context, because we are in the middle of the pipeline, as we are using base damage
                //maybe it's better if damageContext isn't stored on the living entity?
            //need to get the element of the DoT somehow
            //and a way to know that it is a DoT.

        var damageSource = e.getSource();
        var directEntity = damageSource.getDirectEntity();
        var entity = damageSource.getEntity();

        if (damageSource instanceof AilmentDamageSource) return;
        //makes sure the thing that triggered the hit is a LivingEntity
        if(!(entity instanceof LivingEntity livingAttackerOrCaster)) return;

        var hand = livingAttackerOrCaster.getUsedItemHand();
        var itemInHand = livingAttackerOrCaster.getItemInHand(hand).getItem();

        DamageContext damageContext = livingAttackerOrCaster.getData(ModAttachments.DAMAGE_CONTEXT);
        StatContainer livingAttackerOrCasterStatContainer = livingAttackerOrCaster.getData(io.anionianonion.advanced_arpg_attributes_api.attachments.ModAttachments.STAT_CONTAINER);

        //reset tags to recalculate from a blank slate.
        damageContext.reset();
        //there's no more need to setSource to minion upon minion damage

        //self-cast spell-type damage
        if(damageSource instanceof SpellDamageSource spellDamageSource &&
                !(RandomHelpers.isMinion(directEntity) || RandomHelpers.isMinion(livingAttackerOrCaster))
        ) {

            damageContext.addTag("spell");
            var baseDamageModifier = new AttributeModifier(spellDamageSource.spell().getSpellResource(), e.getAmount(), AttributeModifier.Operation.ADD_VALUE);
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

        livingAttackerOrCaster.setData(ModAttachments.DAMAGE_CONTEXT, damageContext);

        boolean continuePipeline = DamagePipeline.didHitSucceed(e);
        e.setCanceled(!continuePipeline);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Pre e) {

        if(e.getSource() instanceof AilmentDamageSource) return;

        float totalDamage = DamagePipeline.dealDamage(e);
        e.setNewDamage(totalDamage);

        //remove potentially added spell base damage modifier from spells
        var attacker = e.getSource().getEntity();
        if(!(attacker instanceof LivingEntity livingAttacker)) return;

        var livingAttackerStatContainer = livingAttacker.getData(io.anionianonion.advanced_arpg_attributes_api.attachments.ModAttachments.STAT_CONTAINER);

        var uuid = livingAttacker.getUUID();

        livingAttackerStatContainer.getAddedModifiers().values().remove(bonusSpelLDamageModifiers.get(uuid));
        livingAttackerStatContainer.getIncreaseModifiers().values().remove(bonusSpelLDamageModifiers.get(uuid));
        livingAttackerStatContainer.getMoreModifiers().values().remove(bonusSpelLDamageModifiers.get(uuid));

        bonusSpelLDamageModifiers.remove(e.getSource().getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Pre e) {
        var entity = e.getEntity();
        if(!(entity instanceof LivingEntity livingEntity)) return;
        var ailmentDataContainer = livingEntity.getData(io.anionianonion.elementals_api.attachments.ModAttachments.AILMENT_DATA);

        ailmentDataContainer.tick();

        var ailmentModContainer = livingEntity.getData(io.anionianonion.elementals_api.attachments.ModAttachments.AILMENT_MODIFIERS);
        ailmentModContainer.getAilmentReplacements().put("ignite", "scorch");
    }

    //livingEntity uuid to attribute modifier mapper.
    private static final HashMap<UUID, AttributeModifier> bonusSpelLDamageModifiers = new HashMap<>();

    @SubscribeEvent
    public static void onLivingEntityDeath(LivingDeathEvent e) {
        bonusSpelLDamageModifiers.remove(e.getEntity().getUUID());
    }
}
