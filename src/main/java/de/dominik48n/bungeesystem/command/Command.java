package de.dominik48n.bungeesystem.command;

import com.google.common.collect.Lists;
import de.dominik48n.bungeesystem.BungeeSystemPlugin;
import de.dominik48n.bungeesystem.config.MessageHandler;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * Created by Dominik48N on 02.02.2021
 */
@Getter
public abstract class Command extends net.md_5.bungee.api.plugin.Command implements TabExecutor {

    private final String command, description, permission;
    private final List< SubCommand > subCommands;
    private final boolean showCommands;

    protected final MessageHandler messageHandler;

    public Command( final String command, final String description, final String permission, final boolean showCommands ) {
        this( command, description, permission, showCommands, BungeeSystemPlugin.getInstance().getMessageHandler() );
    }

    public Command( final String command, final String permission, final boolean showCommands, final MessageHandler messageHandler ) {
        this( command, messageHandler.getMessage( "command." + command.toLowerCase() + ".description" ), permission, showCommands, messageHandler );
    }

    public Command( final String command, final String description, final String permission, final boolean showCommands, final MessageHandler messageHandler ) {
        super( command, null );
        this.command = command;
        this.description = description;
        this.permission = permission;
        this.showCommands = showCommands;

        this.messageHandler = messageHandler;
        this.subCommands = Lists.newArrayList();
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        final String argument = strings.length > 0 ? strings[ 0 ] : "access";

        if ( this.permission != null && !commandSender.hasPermission( this.permission + '.' + argument ) ) {

            commandSender.sendMessage( this.messageHandler.getMessage( "command." + this.command.toLowerCase() + ".no_permission" ) );
            return;
        }

        if ( strings.length > 0 ) {
            for ( final SubCommand subCommand : this.subCommands ) {
                if ( !subCommand.getLabel().equalsIgnoreCase( argument ) ) continue;

                if ( subCommand.isBlockConsole() && !( commandSender instanceof ProxiedPlayer ) ) {

                    commandSender.sendMessage( this.messageHandler.getMessage( "command." + this.command.toLowerCase() + ".console" ) );
                    break;
                }

                if ( subCommand.getPermission() != null && !commandSender.hasPermission( subCommand.getPermission() ) ) {

                    commandSender.sendMessage( this.messageHandler.getMessage( "command." +
                            this.command.toLowerCase() + '.' +
                            subCommand.getCommand() + ".no_permission" ) );
                    break;
                }

                if ( !subCommand.onCommand( commandSender, strings ) ) commandSender.sendMessage(
                        this.messageHandler.getMessage( "command." + this.command.toLowerCase() + ".usage", subCommand.getArguments() ) );
                break;
            }

            return;
        }

        commandSender.sendMessage( this.description );
        if ( !this.showCommands ) return;

        for ( final SubCommand subCommand : this.subCommands ) {
            if ( this.permission != null && !commandSender.hasPermission( this.permission + '.' + subCommand.getLabel().toLowerCase() ) )
                continue;
            if ( subCommand.isBlockConsole() && !( commandSender instanceof ProxiedPlayer ) ) continue;

            commandSender.sendMessage( this.messageHandler.getMessage( "command." + this.command.toLowerCase() + ".show_command",
                    subCommand.getArguments(),
                    subCommand.getDescription() ) );
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        final String argument = args.length > 0 ? args[ 0 ] : "access";

        if ( this.permission != null && !sender.hasPermission( this.permission + '.' + argument ) ) return Lists.newArrayList();
        if ( args.length == 1 ) return this.subCommands
                .stream()
                .map( SubCommand::getLabel )
                .filter(  name -> this.startsWithIgnoreCase( name, args[ 0 ] ) )
                .collect( Collectors.toList() );
        final SubCommand subCommand = this.subCommands
                .stream()
                .filter( subCommand1 -> subCommand1.getLabel().equalsIgnoreCase( args[ 0 ] ) )
                .findAny()
                .orElse( null );

        return subCommand != null ? subCommand.onTab( sender, args ) : Lists.newLinkedList();
    }

    private boolean startsWithIgnoreCase( final String string, final String prefix ) {
        return !( string.length() < prefix.length() ) && string.regionMatches( true, 0, prefix, 0, prefix.length() );
    }
}
