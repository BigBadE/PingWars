package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.Generator;
import bigbade.pingwars.api.PermissionLevel;
import bigbade.pingwars.util.PingPlayer;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;


public class ClaimCommand extends CommandBase {
    public ClaimCommand(PingWars main) {
        super("claim", new String[] { "claim", "c" }, "Claim your pings", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if(args.length != 2) event.getChannel().sendMessage("You have to ping a player to target. Usage: " + main.prefix + "claim @(user)").queue();
        else {
            Member target = event.getGuild().getMemberById(args[1].replace("<@", "").replace(">", ""));
            PingPlayer redeem = main.getFileHelper().loadPlayer(event.getMember());
            PingPlayer pingTarget = main.getFileHelper().loadPlayer(target);
            long redeemed = 0;
            long passed = System.currentTimeMillis()-redeem.getLastTime();
            for(byte generator : redeem.getGenerators().keySet()) {
                Generator generator1 = main.generators.get(generator);
                redeemed += Math.floor(passed/generator1.getTime())*generator1.getPings();
            }
            redeem.addPings(redeemed);
            pingTarget.addPings(-redeemed);
            event.getChannel().sendMessage(event.getMember().getAsMention() + " pinged " + target.getAsMention() + " " + redeemed + " times.").queue();
        }
    }
}
