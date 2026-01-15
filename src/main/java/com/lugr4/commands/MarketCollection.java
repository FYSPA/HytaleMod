package com.lugr4.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.lugr4.commands.itemcommands.ItemCollection;

/**
 * Clase principal que gestiona el comando raíz <code>/market</code>.
 * <p>
 * Esta clase no ejecuta lógica por sí misma, sino que actúa como un contenedor (Collection)
 * para organizar todos los subcomandos relacionados con el sistema de mercado.
 * </p>
 * 
 * Jerarquía de comandos:
 * <ul>
 *   <li>{@code /market} (Esta clase)
 *      <ul>
 *          <li>{@code /market item} (Manejado por {@link ItemCollection})</li>
 *      </ul>
 *   </li>
 * </ul>
 * 
 * @author Lugr4
 * @version 1.0
 */
public class MarketCollection extends AbstractCommandCollection {

    public MarketCollection() {
        // Primer nivel
        // Inicializa el comando con el nombre "market" y su descripción
        super("market", "Market commands");

        // Registrar subcomandos
        // Aquí conectamos la rama de "/market item ..."
        this.addSubCommand(new ItemCollection());
        // this.addSubCommand(new SellCollection());.....
        // this.addSubCommand(new BuyCollection());....etc
    }
}
