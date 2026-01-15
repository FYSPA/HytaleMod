package com.lugr4.commands.itemcommands.sub;

import java.util.Map;
// import java.util.logging.Level; .... 

import javax.annotation.Nonnull;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.CommandContext;

public class GetAllItemsCommand extends CommandBase {
    
    // private static final Argument 
    public GetAllItemsCommand(){
        super("get", "Lista todos los items");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("Te voa imprimir items!"));

        AssetStore<String, Item, ?> itemStore = AssetRegistry.getAssetStore(Item.class);

        if (itemStore != null) {
            // getLogger().at(Level.INFO).log("Explorando definiciones de ítems..."); .... 

            AssetMap<String, Item> assetMap = itemStore.getAssetMap();
            // assetMap.getAsset(getBasePermission())
            Map<String, Item> map = assetMap.getAssetMap();

            // 2. Obtenemos todos los ítems registrados en el juego
            map.forEach((id, item) -> {
                // id = "hytale:iron_sword"
                // item = El objeto con toda la configuración

                context.sendMessage(Message.raw(item.getTranslationKey()));
                // getLogger().at(Level.INFO).log("ID: %s | Nombre Interno: %s", id, item.getTranslationKey());.... 
            });
            
            // getLogger().at(Level.INFO).log("Total de ítems cargados: %d", itemStore.get); .... 
        } else {
            // getLogger().at(Level.SEVERE).log("Error: No se encontró el AssetStore para 'Item'."); .... 
        }

    }
}
