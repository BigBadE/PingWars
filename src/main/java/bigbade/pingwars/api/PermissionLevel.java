package bigbade.pingwars.api;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

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

    public static PermissionLevel getLevel(Member member) {
        for(PermissionLevel level : PermissionLevel.values()) {
            if(level.perm != null && member.getPermissions().contains(level.perm)) return level;
        }
        return USER;
    }
}
