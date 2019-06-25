package bigbade.pingwars.util;

import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;

public class GuildConfig {
    private Channel pingChannel;
    private Channel[] commandChannels;
    private Guild guild;

    public GuildConfig(Channel pingChannel, Channel[] commandChannels, Guild guild) {
        this.commandChannels = commandChannels;
        this.pingChannel = pingChannel;
        this.guild = guild;
    }

    public Channel getPingChannel() {
        return pingChannel;
    }

    public Channel[] getCommandChannels() {
        return commandChannels;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setPingChannel(Channel pingChannel) {
        this.pingChannel = pingChannel;
    }

    public void setCommandChannels(Channel[] commandChannels) {
        this.commandChannels = commandChannels;
    }

    public byte[] save(ByteUtils utils) {
        byte[] data = new byte[8 + commandChannels.length * 8];
        byte[] temp;
        if (pingChannel == null)
            temp = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        else
            temp = utils.longToBytes(pingChannel.getIdLong());
        System.arraycopy(temp, 0, data, 0, 8);
        if (commandChannels != null)
            for (int i = 0; i < commandChannels.length; i++) {
                temp = utils.longToBytes(commandChannels[i].getIdLong());
                System.arraycopy(temp, 0, data, 8 + i * 8, 8);
            }
        return data;
    }

    public static GuildConfig load(byte[] data, ByteUtils utils, Guild guild) {
        byte[] temp = new byte[8];
        System.arraycopy(data, 0, temp, 0, 8);
        long id = utils.bytesToLong(temp);
        Channel pingChannel;
        if(id == 0)
            pingChannel = null;
        else
            pingChannel = guild.getTextChannelById(id);
        Channel[] commandChannels = new Channel[(data.length - 8) / 8];
        for (int i = 0; i < (data.length - 8) / 8; i++) {
            System.arraycopy(data, 8 + i * 8, temp, 0, 8);
            commandChannels[i] = guild.getTextChannelById(utils.bytesToLong(temp));
        }
        if(commandChannels.length == 0)
            commandChannels = null;
        return new GuildConfig(pingChannel, commandChannels, guild);
    }

    public boolean equals(Object o) {
        return o.equals(guild);
    }
}
