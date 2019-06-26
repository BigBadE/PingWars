package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.*;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class ClaimCommand extends CommandBase {
    public ClaimCommand(PingWars main) {
        super("claim", new String[]{"claim", "c"}, "Claim your pings", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if (args.length != 2)
            event.getChannel().sendMessage("You have to ping a player to target. Usage: " + main.prefix + "claim @(user)").queue();
        else {
            Member target = event.getGuild().getMemberById(args[1].replace("<@", "").replace(">", ""));
            PingPlayer redeem = main.getFileHelper().loadPlayer(event.getMember());
            PingPlayer pingTarget = main.getFileHelper().loadPlayer(target);
            PlayerGuild guild = main.getFileHelper().loadGuild(redeem.getGuild(), event.getGuild(),null, null);
            PlayerGuild targetGuild = main.getFileHelper().loadGuild(pingTarget.getGuild(), event.getGuild(),null, null);
            if (guild.getWar() != null) {
                if(guild.checkWar()) {
                    boolean won = guild.warScore() > guild.warScore();
                    guild.endWar(won, event.getGuild(), main.getFileHelper());
                }
                if (pingTarget.getGuild().equals(main.getFileHelper().loadGuild(pingTarget.getGuild(), event.getGuild(),null, null).getWar())) {
                    event.getChannel().sendMessage("You cannot ping " + target.getEffectiveName() + ", you are at war!").queue();
                    return;
                }
                long redeemed = redeem(redeem);
                redeem.addPings(redeemed);
                guild.warPing(event.getMember().getUser().getIdLong(), redeemed);
                targetGuild.attackPing(pingTarget.getMember().getUser().getIdLong(), redeemed);
                return;
            }
            long redeemed = redeem(redeem);
            redeem.addPings(redeemed);
            pingTarget.addPings(-redeemed);
            event.getChannel().sendMessage(event.getMember().getAsMention() + " pinged " + target.getAsMention() + " " + redeemed + " times.").queue();
        }
    }

    private long redeem(PingPlayer redeem) {
        long redeemed = 0;
        long passed = System.currentTimeMillis() - redeem.getLastTime();
        for (byte generator : redeem.getGenerators().keySet()) {
            Generator generator1 = main.generators.get(generator);
            redeemed += Math.floor(passed / generator1.getTime()) * generator1.getPings();
        }
        redeem.setLastTime(System.currentTimeMillis());
        return redeemed;
    }
}
