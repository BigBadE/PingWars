package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.PermissionLevel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StopCommand extends CommandBase {

    public StopCommand(PingWars main) {
        super("shutdown", new String[] { "shutdown"}, "Stop the bot", PermissionLevel.ADMINISTRATOR, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if(event.getAuthor().getId().equals("425313981767352341")) {
            event.getChannel().sendMessage("Stopping").queue();
            main.stop();
        } else {
            event.getChannel().sendMessage("You are not Big_Bad_E!").queue();
        }
    }
}
