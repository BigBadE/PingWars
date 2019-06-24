package bigbade.pingwars.api;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class CommandBase {
    String id;
    String[] aliases;
    String description;
    PermissionLevel perm;

    public CommandBase(String id, String[] aliases, String description, PermissionLevel perm) {
        this.id = id;
        this.aliases = aliases;
        this.description = description;
        this.perm = perm;
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
