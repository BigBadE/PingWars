package bigbade.pingwars.api;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.Map;

public class Boss {
    //Current hp
    private long hp;
    //Initial hp
    private long initialHp;
    //The boss message
    private Message message;
    //Everyone who pinged the boss and how much pings they did
    private Map<Member, Long> attackers = new HashMap<>();

    /**
     * Boss
     * @param hp the initial hp
     * @param message the message
     */
    public Boss(long hp, Message message) {
        this.hp = hp;
        this.initialHp = hp;
        this.message = message;
    }

    /**
     * Damage the boss
     * @param damager who attacked
     * @param lost pings done
     */
    public void loseHP(Member damager, long lost) {
        if(Long.compareUnsigned(lost, hp) == 1) {
            hp = 0;
            attackers.put(damager, hp);
        } else {
            hp -= lost;
            attackers.put(damager, lost);
        }
    }

    /**
     * Get the message
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Set the message
     * @param message message
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Get HP
     * @return hp
     */
    public long getHp() {
        return hp;
    }

    /**
     * Initial HP
     * @return initial hp
     */
    public long getInitialHp() {
        return initialHp;
    }

    /**
     * Get attackers
     * @return attackers
     */
    public Map<Member, Long> getAttackers() {
        return attackers;
    }
}
