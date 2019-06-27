package bigbade.pingwars.listeners;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Random;

public class MessageListener extends ListenerAdapter {
    private PingWars main;
    private Random random = new Random();

    private Member boss;

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
                if(main.checkBoss(event.getGuild())) {
                    try {
                        main.getBosses().remove(event.getMember());
                    } catch(NullPointerException ignored) {}
                    do {
                        boss = event.getGuild().getMembers().get(random.nextInt(event.getGuild().getMembers().size()));
                    } while (boss.getUser().isBot() || boss.getUser().getIdLong() == event.getMember().getUser().getIdLong());
                    PingPlayer player = main.getFileHelper().loadPlayer(event.getMember());
                    long hp = player.getPower()*100+(player.getPings()*20);
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setAuthor(boss.getEffectiveName(), null, boss.getUser().getEffectiveAvatarUrl()).addField("Boss", "Player: " + boss.getUser().getAsTag() + "\nHP: " + hp + "/" + hp + "", false).setFooter("Ping this player to attack them!", null).build()).queue((message1) -> main.addBoss(boss, hp, message1));
                }
                GuildConfig config = main.getFileHelper().loadGuildConfig(event.getGuild());
                String name = message.substring(main.prefix.length()).split(" ")[0];
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
            for (int i = 1; i < Math.min(mentions.length, 5); i++) {
                String id = mentions[i].split(">")[0];
                PingPlayer target = main.getFileHelper().loadPlayer(event.getGuild().getMemberById(id));
                try {
                    Boss boss = main.getBosses().get(target.getMember());
                    boss.loseHP(1);
                    boss.getMessage().editMessage(new EmbedBuilder().setColor(Color.RED).setAuthor(target.getMember().getEffectiveName(), null, target.getMember().getUser().getEffectiveAvatarUrl()).addField("Boss", "Player: " + target.getMember().getUser().getAsTag() + "\nHP: " + boss.getHp() + "/" + boss.getInitialHp() + "", false).setFooter("Ping this player to attack them!", null).build()).queue((boss::setMessage));
                    if(boss.getHp() == 0) {
                        long earned = (long) Math.floor(boss.getInitialHp()/10000);
                        event.getChannel().sendMessage("You have defeated the Boss! You earned " + earned + " BP!").queue();
                        pingPlayer.addBossPoints(earned);
                    }
                } catch(NullPointerException e) {
                    if (target.getGuild() != null && target.getGuild().equals(pingPlayer.getGuild()) || event.getGuild().getMemberById(id).getUser().isBot())
                        continue;
                    target.addPings(-1);
                    pingPlayer.addPings(1);
                }
            }
        }
    }
}
