package bigbade.pingwars.api;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.upgrades.Upgrade;
import bigbade.pingwars.util.ByteUtils;

import java.util.*;

public class PlayerGuild {

    private long leader, points, id;
    private Map<Byte, Boolean> upgrades;
    private String war, name;
    private Set<Long> members, elders;
    private Map<Long, Long> warValues;
    private long warTime;

    public PlayerGuild(long id, long leader, long points, Map<Byte, Boolean> upgrades, String war, Set<Long> members, Set<Long> elders, String name) {
        this.id = id;
        this.leader = leader;
        this.points = points;
        this.upgrades = upgrades;
        this.war = war;
        this.members = members;
        this.elders = elders;
        this.name = name;
    }

    public long getLeader() {
        return leader;
    }

    public long getPoints() {
        return points;
    }

    public String getWar() {
        return war;
    }

    public Set<Long> getMembers() {
        return members;
    }

    public String getId() {
        return Long.toHexString(id);
    }

    public Map<Byte, Boolean> getUpgrades() { return upgrades; }

    public String getName() { return name; }

    public boolean hasUpgrade(byte upgrade) {
        return upgrades.get(upgrade);
    }

    public void addMember(long id) {
        this.members.add(id);
    }

    public void warPing(long id, long pings) {
        warValues.replace(id, warValues.get(id)+pings);
    }

    public boolean addUpgrade(byte upgrade) {
        if(!upgrades.get(upgrade))
            upgrades.replace(upgrade, true);
        else
            return false;
        return true;
    }

    public void addPoints(long add) {
        points += add;
    }

    public void promote(long id) {
        elders.add(id);
    }

    public void startWar(String enemy) {
        for(long member : members)
            warValues.put(member, 0L);
        this.war = enemy;
        this.warTime = System.currentTimeMillis();
    }
    public static PlayerGuild load(byte[] data, ByteUtils utils, PingWars main) {
        byte[] byteData = new byte[8];
        System.arraycopy(data, 0, byteData, 0, 8);
        long id = utils.bytesToLong(byteData);
        System.arraycopy(data, 8, byteData, 0, 8);
        long leader = utils.bytesToLong(byteData);
        System.arraycopy(data, 16, byteData, 0, 8);
        long points = utils.bytesToLong(byteData);
        Map<Byte, Boolean> upgrades = new LinkedHashMap<>();
        byte i = 16;
        for(Upgrade upgrade : main.upgrades) {
            upgrades.put(upgrade.getId(), byteData[i] == 0x00);
            i++;
        }
        System.arraycopy(data, i, byteData, 0, 8);
        String war = Long.toHexString(utils.bytesToLong(byteData));
        System.arraycopy(data, i, byteData, 0, 16);
        String name = new String(byteData);
        Set<Long> members = new HashSet<>();
        i += 24;
        while(true) {
            if(data[i] == (byte) 256) {
                i++;
                break;
            } else {
                System.arraycopy(data, i, byteData, 0, 8);
                members.add(utils.bytesToLong(byteData));
                i+=8;
            }
        }
        Set<Long> elders = new HashSet<>();
        for(; i < data.length; i+=8) {
            System.arraycopy(data, i, byteData, 0, 8);
            elders.add(utils.bytesToLong(byteData));
        }
        return new PlayerGuild(id, leader, points, upgrades, war, members, elders, name);
    }

    public byte[] save(ByteUtils utils) {
        byte[] data = new byte[40+upgrades.size()+members.size()*8+elders.size()*8];
        System.arraycopy(utils.longToBytes(id), 0, data, 0, 8);
        System.arraycopy(utils.longToBytes(leader), 0, data, 8, 8);
        System.arraycopy(utils.longToBytes(points), 0, data, 16, 8);
        int i = 16;
        for(Boolean upgrade : upgrades.values()) {
            data[i] = (upgrade) ? 0 : (byte) 1;
            i++;
        }
        System.arraycopy(utils.longToBytes(Long.parseLong(war)), 0, data, i, 8);
        System.arraycopy(name.getBytes(), 0, data, i, 16);
        i+=24;
        for(long member : members) {
            System.arraycopy(utils.longToBytes(member), 0, data, i, 8);
            i+=8;
        }
        data[i+1]=(byte) 256;
        i++;
        for(long elder : elders) {
            System.arraycopy(utils.longToBytes(elder), 0, data, i, 8);
            i+=8;
        }
        return data;
    }
}
