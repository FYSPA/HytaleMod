package com.lugr4.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.lugr4.commands.playertp.sub.TpAcceptSubCommand;
import com.lugr4.commands.playertp.sub.TpDenySubCommand;
import com.lugr4.commands.playertp.sub.TpaSubCommand;

public class TpaCommandCollection extends AbstractCommandCollection {
    public TpaCommandCollection() {
        super("request", "TPA System");

        this.addSubCommand(new TpAcceptSubCommand());
        this.addSubCommand(new TpaSubCommand());
        this.addSubCommand(new TpDenySubCommand());

    }
}
