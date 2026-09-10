package com.anionianonion.eadcfiss_v2.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import javax.management.ReflectionException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetAllSpellsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("get_all_iss_spells").executes(ctx -> {

            String spells = "";
            for(var spell : SpellRegistry.getEnabledSpells()) {
                spells += spell.getSpellId() + ", ";
            }

            String finalSpells = spells;
            ctx.getSource().sendSuccess(() -> Component.literal(finalSpells), true);


            return 0;
        }));
    }
}
