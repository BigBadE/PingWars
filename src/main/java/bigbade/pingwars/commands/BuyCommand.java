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
        super("buy", new String[]{"buy", "b", "shop", "store"}, "Buy generators to get more pings. You can add a number after the name to buyh multiple", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        PingPlayer pingPlayer = main.getFileHelper().loadPlayer(event.getMember());
        if (args.length == 1) {
            event.getChannel().sendMessage(print(0, pingPlayer, pingPlayer.getGenerators().size())).queue();
        } else if (args.length >= 2) {
            try {
                int page = Integer.parseInt(args[1]);
                page = (int) Math.min(page*5, Math.ceil(pingPlayer.getGenerators().size() / 5));
                if(page == 0) page = 1;
                event.getChannel().sendMessage(print(page*5, pingPlayer, pingPlayer.getGenerators().size())).queue();
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
                    if (repeatGenerator.getName().equalsIgnoreCase(name.toString()) || repeatGenerator.getName().split(" ")[0].equalsIgnoreCase(args[1]))
                        generator = repeatGenerator;
                if (generator != null) {
                    //Check if they have enough pings
                    if (Long.compareUnsigned(pingPlayer.getPings(), generator.getPrice() * adding) >= 0) {
                        //Add the gen and take the pings
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

    /**
     * Print out the buy menu
     * @param gens How many gens to print
     * @param pingPlayer The player
     * @param maxGen Amount of gens the player has
     * @return the embed
     */
    private MessageEmbed print(int gens, PingPlayer pingPlayer, int maxGen) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.addField("Use " + main.prefix + "buy (name) to buy a generator.", "", false);
        for (int i = gens; i < Math.min(gens + 5, maxGen+1); i++) {
            Generator generator = main.generators.get(i);
            long amount;
            try {
                amount = pingPlayer.getGenerators().get(generator.getId()).getAmount();
            } catch (NullPointerException ignored) {
                amount = 0;
            }
            StringBuilder price = new StringBuilder();
            if(generator.getPrice() > 0)
                price.append(generator.getPrice()).append(" Pings ");
            if(generator.getBpPrice() > 0)
                price.append(generator.getBpPrice()).append(" BP ");
            builder.addField(generator.getName(), generator.getDescription() + "\nPrice: " + price.toString() + "| Owned: " + amount, false);
            builder.setFooter("Page " + ((int) Math.ceil(gens/5)+1) + "/" + (int) (Math.ceil(Math.max(pingPlayer.getGenerators().size(), 1)/5)+1), null);
        }
        return builder.build();
    }
}
