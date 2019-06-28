package bigbade.pingwars.api;

import bigbade.pingwars.PingWars;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class CommandBase {
    //ID used in help
    private String id;
    //All aliases
    private String[] aliases;
    //Description
    private String description;
    //Permission required to execute the command
    private PermissionLevel perm;
    //Main command
    protected PingWars main;

    public CommandBase(String id, String[] aliases, String description, PermissionLevel perm, PingWars main) {
        this.id = id;
        this.aliases = aliases;
        this.description = description;
        this.perm = perm;
        this.main = main;
    }

    public String getId() {
        return id;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public PermissionLevel getPerm() {
        return perm;
    }

    public abstract void onCommand(MessageReceivedEvent event, String[] args);
}
