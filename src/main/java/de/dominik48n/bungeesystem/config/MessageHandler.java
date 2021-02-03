package de.dominik48n.bungeesystem.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.dominik48n.bungeesystem.BungeeSystemPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Created by Dominik48N on 03.02.2021
 */
public class MessageHandler {

    private final Map< String, String > messageCache;

    private final Configuration configuration;

    public MessageHandler( final BungeeSystemPlugin plugin ) {
        this.configuration = this.mkdirs( plugin );
        this.messageCache = Maps.newConcurrentMap();
    }

    /**
     * Cache all messages
     */
    public void load() {
        final String prefix = ChatColor.translateAlternateColorCodes( '&', this.configuration.getString( "prefix" ) );

        for ( final String key : this.getKeys( true ) ) {
            this.messageCache.put( key, ChatColor.translateAlternateColorCodes( '&', this.configuration.getString( key )
                    .replace( "{0}", prefix ) ) );
        }
    }

    /**
     * Fetches a message with the specified key from the cache and replaces the specified objects.
     *
     * @param key          the message key
     * @param replacements the objects to be replaced
     *
     * @return the created message or, if the message is not found, a predefined message.
     */
    public String getMessage( final String key, final Object... replacements ) {
        synchronized ( this.messageCache ) {
            String message = this.messageCache.get( key );

            if ( message == null ) return "§4Not Found! " + key;
            if ( replacements.length == 0 ) return message;
            int i = 1;

            for ( final Object o : replacements ) {
                message = message.replace( "{" + i + "}", o.toString() );
                i++;
            }

            return message;
        }
    }

    private Collection< String > getKeys( final boolean deep ) {
        if ( deep ) return this.recursiveKeys( "" );

        return this.configuration.getKeys();
    }

    private List< String > recursiveKeys( final String path ) {
        final LinkedList< String > list = Lists.newLinkedList();
        final Collection< String > keys = path.equals( "" ) ? this.configuration.getKeys() : this.configuration.getSection( path ).getKeys();

        if ( keys.isEmpty() ) return list;

        for ( final String key : keys ) {
            list.add( key );
            list.addAll( this.recursiveKeys( ( path.equals( "" ) ? "" : path + "." ) + key ) );
        }

        return list;
    }

    private Configuration mkdirs( final BungeeSystemPlugin plugin ) {
        final File file = new File( plugin.getDataFolder(), "messages.yml" );

        if ( !file.exists() ) {
            try ( final InputStream inputStream = plugin.getResourceAsStream( "messages.yml" ) ) {
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
