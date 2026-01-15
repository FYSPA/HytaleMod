package com.lugr4.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

// Importamos tus subcomandos directamente (no importa que estén en otra carpeta)
import com.lugr4.commands.home.sub.*;

public class HomeCommandCollection extends AbstractCommandCollection {

    public HomeCommandCollection() {

        super("home", "Sistema de casas");

        this.addSubCommand(new SetHomeSubCommand());
        this.addSubCommand(new GoHomeSubCommand());
        this.addSubCommand(new ListHomeSubCommand());
        this.addSubCommand(new DelHomeSubCommand());
        this.addSubCommand(new ConfigSubCommand());
    }
}