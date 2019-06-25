package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.*;
import bigbade.pingwars.upgrades.Upgrade;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class GuildCommand extends CommandBase {
    public GuildCommand(PingWars main) {
        super("guild", new String[]{"guild", "g"}, "Command for guilds", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if (args.length == 1)
            event.getChannel().sendMessage(new EmbedBuilder().addField("Guild", "Create: Create a guild.\nShop: Buy guild upgrades.\nWar: Command for guild wars.\nInfo: View guild stats.", false).build()).queue();
        else {
            switch (args[1]) {
                case "Create":
                    if (args.length == 2)
                        event.getChannel().sendMessage("You need to specify a name!").queue();
                    else {
                        main.getFileHelper().loadGuild(null, event.getMember(), args[2]);
                        event.getChannel().sendMessage("Successfully created guild").queue();
                    }
                    break;
                case "Shop":
                    if (args.length == 2)
                        print(0, main.getFileHelper().loadGuild(null, event.getMember(), null));
                    else {
                        PlayerGuild guild = main.getFileHelper().loadGuild(null, event.getMember(), null);
                        try {
                            int page = Integer.parseInt(args[1]);
                            page = (int) Math.min(page, Math.floor(guild.getUpgrades().size() / 5));
                            event.getChannel().sendMessage(print(page, guild)).queue();
                        } catch (NumberFormatException e) {
                            StringBuilder name = new StringBuilder(args[2] + " ");
                            for (int i = 3; i < args.length; i++)
                                name.append(args[i]).append(" ");
                            name.deleteCharAt(name.length() - 1);
                            Upgrade upgrade = null;
                            for (Upgrade repeatUpgrade : main.upgrades)
                                if (repeatUpgrade.getName().equalsIgnoreCase(name.toString()))
                                    upgrade = repeatUpgrade;
                            if (upgrade != null) {
                                if (Long.compareUnsigned(guild.getPoints(), upgrade.getPrice()) >= 0) {
                                    guild.addUpgrade(upgrade.getId());
                                    guild.addPoints(-(upgrade.getPrice()));
                                    event.getChannel().sendMessage("You have bought " + upgrade.getName() + ".").queue();
                                } else {
                                    event.getChannel().sendMessage("You do not have " + upgrade.getPrice() + " GP.").queue();
                                }
                            } else
                                event.getChannel().sendMessage("There is no upgrade by the name of " + name.toString()).queue();
                        }
                    }
                    break;
                case "War":
                    if(args.length == 2)
                        event.getChannel().sendMessage(main.prefix + "guild war (Guild ID)").queue();
                    else {
                        PlayerGuild guild = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), null, null);
                        if(guild.getLeader() == event.getAuthor().getIdLong()) {
                            PlayerGuild enemy = main.getFileHelper().loadGuild(args[2], null, null);
                            guild.startWar(enemy.getId());
                            enemy.startWar(guild.getId());
                            event.getChannel().sendMessage(guild.getName() + " declared war on " + enemy.getName() + "!").queue();
                        } else
                            event.getChannel().sendMessage("You have to be a guild leader to start a war!").queue();
                    }
                    break;
                case "Info":
                    PlayerGuild guild;
                    if(args.length == 2) {
                        guild = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), null, null);
                    } else {
                        guild = main.getFileHelper().loadGuild(args[2], null, null);
                        if(guild == null) {
                            event.getChannel().sendMessage("Could not find a guild with an ID of " + args[2]).queue();
                            return;
                        }
                    }
                    long power = 0;
                    for(long member : guild.getMembers())
                        power += main.getFileHelper().loadPlayer(event.getGuild().getMemberById(member)).getPower();
                    Member leader = event.getGuild().getMemberById(guild.getLeader());
                    event.getChannel().sendMessage(new EmbedBuilder().addField(guild.getName(), "Power: " + Long.toUnsignedString(power) + "\nGP: " + guild.getPoints() + "\nMembers: " + guild.getMembers().size() + "\nLeader: " + leader.getEffectiveName() + "#" + leader.getUser().getAsTag(), false).setColor(Color.GREEN).build()).queue();
            }
        }
    }

    private MessageEmbed print(int page, PlayerGuild guild) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.addField("Use " + main.prefix + "buy (name) to buy a generator.", "", false);
        for (int i = 0; i < Math.min(page * 5 + 5, main.generators.size()); i++) {
            Generator generator = main.generators.get(i);
            boolean owned;
            owned = guild.getUpgrades().get(generator.getId());
            builder.addField(generator.getName(), generator.getDescription() + "\nPrice: " + generator.getPrice() + " Bought: " + ((owned) ? "Yes" : "No"), false);
        }
        return builder.build();
    }
}
