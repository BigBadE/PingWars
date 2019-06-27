package bigbade.pingwars.api;

import net.dv8tion.jda.core.entities.Message;

public class Boss {
    private long hp;
    private long initialHp;
    private Message message;

    public Boss(long hp, Message message) {
        this.hp = hp;
        this.initialHp = hp;
        this.message = message;
    }

    public void loseHP(long lost) {
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
}
