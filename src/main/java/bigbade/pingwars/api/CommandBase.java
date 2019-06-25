package bigbade.pingwars.api;

import bigbade.pingwars.PingWars;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class CommandBase {
    private String id;
    private String[] aliases;
    private String description;
    private PermissionLevel perm;
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
