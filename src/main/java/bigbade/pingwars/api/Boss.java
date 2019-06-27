package bigbade.pingwars.api;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.Map;

public class Boss {
    private long hp;
    private long initialHp;
    private Message message;
    private Map<Member, Long> attackers = new HashMap<>();

    public Boss(long hp, Message message) {
        this.hp = hp;
        this.initialHp = hp;
        this.message = message;
    }

    public void loseHP(Member damager, long lost) {
        hp -= lost;
        hp = (Long.compareUnsigned(0, hp) == 1) ? hp : 0;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public long getHp() {
        return hp;
    }

    public long getInitialHp() {
        return initialHp;
    }

    public Map<Member, Long> getAttackers() {
        return attackers;
    }
}
