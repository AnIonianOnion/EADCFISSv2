package com.anionianonion.eadcfiss_v2.util;

import com.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.eadcfiss_v2.AnIonianOnionsDamageMegacompatMod;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.HashSet;

public class Helpers {

    public static String getSelfOrMinion(DamageContext damageContext) {
        if(damageContext.getSource().equals("self")) return "self";
        else return "minion";
    }

    /**
    @return true if entity is a minion
     */
    public static boolean isMinion(Entity entity) {
        return entity instanceof LivingEntity && SummonManager.getOwner(entity) != null;
    }
}
