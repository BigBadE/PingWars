package bigbade.pingwars.util;

import bigbade.pingwars.api.Generator;
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

    public Map<Byte, Long> getGenerators() { return generators; }

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

    public Long getLastTime() { return getLastTime(); }

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
        pings = Math.max(0, pings);
    }

    public void addPower(long add) {
        this.power += add;
    }

    public void addBossPoints(long add) {
        this.bossPoints += add;
    }

    public void addGenerator(Generator generator, long adding) {
        long amount = generators.get(generator.getId());
        if(amount == -1)
            generators.put(generator.getId(), amount);
        else
            generators.replace(generator.getId(), amount+adding);
    }

    public byte[] save(ByteUtils utils) {
        int amount = 40;
        amount += generators.size()*9;
        byte[] data = new byte[amount];
        System.arraycopy(utils.longToBytes(pings), 0, data, 0, 8);
        System.arraycopy(utils.longToBytes(power), 0, data, 8, 8);
        System.arraycopy(utils.longToBytes(bossPoints), 0, data, 16, 8);
        System.arraycopy(utils.longToBytes(guild), 0, data, 24, 8);
        System.arraycopy(utils.longToBytes(lastTime), 0, data, 32, 8);
        int i = 40;
        for(byte generator : generators.keySet()) {
            data[i+1] = generator;
            System.arraycopy(utils.longToBytes(generators.get(generator)), 0, data, i+2, 8);
            i+=9;
        }
        return data;
    }

    public static PingPlayer loadPlayer(byte[] data, Member member, ByteUtils utils) {
        byte[] byteData = new byte[8];
        System.arraycopy(data, 0, byteData, 0, 8);
        long pings = utils.bytesToLong(byteData);
        System.arraycopy(data, 8, byteData, 0, 8);
        long power = utils.bytesToLong(byteData);
        System.arraycopy(data, 16, byteData, 0, 8);
        long bossPoints = utils.bytesToLong(byteData);
        System.arraycopy(data, 24, byteData, 0, 8);
        long guild = utils.bytesToLong(byteData);
        System.arraycopy(data, 32, byteData, 0, 8);
        long lastTime = utils.bytesToLong(byteData);
        Map<Byte, Long> generators = new HashMap<>();
        if(data.length != 40) {
            int gens = (data.length - 40) / 9;
            for (int i = 0; i < gens; i++) {
                int pos = 40 + (i * 9);
                byte id = data[pos + 1];
                System.arraycopy(data, pos + 2, byteData, 0, 8);
                long amount = utils.bytesToLong(byteData);
                generators.put(id, amount);
            }
        }
        return new PingPlayer(member, pings, power, bossPoints, guild, lastTime, generators);
    }

    public boolean equals(Object o) {
        return ((PingPlayer) o).getMember().equals(member);
    }
}
