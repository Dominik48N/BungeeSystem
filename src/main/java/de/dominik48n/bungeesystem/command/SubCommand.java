package de.dominik48n.bungeesystem.command;

import de.dominik48n.bungeesystem.config.MessageHandler;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by Dominik48N on 02.02.2021
 */
@AllArgsConstructor @Getter
public abstract class SubCommand {

    private final Command command;
    private final String label, description, permission, arguments;
    private final boolean blockConsole;

    protected final MessageHandler messageHandler;

    public SubCommand( final Command command,
                       final String label,
                       final String arguments,
                       final boolean blockConsole,
                       final MessageHandler messageHandler ) {
        this ( command,
                label,
                messageHandler.getMessage( "command." + command.getCommand() + '.' + label + ".description" ),
                command.getCommand() + '.' + label,
                arguments,
                blockConsole,
                messageHandler );
    }

    public SubCommand( final Command command,
                       final String label,
                       final String permission,
                       final String arguments,
                       final boolean blockConsole,
                       final MessageHandler messageHandler ) {
        this( command,
                label,
                messageHandler.getMessage( "command." + command.getCommand() + '.' + label + ".description" ),
                permission,
                arguments,
                blockConsole,
                messageHandler );
    }

    public abstract boolean onCommand( final CommandSender commandSender, final String[] arguments );

    public abstract List< String > onTab( final CommandSender commandSender, final String[] strings );

}
