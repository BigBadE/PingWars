package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.PermissionLevel;
import bigbade.pingwars.api.GuildConfig;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class ConfigCommand extends CommandBase {

    public ConfigCommand(PingWars main) {
        super("config", new String[]{"config", "cfg", "configuration"}, "Configure PingBot's settings", PermissionLevel.ADMINISTRATOR, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if (args.length == 1) {
            GuildConfig config = main.getFileHelper().loadGuildConfig(event.getGuild());
            String cmdChannel;
            if (config.getCommandChannels() == null) {
                cmdChannel = "None";
            } else {
                StringBuilder cmdChannels = new StringBuilder();
                for (int i = 0; i < config.getCommandChannels().length; i++) {
                    cmdChannels.append("#").append(config.getCommandChannels()[i].getName());
                    if (i < config.getCommandChannels().length - 1) cmdChannels.append(", ");
                }
                cmdChannel = cmdChannels.toString();
            }
            event.getChannel().sendMessage(new EmbedBuilder().addField(new MessageEmbed.Field("Ping Channel", "Set the channel used for pings.\nUsage: " + main.prefix + "config pingchannel #channelname\nCurrent value: " + ((config.getPingChannel() == null) ? "None" : "#" + config.getPingChannel().getName()) + "\n\n", false))
                            .addField(new MessageEmbed.Field("Command Channel", "Set the channel PingBot commands can be used in.\nUsage: " + main.prefix + "config commandchannel #channelname #channel2name #channel3name etc...\nCurrent value: " + cmdChannel, false)).setTitle("Configuration").setColor(Color.GREEN).build()).queue();
        } else {
            if (args[1].equalsIgnoreCase("pingchannel")) {
                if(args.length != 3) {
                    if (!args[2].contains("#<")) {
                        event.getChannel().sendMessage(args[2] + " is not a recognised channel. Make sure you put a # and clicked on the channel name.").queue();
                        return;
                    }
                    main.getFileHelper().loadGuildConfig(event.getGuild()).setPingChannel(event.getGuild().getTextChannelById(args[2].replace("<#", "").replace(">", "")));
                    event.getChannel().sendMessage("Set ping channel!").queue();
                } else
                    event.getChannel().sendMessage("You have to specify a channel!").queue();
                } else if (args[1].equalsIgnoreCase("commandchannel")) {
                if(args.length >= 3) {
                    Channel[] commandChannels = new Channel[args.length - 2];
                    for (int i = 2; i < args.length; i++) {
                        if (!args[i].contains("#")) {
                            event.getChannel().sendMessage(args[i] + " is not a recognised channel. Make sure you put a # and clicked on the channel name.").queue();
                            return;
                        }
                        commandChannels[i - 2] = event.getGuild().getTextChannelById(args[i].replace("<#", "").replace(">", ""));
                    }
                    main.getFileHelper().loadGuildConfig(event.getGuild()).setCommandChannels(commandChannels);
                    event.getChannel().sendMessage("Set command channel(s)!").queue();
                } else
                    event.getChannel().sendMessage("You have to specify a channel!").queue();
            }
        }
    }
}