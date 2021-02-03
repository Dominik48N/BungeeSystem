package de.dominik48n.bungeesystem;

import de.dominik48n.bungeesystem.config.MessageHandler;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * Created by Dominik48N on 03.02.2021
 */
@Getter
public class BungeeSystemPlugin extends Plugin {

    @Getter private static BungeeSystemPlugin instance;

    private MessageHandler messageHandler;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        // Load all messages
        this.messageHandler = new MessageHandler( this );
        this.messageHandler.load();

        // Register all commands and listeners
        this.registerListeners();
        this.registerCommands();

    }

    @Override
    public void onDisable() {

    }

    /**
     * Register all commands
     */
    private void registerCommands() {
        final PluginManager pluginManager = this.getProxy().getPluginManager();

    }

    /**
     * Register all listeners
     */
    private void registerListeners() {
        final PluginManager pluginManager = this.getProxy().getPluginManager();

    }
}
