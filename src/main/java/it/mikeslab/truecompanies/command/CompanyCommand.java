package it.mikeslab.truecompanies.command;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;

@CommandAlias("company")
public class CompanyCommand {

    @Default
    @HelpCommand
    public void onHelp(CommandHelp helpCommand) {
        helpCommand.showHelp();
    }


}
