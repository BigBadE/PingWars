package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.PermissionLevel;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class HelpCommand extends CommandBase {
    public HelpCommand(PingWars main) {
        super("help", new String[]{"help", "commands", "helpme"}, "Figure out what commands do", PermissionLevel.USER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.addField("Ping players to get pings!", "", false);
        for (CommandBase base : main.commands) {
            if (!base.getId().equals("shutdown"))
                builder.addField(main.prefix + base.getId(), base.getDescription(), false);
        }
        event.getChannel().sendMessage(builder.build()).queue();
    }
}
