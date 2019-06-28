package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class PrestigeCommand extends CommandBase {
    public PrestigeCommand(PingWars main) {
        super("prestige", new String[]{"prestige", "prestigue", "pres", "p"}, "Prestige a generator, gaining a 15% bonus to pings", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            StringBuilder name = new StringBuilder(args[1] + " ");
            for (int i = 2; i < args.length; i++)
                name.append(args[i]).append(" ");
            Generator generator = null;
            name.deleteCharAt(name.length() - 1);
            for (Generator repeatGenerator : main.generators)
                if (repeatGenerator.getName().equalsIgnoreCase(name.toString()) || repeatGenerator.getName().split(" ")[0].equalsIgnoreCase(args[1]))
                    generator = repeatGenerator;
            if (generator != null) {
                PingPlayer player = main.getFileHelper().loadPlayer(event.getMember());
                GeneratorData data = player.getGenerators().get(generator.getId());
                long needed = (long) Math.pow(2, data.getPrestigue()-1)*100;
                if(Long.compareUnsigned(data.getAmount(), needed) <= 0) {
                    data.addPrestigue();
                    data.removeAmount(-needed);
                }
            } else
                event.getChannel().sendMessage("Could not find a generator with that name!").queue();
        } else
            event.getChannel().sendMessage("You have to select a generator to prestige").queue();
    }
}
