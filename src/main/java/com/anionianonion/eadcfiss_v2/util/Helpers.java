package com.anionianonion.eadcfiss_v2.util;

import com.anionianonion.advanced_arpg_attributes_api.AdvancedARPGAttribute;
import com.anionianonion.advanced_arpg_attributes_api.StatContainer;
import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.anionianonion.damage_pipeline_api.DamageContext;
import com.anionianonion.eadcfiss_v2.AnIonianOnionsDamageMegacompatMod;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;

public class Helpers {

    public static String getSelfOrMinion(DamageContext damageContext) {
        if(damageContext.getSource().equals("self")) return "self";
        else return "minion";
    }

    /**
     Takes two StatContainers, and two strings for find and replace, and returns a new StatContainer
     Use cases: use summoner's attributes to be included in minion's attributes as well
     */
    public static StatContainer getNewStatContainerByRemappingBtoA(StatContainer a, StatContainer b, HashMap<String, String> tagToReplaceToNewReplacementTagMap) {

        AdvancedARPGAttributesAPI aaaAPI = new AdvancedARPGAttributesAPI();
        StatContainer resultStatContainer = new StatContainer();

        AnIonianOnionsDamageMegacompatMod.LOGGER.info("logging statContainer a");
        logDataFromStatContainer(a);

        AnIonianOnionsDamageMegacompatMod.LOGGER.info("logging statContainer b");
        logDataFromStatContainer(b);

        //adds everything in StatContainer a to new stat container
        for(var addedEntry : a.getAddedModifiers().entries()) {
            var attributeId = addedEntry.getKey().toString();
            var modifier = addedEntry.getValue();
            resultStatContainer.addModifier(modifier, attributeId);
        }

        for(var increasedEntry : a.getIncreaseModifiers().entries()) {
            var attributeId = increasedEntry.getKey().toString();
            var modifier = increasedEntry.getValue();
            resultStatContainer.addModifier(modifier, attributeId);
        }

        for(var moreEntry : a.getMoreModifiers().entries()) {
            var attributeId = moreEntry.getKey().toString();
            var modifier = moreEntry.getValue();
            resultStatContainer.addModifier(modifier, attributeId);
        }

        //moving on to StatContainer b
        for(var entry : tagToReplaceToNewReplacementTagMap.entrySet()) {
            var attributeTagToReplace = entry.getKey();
            var replacement = entry.getValue();

            for(var addedEntry : b.getAddedModifiers().entries()) {
                var attributeRL = addedEntry.getKey();
                var modifier = addedEntry.getValue();

                var advancedAPGAttribute = AdvancedARPGAttribute.get(attributeRL);
                if(advancedAPGAttribute == null) continue;

                if(!advancedAPGAttribute.getTags().contains(attributeTagToReplace)) continue;

                var newTags = new HashSet<>(advancedAPGAttribute.getTags());
                newTags.remove(attributeTagToReplace);
                newTags.add(replacement);

                var replacementAttributeRL = (ResourceLocation) aaaAPI.getFilteredAttributes(newTags).toArray()[0];
                var replacementAttributeId = replacementAttributeRL.toString();

                resultStatContainer.addModifier(modifier, replacementAttributeId);

            }

            for(var increasedEntry : b.getIncreaseModifiers().entries()) {
                var attributeRL = increasedEntry.getKey();
                var modifier = increasedEntry.getValue();

                var advancedAPGAttribute = AdvancedARPGAttribute.get(attributeRL);
                if(advancedAPGAttribute == null) continue;

                if(!advancedAPGAttribute.getTags().contains(attributeTagToReplace)) continue;

                var newTags = new HashSet<>(advancedAPGAttribute.getTags());
                newTags.remove(attributeTagToReplace);
                newTags.add(replacement);

                var replacementAttributeRL = (ResourceLocation) aaaAPI.getFilteredAttributes(newTags).toArray()[0];
                var replacementAttributeId = replacementAttributeRL.toString();

                resultStatContainer.addModifier(modifier, replacementAttributeId);

            }

            for(var moreEntry : b.getMoreModifiers().entries()) {
                var attributeRL = moreEntry.getKey();
                var modifier = moreEntry.getValue();

                var advancedAPGAttribute = AdvancedARPGAttribute.get(attributeRL);
                if(advancedAPGAttribute == null) continue;

                if(!advancedAPGAttribute.getTags().contains(attributeTagToReplace)) continue;

                var newTags = new HashSet<>(advancedAPGAttribute.getTags());
                newTags.remove(attributeTagToReplace);
                newTags.add(replacement);

                var replacementAttributeRL = (ResourceLocation) aaaAPI.getFilteredAttributes(newTags).toArray()[0];
                var replacementAttributeId = replacementAttributeRL.toString();

                resultStatContainer.addModifier(modifier, replacementAttributeId);

            }

        }


        AnIonianOnionsDamageMegacompatMod.LOGGER.info("logging result statContainer");
        logDataFromStatContainer(resultStatContainer);

        return resultStatContainer;
    }

    public static void logDataFromStatContainer(StatContainer statContainer) {
        for(var key : statContainer.getAddedModifiers().keySet()) {
            AnIonianOnionsDamageMegacompatMod.LOGGER.info(key + " "  + statContainer.getAddedModifiers().get(key).toString());
        }

        for(var key : statContainer.getIncreaseModifiers().keySet()) {
            AnIonianOnionsDamageMegacompatMod.LOGGER.info(key + " " + statContainer.getIncreaseModifiers().get(key).toString());
        }

        for(var key : statContainer.getMoreModifiers().keySet()) {
            AnIonianOnionsDamageMegacompatMod.LOGGER.info(key + " " + statContainer.getMoreModifiers().get(key).toString());
        }
    }
}
