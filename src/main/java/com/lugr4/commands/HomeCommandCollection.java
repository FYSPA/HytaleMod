package com.lugr4.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

// Importamos tus subcomandos directamente (no importa que estén en otra carpeta)
import com.lugr4.commands.home.sub.SetHomeSubCommand;
import com.lugr4.commands.home.sub.GoHomeSubCommand;
import com.lugr4.commands.home.sub.ListHomeSubCommand;
import com.lugr4.commands.home.sub.DelHomeSubCommand; // Si creaste el de borrar

public class HomeCommandCollection extends AbstractCommandCollection {

    public HomeCommandCollection() {
        // Nivel 1: /home
        super("home", "Sistema de casas");

        // ANTES: Llamábamos al intermediario "manage"
        // this.addSubCommand(new HomeCollection()); <--- BORRA O COMENTA ESTO

        // AHORA: Conectamos los comandos directamente aquí
        this.addSubCommand(new SetHomeSubCommand());  // Resultado: /home set
        this.addSubCommand(new GoHomeSubCommand());   // Resultado: /home tp
        this.addSubCommand(new ListHomeSubCommand()); // Resultado: /home list
        this.addSubCommand(new DelHomeSubCommand()); // Resultado: /home list

        // Si tienes el de borrar:
        // this.addSubCommand(new DelHomeSubCommand()); // Resultado: /home del
    }
}