package com.anionianonion.eadcfiss_v2.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import javax.management.ReflectionException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetAllSpellsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("get_all_iss_spells").executes(ctx -> {

            List<Class<? extends AbstractSpell>> allSpells = new ArrayList<>();
            AbstractSpell.class.
            return 0;
        }));
    }
}
