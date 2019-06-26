package bigbade.pingwars.api;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.storage.FlatFileHelper;
import bigbade.pingwars.upgrades.Upgrade;
import bigbade.pingwars.util.ByteUtils;
import bigbade.pingwars.util.TimeUnit;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.*;

public class PlayerGuild {

    private long leader, points, id;
    private Map<Byte, Boolean> upgrades;
    private String war, name;
    private Set<Long> members, elders;
    private Map<Long, Long> warValues, attackValues;
    private List<Member> invites = new ArrayList<>();
    private Guild guild;
    private long warTime;

    public PlayerGuild(long id, long leader, long points, Map<Byte, Boolean> upgrades, String war, Set<Long> members, Set<Long> elders, String name, Guild guild) {
        this.id = id;
        this.leader = leader;
        this.points = points;
        this.upgrades = upgrades;
        this.war = war;
        this.members = members;
        this.elders = elders;
        this.name = name;
        this.guild = guild;
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

    public Map<Byte, Boolean> getUpgrades() {
        return upgrades;
    }

    public String getName() {
        return name;
    }

    public boolean invited(Member member) { return invites.contains(member); }

    public boolean isElder(Member member) { return elders.contains(member.getUser().getIdLong()); }

    public boolean hasUpgrade(byte upgrade) {
        return upgrades.get(upgrade);
    }

    public void addMember(long id) {
        System.out.println("Added to " + this);
        members.add(id);
        System.out.println(members.size());
    }

    public long warScore() {
        long earned = 0;
        for (long points : warValues.values()) {
            earned += points;
        }
        return earned;
    }

    public void warPing(long id, long pings) {
        warValues.replace(id, warValues.get(id) + pings);
    }

    public void attackPing(long id, long pings) {
        attackValues.replace(id, warValues.get(id) + pings);
    }

    public boolean addUpgrade(byte upgrade) {
        if (!upgrades.get(upgrade))
            upgrades.replace(upgrade, true);
        else
            return false;
        return true;
    }

    public boolean checkWar() {
        return warTime - System.currentTimeMillis() >= TimeUnit.HOUR;
    }

    public void endWar(boolean win, Guild guild, FlatFileHelper utils) {
        if (win) {
            for (long player : warValues.keySet())
                utils.loadPlayer(guild.getMemberById(player)).addPings(warValues.get(player) + attackValues.get(player));
        }
        warValues = null;
        attackValues = null;
    }

    public void addPoints(long add) {
        points += add;
    }

    public void invite(Member member) {
        invites.add(member);
    }

    public void removeInvite(Member member) {
        invites.remove(member);
    }

    public void promote(long id, String rank) {
        if(rank.equals("e"))
            elders.add(id);
        else
            leader = id;
    }

    public void startWar(String enemy) {
        warValues = new HashMap<>();
        for (long member : members)
            warValues.put(member, 0L);
        attackValues = new HashMap<>();
        for (long member : members)
            attackValues.put(member, 0L);
        this.war = enemy;
        this.warTime = System.currentTimeMillis();
    }

    public static PlayerGuild load(byte[] data, ByteUtils utils, PingWars main, Guild guild) {
        StringBuilder builder = new StringBuilder();
        byte[] byteData = new byte[8];
        System.arraycopy(data, 0, byteData, 0, 8);
        long id = utils.bytesToLong(byteData);
        System.arraycopy(data, 8, byteData, 0, 8);
        long leader = utils.bytesToLong(byteData);
        System.arraycopy(data, 16, byteData, 0, 8);
        long points = utils.bytesToLong(byteData);
        Map<Byte, Boolean> upgrades = new LinkedHashMap<>();
        byte i = 24;
        for (Upgrade upgrade : main.upgrades) {
            upgrades.put(upgrade.getId(), byteData[i] == 0x00);
            i++;
        }
        System.arraycopy(data, i, byteData, 0, 8);
        String war = Long.toHexString(utils.bytesToLong(byteData));
        if (war.equals("7fffffffffffffff")) war = null;
        i += 8;
        byte[] tempData = new byte[16];
        System.arraycopy(data, i, tempData, 0, 16);
        byte length = 0;
        for (byte byze : tempData)
            if (byze == 0) break;
            else length++;
        byte[] nameBytes = new byte[length];
        System.arraycopy(tempData, 0, nameBytes, 0, length);
        String name = new String(nameBytes);
        Set<Long> members = new HashSet<>();
        i += 16;
        while (true) {
            System.arraycopy(data, i, byteData, 0, 8);
            long member = utils.bytesToLong(byteData);
            if (member == Long.MAX_VALUE) {
                i += 8;
                break;
            } else {
                System.out.println("Found");
                members.add(member);
                i += 8;
            }
        }
        Set<Long> elders = new HashSet<>();
        for (; i < data.length; i += 8) {
            System.arraycopy(data, i, byteData, 0, 8);
            elders.add(utils.bytesToLong(byteData));
        }
        return new PlayerGuild(id, leader, points, upgrades, war, members, elders, name, guild);
    }

    public byte[] save(ByteUtils utils) {
        byte[] data = new byte[56 + upgrades.size() + members.size() * 8 + elders.size() * 8];
        System.arraycopy(utils.longToBytes(id), 0, data, 0, 8);
        System.arraycopy(utils.longToBytes(leader), 0, data, 8, 8);
        System.arraycopy(utils.longToBytes(points), 0, data, 16, 8);
        int i = 24;
        for (Boolean upgrade : upgrades.values()) {
            data[i] = (upgrade) ? 0 : (byte) 1;
            i++;
        }
        if (war == null) war = "7fffffffffffffff";
        System.arraycopy(utils.longToBytes(Long.parseLong(war, 16)), 0, data, i, 8);
        i += 8;
        byte[] nameArray = new byte[16];
        Arrays.fill(nameArray, (byte) 0);
        byte[] nameBytes = name.getBytes();
        System.arraycopy(nameBytes, 0, nameArray, 0, name.length());
        System.arraycopy(nameArray, 0, data, i, 16);
        i += 16;
        for (long member : members) {
            System.arraycopy(utils.longToBytes(member), 0, data, i, 8);
            i += 8;
        }
        System.arraycopy(utils.longToBytes(Long.MAX_VALUE), 0, data, i, 8);
        i+=8;
        for (long elder : elders) {
            System.arraycopy(utils.longToBytes(elder), 0, data, i, 8);
            i += 8;
        }
        return data;
    }

    public Guild getGuild() {
        return guild;
    }
}
