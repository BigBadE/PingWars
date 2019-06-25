package bigbade.pingwars.api;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;

public enum PermissionLevel {
    ADMINISTRATOR(Permission.ADMINISTRATOR),
    STAFF(Permission.BAN_MEMBERS),
    CHATMOD(Permission.MANAGE_CHANNEL),
    MEMBER(Permission.MESSAGE_WRITE),
    USER(null);

    private Permission perm;

    PermissionLevel(Permission perm) {
        this.perm = perm;
    }

    public Permission getPerm() {
        return perm;
    }

    public static PermissionLevel getLevel(Member member, MessageChannel channel) {
        for(PermissionLevel level : PermissionLevel.values()) {
            for(Permission permission : member.getPermissions((Channel) channel))
                if(permission == level.perm)
                    return level;
        }
        return USER;
    }
}
