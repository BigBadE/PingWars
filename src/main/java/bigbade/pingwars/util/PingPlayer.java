package bigbade.pingwars.util;

import net.dv8tion.jda.core.entities.Member;

import java.util.HashMap;
import java.util.Map;

public class PingPlayer {
    //My unsigned long system allows for storage of up to 18,446,744,073,709,551,615 of each number.

    //Amount of pings
    private long pings;
    //Total amount of generators
    private long power;
    //Amount of BP
    private long bossPoints;
    //Guild ID
    private long guild;
    //Last time pings were claimed
    private long lastTime;
    //The member
    private Member member;
    //HashMap of ID and amount of each generator
    private Map<Byte, Long> generators;

    public PingPlayer(Member member, long pings, long power, long bossPoints, long guild, long lastTime, Map<Byte, Long> generators) {
        this.pings = pings;
        this.power = power;
        this.bossPoints = bossPoints;
        this.guild = guild;
        this.member = member;
        this.lastTime = lastTime;
        this.generators = generators;
    }

    public long getPings() {
        return pings;
    }

    public Member getMember() {
        return member;
    }

    public long getPower() {
        return power;
    }

    public long getBossPoints() {
        return bossPoints;
    }

    public long getGuild() {
        return guild;
    }

    //The Long is stored as unsigned (as you cannot have negative pings) so we have to convert it to a good looking number.
    public String getDisplayPings() {
        return Long.toUnsignedString(pings);
    }

    public String getDisplayPower() {
        return Long.toUnsignedString(power);
    }

    public String getDisplayBP() {
        return Long.toUnsignedString(bossPoints);
    }

    public void setPings(long pings) {
        this.pings = pings;
    }

    public void setPower(long power) {
        this.power = power;
    }

    public void setBossPoints(long bossPoints) {
        this.bossPoints = bossPoints;
    }

    public void setGuild(long guild) {
        this.guild = guild;
    }

    public void addPings(long add) {
        this.pings += add;
    }

    public void addPower(long add) {
        this.power += add;
    }

    public void addBossPoints(long add) {
        this.bossPoints += add;
    }

    public boolean equals(Object o) {
        return ((PingPlayer) o).getMember().equals(member);
    }

    public byte[] save(ByteUtils utils) {
        byte amount = 20;
        amount += generators.size()*4;
        byte[] data = new byte[amount];
        System.arraycopy(data, 0, utils.longToBytes(pings), 0, 4);
        return data;
    }

    public static PingPlayer loadPlayer(byte[] data, Member member, ByteUtils utils) {
        byte[] byteData = new byte[4];
        System.arraycopy(data, 0, byteData, 0, 4);
        long pings = utils.bytesToLong(byteData);
        System.arraycopy(data, 4, byteData, 0, 4);
        long power = utils.bytesToLong(byteData);
        System.arraycopy(data, 8, byteData, 0, 4);
        long bossPoints = utils.bytesToLong(byteData);
        System.arraycopy(data, 12, byteData, 0, 4);
        long guild = utils.bytesToLong(byteData);
        System.arraycopy(data, 16, byteData, 0, 4);
        long lastTime = utils.bytesToLong(byteData);
        int gens = data.length-20/5;
        Map<Byte, Long> generators = new HashMap<>();
        for(int i = 0; i < gens; i++) {
            int pos = 20+(i*5);
            byte id = data[pos+1];
            System.arraycopy(data, pos+2, byteData, 0, 4);
            long amount = utils.bytesToLong(byteData);
        }
        return new PingPlayer(member, pings, power, bossPoints, guild, lastTime, generators);
    }
}
