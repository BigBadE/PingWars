package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.Generator;
import bigbade.pingwars.api.PermissionLevel;
import bigbade.pingwars.api.PingPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class BuyCommand extends CommandBase {
    public BuyCommand(PingWars main) {
        super("buy", new String[]{"buy", "b", "shop", "store"}, "Buy generators to get more pings", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        PingPlayer pingPlayer = main.getFileHelper().loadPlayer(event.getMember());
        if (args.length == 1) {
            event.getChannel().sendMessage(print(0, pingPlayer)).queue();
        } else if (args.length >= 2) {
            try {
                int page = Integer.parseInt(args[1]);
                page = (int) Math.min(page, Math.floor(pingPlayer.getGenerators().size() / 5));
                event.getChannel().sendMessage(print(page, pingPlayer)).queue();
            } catch (NumberFormatException e) {
                Generator generator = null;
                boolean added = true;
                long adding = 1;
                try {
                    adding = Long.parseUnsignedLong(args[args.length - 1]);
                } catch (NumberFormatException ignored) {
                    added = false;
                }
                StringBuilder name = new StringBuilder(args[1] + " ");
                for (int i = 2; i < ((added) ? args.length - 1 : args.length); i++)
                    name.append(args[i]).append(" ");
                name.deleteCharAt(name.length() - 1);
                for (Generator repeatGenerator : main.generators)
                    if (repeatGenerator.getName().equalsIgnoreCase(name.toString()))
                        generator = repeatGenerator;
                if (generator != null) {
                    if (Long.compareUnsigned(pingPlayer.getPings(), generator.getPrice() * adding) >= 0) {
                        pingPlayer.addPings(-(generator.getPrice() * adding));
                        pingPlayer.addPower(adding);
                        pingPlayer.addGenerator(generator, adding);
                        event.getChannel().sendMessage("You have bought " + adding + " " + generator.getName() + ((added) ? "s" : "") + ".").queue();
                    } else {
                        event.getChannel().sendMessage("You do not have " + generator.getPrice()*adding + " pings.").queue();
                    }
                } else
                    event.getChannel().sendMessage("There is no generator by the name of " + name.toString()).queue();
            }
        }
    }

    private MessageEmbed print(int page, PingPlayer pingPlayer) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.addField("Use " + main.prefix + "buy (name) to buy a generator.", "", false);
        for (int i = 0; i < Math.min(page * 5 + 5, main.generators.size()); i++) {
            Generator generator = main.generators.get(i);
            long amount;
            amount = pingPlayer.getGenerators().get(generator.getId());
            builder.addField(generator.getName(), generator.getDescription() + "\nPrice: " + generator.getPrice() + " Owned: " + amount, false);
        }
        return builder.build();
    }
}