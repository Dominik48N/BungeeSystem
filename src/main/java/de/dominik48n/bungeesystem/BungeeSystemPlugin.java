package de.dominik48n.bungeesystem;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import de.dominik48n.bungeesystem.config.ConfigStorage;
import de.dominik48n.bungeesystem.config.MessageHandler;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by Dominik48N on 03.02.2021
 */
@Getter
public class BungeeSystemPlugin extends Plugin {

    @Getter private static BungeeSystemPlugin instance;

    private MongoDatabase database;
    private MongoClient client;

    private MessageHandler messageHandler;
    private ConfigStorage configStorage;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        // Load the config
        this.configStorage = new ConfigStorage( this );
        this.configStorage.load();

        // Establishes a connection to the database
        this.connectDatabase();

        // Load all messages
        this.messageHandler = new MessageHandler( this );
        this.messageHandler.load();

        // Register all commands and listeners
        this.registerListeners();
        this.registerCommands();

    }

    @Override
    public void onDisable() {
        // Closes the database connection
        this.disconnectDatabase();
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

    /**
     * Establishes a connection to the database
     */
    private void connectDatabase() {
        final Configuration data = this.configStorage.getDatabaseConfiguration();

        final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs( new UuidCodec( UuidRepresentation.STANDARD ) ),
                MongoClient.getDefaultCodecRegistry()
        );

        this.client = new MongoClient(
                new ServerAddress( data.getString( "hostname" ), data.getInt( "port" ) ),
                Collections.singletonList( MongoCredential.createCredential(
                        data.getString( "username" ),
                        data.getString( "database" ),
                        data.getString( "password" ).toCharArray() ) ),
                MongoClientOptions.builder().codecRegistry( codecRegistry ).build()
        );
        this.database = this.client.getDatabase( data.getString( "database" ) );

        this.getLogger().info( "The database is successfully connected!" );
    }

    /**
     * Closes the database connection
     */
    private void disconnectDatabase() {
        this.client.close();
        this.getLogger().info( "The database connection was closed!" );
    }
}
