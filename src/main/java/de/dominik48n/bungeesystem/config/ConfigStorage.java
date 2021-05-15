package de.dominik48n.bungeesystem.config;

import de.dominik48n.bungeesystem.BungeeSystemPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Created by Dominik48N on 03.02.2021
 */
public class ConfigStorage {

    private final Configuration configuration;

    @Getter private Configuration databaseConfiguration;

    public ConfigStorage( final BungeeSystemPlugin plugin ) {
        this.configuration = this.mkdirs( plugin );
    }

    /**
     * Load the config stuff
     */
    public void load() {
        this.databaseConfiguration = this.configuration.getSection( "mongodb" );
    }

    private Configuration mkdirs( final BungeeSystemPlugin plugin ) {
        final File file = new File( plugin.getDataFolder(), "config.yml" );

        if ( !file.exists() ) {
            try ( final InputStream inputStream = plugin.getResourceAsStream( "config.yml" ) ) {
                Files.copy( inputStream, file.toPath() );
            } catch ( final IOException e ) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider( YamlConfiguration.class ).load( file );
        } catch ( final IOException e ) {
            e.printStackTrace();
        }
        return null;
    }

}
