package bigbade.pingwars.util;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;

public class GuildConfig {
    private Channel pingChannel;
    private Channel[] commandChannels;

    public GuildConfig(Channel pingChannel, Channel[] commandChannels) {
        this.commandChannels = commandChannels;
        this.pingChannel = pingChannel;
    }

    public Channel getPingChannel() {
        return pingChannel;
    }

    public Channel[] getCommandChannels() {
        return commandChannels;
    }

    public void setPingChannel(Channel pingChannel) {
        this.pingChannel = pingChannel;
    }

    public void setCommandChannels(Channel[] commandChannels) {
        this.commandChannels = commandChannels;
    }

    public byte[] save(ByteUtils utils) {
        byte[] data = new byte[4+commandChannels.length*4];
        byte[] temp = utils.longToBytes(pingChannel.getIdLong());
        System.arraycopy(temp, 0, data, 0, 4);
        for(int i = 0; i < commandChannels.length; i++) {
            temp = utils.longToBytes(commandChannels[i].getIdLong());
            System.arraycopy(temp, 0, data, 4+i*4, 4);
        }
        return data;
    }

    public static GuildConfig load(byte[] data, ByteUtils utils, Guild guild) {
        byte[] temp = new byte[4];
        System.arraycopy(data, 0, temp, 0, 4);
        Channel pingChannel = guild.getTextChannelById(utils.bytesToLong(temp));
        Channel[] commandChannels = new Channel[(data.length-4)/4];
        for(int i = 0; i < (data.length-4)/4; i++) {
            System.arraycopy(data, 4+i*4, temp, 0, 4);
            commandChannels[i] = guild.getTextChannelById(utils.bytesToLong(temp));
        }
        return new GuildConfig(pingChannel, commandChannels);
    }

    public boolean equals(Object o) {
        return o.equals(pingChannel.getGuild());
    }
}
