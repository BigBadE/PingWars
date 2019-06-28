package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.*;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;


public class ClaimCommand extends CommandBase {
    public ClaimCommand(PingWars main) {
        super("claim", new String[]{"claim", "c"}, "Claim your pings gotten from generators", PermissionLevel.MEMBER, main);
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
            if (guild != null && guild.getWar() != null) {
                if(guild.checkWar()) {
                    boolean won = guild.warScore() > guild.warScore();
                    guild.endWar(won, event.getGuild(), main.getFileHelper());
                }
                if (pingTarget.getGuild().equals(main.getFileHelper().loadGuild(pingTarget.getGuild(), event.getGuild(),null, null).getWar())) {
                    event.getChannel().sendMessage("You cannot ping " + target.getEffectiveName() + ", you are at war!").queue();
                    return;
                }
                long redeemed = redeem(redeem, main);
                redeem.setLastTime(System.currentTimeMillis());
                redeem.addPings(redeemed);
                guild.warPing(event.getMember().getUser().getIdLong(), redeemed);
                targetGuild.attackPing(pingTarget.getMember().getUser().getIdLong(), redeemed);
                return;
            }
            long redeemed = redeem(redeem, main);
            redeem.setLastTime(System.currentTimeMillis());
            try {
                Boss boss = main.getBosses().get(target);
                boss.loseHP(event.getMember(), redeemed);
                boss.getMessage().editMessage(new EmbedBuilder().setColor(Color.RED).setAuthor(target.getEffectiveName(), null, target.getUser().getEffectiveAvatarUrl()).addField("Boss", "Player: " + target.getUser().getAsTag() + "\nHP: " + boss.getHp() + "/" + boss.getInitialHp() + "", false).setFooter("Ping this player to attack them!", null).build()).queue((boss::setMessage));
                if(boss.getHp() == 0) {
                    long earned = (long) Math.floor(boss.getInitialHp()/10000);
                    event.getChannel().sendMessage("You have defeated the Boss! You earned " + earned + " BP!").queue();
                    redeem.addBossPoints(earned);
                }
            } catch(NullPointerException e) {
                if(redeem.getGuild() != null && redeem.getGuild().equals(pingTarget.getGuild()))
                    event.getChannel().sendMessage("You cannot ping a guildmate!").queue();
                else {
                    redeem.addPings(redeemed);
                    pingTarget.addPings(-redeemed);
                    event.getChannel().sendMessage(event.getMember().getAsMention() + " pinged " + target.getAsMention() + " " + redeemed + " times.").queue();
                }
            }
        }
    }

    public static long redeem(PingPlayer redeem, PingWars main) {
        long redeemed = 0;
        long passed = System.currentTimeMillis() - redeem.getLastTime();
        for (byte generator : redeem.getGenerators().keySet()) {
            Generator generator1 = main.generators.get(generator);
            GeneratorData data = redeem.getGenerators().get(generator1.getId());
            long beforePrestigue = (long) (Math.floor(passed / generator1.getTime()) * generator1.getPings() * data.getAmount());
            redeemed +=  beforePrestigue + (beforePrestigue/(data.getPrestigue()/10));
        }
        return redeemed;
    }
}
