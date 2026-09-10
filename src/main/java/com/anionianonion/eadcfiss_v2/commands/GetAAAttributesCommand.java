package com.anionianonion.eadcfiss_v2.commands;

import com.anionianonion.advanced_arpg_attributes_api.api.AdvancedARPGAttributesAPI;
import com.mojang.brigadier.CommandDispatcher;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class GetAAAttributesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("get_all_aaattributes").executes(ctx -> {

            String str = "";
            for(var entry : AdvancedARPGAttributesAPI.getRegistry().entrySet()) {
                str += "[" + entry.getKey().toString() + "]: " + entry.getValue().getTags() + " | " + entry.getValue().getAllowedModifierTypes() + "\n";
            }

            String finalSpells = str;
            ctx.getSource().sendSuccess(() -> Component.literal(finalSpells), true);


            return 0;
        }));
    }
}
