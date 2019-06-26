package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.PermissionLevel;
import bigbade.pingwars.api.PingPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class InfoCommand extends CommandBase {
    public InfoCommand(PingWars main) {
        super("info", new String[] { "info", "information", "stats" }, "See your stats!", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        PingPlayer pingPlayer = main.getFileHelper().loadPlayer(event.getMember());
        event.getChannel().sendMessage(new EmbedBuilder().setAuthor(event.getMember().getEffectiveName(), null, event.getAuthor().getEffectiveAvatarUrl()).setColor(Color.GREEN).setTitle("Stats for " + event.getAuthor().getAsTag())
                .addField(new MessageEmbed.Field("Pings: " + pingPlayer.getDisplayPings(), "Power: " + pingPlayer.getDisplayPower() + "\nBoss Points: " + pingPlayer.getDisplayBP() + "\nCurrent Guild: " + ((pingPlayer.getGuild() != null) ? main.getFileHelper().loadGuild(pingPlayer.getGuild(), event.getGuild(),null, null).getName() : "None"), false)).build()).queue();
    }
}
