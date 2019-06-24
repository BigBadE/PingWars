package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.PermissionLevel;
import bigbade.pingwars.util.GuildConfig;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ConfigCommand extends CommandBase {
    private PingWars main;

    public ConfigCommand(PingWars main) {
        super("config", new String[] {"config", "cfg", "configuration"}, "Configure PingBot's settings", PermissionLevel.ADMINISTRATOR);
        this.main = main;
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if(args.length == 1) {
            GuildConfig config = main.getFileHelper().loadGuild(event.getGuild());
            String cmdChannel;
            if(config.getCommandChannels() == null)
                cmdChannel = "None";
            else {
                StringBuilder cmdChannels = new StringBuilder();
                for (Channel channel : config.getCommandChannels())
                    cmdChannels.append("#" + channel.getName() + ", ");
                cmdChannel = cmdChannels.toString();
            }
            event.getChannel().sendMessage("Settings:\nPing Channel: Set the channel used for pings.\nUsage: " + main.prefix + "config pingchannel #channelname\nCurrent value: " + ((config.getPingChannel() == null) ? "None" : "#"+config.getPingChannel().getName()) + "\n" +
                    "Command Channel: Set the channel PingBot commands can be used in.\nUsage: " + main.prefix + "config commandchannel #channelname #channel2name #channel3name etc...\nCurrent value: " + cmdChannel).queue();
        } else {
            if(args[1].equalsIgnoreCase("pingchannel")) {
                if(!args[2].contains("#<")) {
                    event.getChannel().sendMessage(args[2] + " is not a recognised channel. Make sure you put a # and clicked on the channel name.").queue();
                    return;
                }
                main.getFileHelper().loadGuild(event.getGuild()).setPingChannel(event.getGuild().getTextChannelById(args[2].replace("<#", "").replace(">", "")));
                event.getChannel().sendMessage("Set ping channel!").queue();
            } else if(args[1].equalsIgnoreCase("commandchannel")) {
                Channel[] commandChannels = new Channel[args.length];
                for(int i = 2; i < args.length; i++) {
                    if(!args[i].contains("#")) {
                        event.getChannel().sendMessage(args[i] + " is not a recognised channel. Make sure you put a # and clicked on the channel name.").queue();
                        return;
                    }
                    commandChannels[i] = event.getGuild().getTextChannelById(args[i].replace("<#", "").replace(">", ""));
                }
                main.getFileHelper().loadGuild(event.getGuild()).setCommandChannels(commandChannels);
                event.getChannel().sendMessage("Set command channel(s)!").queue();
            }
        }
    }
}