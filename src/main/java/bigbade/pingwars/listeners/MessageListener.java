package bigbade.pingwars.listeners;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.GuildConfig;
import bigbade.pingwars.api.PermissionLevel;
import bigbade.pingwars.api.PingPlayer;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    private PingWars main;

    public MessageListener(PingWars main) {
        this.main = main;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentRaw();
        if (message == null) return;
        if (message.length() >= main.prefix.length()) {
            if (message.substring(0, main.prefix.length()).equalsIgnoreCase(main.prefix)) {
                GuildConfig config = main.getFileHelper().loadGuildConfig(event.getGuild());
                String name = message.substring(main.prefix.length()).split(" ")[0];
                //Useless if statement so the label will encompass the return.
                channelCheck:
                if (config.getCommandChannels() != null) {
                    for (Channel channel : config.getCommandChannels()) {
                        if (event.getChannel().equals(channel))
                            break channelCheck;
                    }
                    return;
                }
                for (CommandBase command : main.commands)
                    for (String alias : command.getAliases())
                        if (name.equalsIgnoreCase(alias)) {
                            if (PermissionLevel.getLevel(event.getMember(), event.getChannel()).ordinal() <= command.getPerm().ordinal())
                                command.onCommand(event, message.split(" "));
                            else
                                event.getChannel().sendMessage("You do not have permission to run that command!").queue();
                            return;
                        }
            }
        }
        String[] mentions = message.split("<@");
        if (mentions.length > 0) {
            PingPlayer pingPlayer = main.getFileHelper().loadPlayer(event.getMember());
            for (int i = 1; i < mentions.length; i++) {
                String id = mentions[i].split(">")[0];
                PingPlayer target = main.getFileHelper().loadPlayer(event.getGuild().getMemberById(id));
                if (target.getGuild() != null && target.getGuild().equals(pingPlayer.getGuild()) || event.getGuild().getMemberById(id).getUser().isBot()) continue;
                target.addPings(-1);
                pingPlayer.addPings(1);
            }
        }
    }
}
