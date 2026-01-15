package com.lugr4.commands.itemcommands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.lugr4.commands.itemcommands.sub.GetAllItemsCommand;

public class ItemCollection extends AbstractCommandCollection {
    public ItemCollection() {
        // Nivel 2: /market item
        super("item", "Comandos de items");
        this.addSubCommand(new GetCollection());
    }


    // Nivel 3: /market item get
    // TAMBIÉN es un Collection, porque tiene hijos (all y key)
    private static class GetCollection extends AbstractCommandCollection {
        public GetCollection() {
            super("get", "Opciones de obtención");
            this.addSubCommand(new GetAllItemsCommand());
            // this.addSubCommand(new GetKeyCommand());
        }
    }


}
