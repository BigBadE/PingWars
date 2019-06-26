package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.*;
import bigbade.pingwars.upgrades.Upgrade;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class GuildCommand extends CommandBase {
    public GuildCommand(PingWars main) {
        super("guild", new String[]{"guild", "g", "guilds" }, "Command for guilds", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if (args.length == 1)
            event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.GREEN).addField("Guild", "Create: Create a guild.\nShop: Buy guild upgrades.\nWar: Command for guild wars.\nInfo: View guild stats.\nLeave: Leave your guild.\nWar (Owner only: Start a guild war.\nPromote (Owner only): Promote another member to Owner.\nDisband: Disband the guild.", false).build()).queue();
        else {
            switch (args[1]) {
                case "create":
                    if (main.getFileHelper().loadPlayer(event.getMember()).getGuild() == null)
                        if (args.length == 2)
                            event.getChannel().sendMessage("You need to specify a name!").queue();
                        else {
                            StringBuilder name = new StringBuilder(args[2] + " ");
                            for (int i = 3; i < args.length; i++)
                                name.append(args[i]).append(" ");
                            name.deleteCharAt(name.length() - 1);
                            if(name.length() > 16)
                                event.getChannel().sendMessage("Guild names must be less than 17 characters long!").queue();
                            else {
                                main.getFileHelper().loadGuild(null, event.getGuild(),event.getMember(), name.toString());
                                event.getChannel().sendMessage("Successfully created guild").queue();
                            }
                        }
                    else
                        event.getChannel().sendMessage("You cannot create a guild if you are in a guild!").queue();
                    break;
                case "shop":
                    if (args.length == 2)
                        print(0, main.getFileHelper().loadGuild(null, event.getGuild(),event.getMember(), null));
                    else {
                        PlayerGuild guild = main.getFileHelper().loadGuild(null, event.getGuild(),event.getMember(), null);
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
                case "war":
                    if (args.length == 2)
                        event.getChannel().sendMessage(main.prefix + "guild war (Guild ID)").queue();
                    else {
                        PlayerGuild guild = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(),null, null);
                        if (guild.getLeader() == event.getAuthor().getIdLong()) {
                            PlayerGuild enemy = main.getFileHelper().loadGuild(args[2], event.getGuild(),null, null);
                            guild.startWar(enemy.getId());
                            enemy.startWar(guild.getId());
                            event.getChannel().sendMessage(guild.getName() + " declared war on " + enemy.getName() + "!").queue();
                        } else
                            event.getChannel().sendMessage("You have to be a guild leader to start a war!").queue();
                    }
                    break;
                case "info":
                    PlayerGuild guild;
                    if (args.length == 2) {
                        guild = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(),null, null);
                    } else {
                        guild = main.getFileHelper().loadGuild(args[2], event.getGuild(),null, null);
                        if (guild == null) {
                            event.getChannel().sendMessage("Could not find a guild with an ID of " + args[2]).queue();
                            return;
                        }
                    }
                    long power = 0;
                    for (long member : guild.getMembers())
                        power += main.getFileHelper().loadPlayer(event.getGuild().getMemberById(member)).getPower();
                    Member leader = event.getGuild().getMemberById(guild.getLeader());
                    event.getChannel().sendMessage(new EmbedBuilder().addField(guild.getName(), "GP: " + Long.toUnsignedString(guild.getPoints()) + "\nGP: " + Long.toUnsignedString(power) + "\nMembers: " + guild.getMembers().size() + "\nLeader: " + leader.getUser().getAsTag(), false).setColor(Color.GREEN).build()).queue();
                    break;
                case "leave":
                    PingPlayer player = main.getFileHelper().loadPlayer(event.getMember());
                    PlayerGuild guild1 = main.getFileHelper().loadGuild(player.getGuild(), event.getGuild(),null, null);
                    if (guild1.getLeader() == event.getAuthor().getIdLong()) {
                        event.getChannel().sendMessage("You cannot leave your own guild! You must disband it or promote someone else!").queue();
                        return;
                    }
                    player.setGuild(null);
                    event.getChannel().sendMessage("You have left your guild").queue();
                    break;
                case "promote":
                    if (args.length == 2)
                        event.getChannel().sendMessage("You have to specify a player to promote (ping them)").queue();
                    else {
                        Member promoted = event.getGuild().getMemberById(Long.parseLong(args[2].replace("<@", "").replace(">", "")));
                        PingPlayer promoter = main.getFileHelper().loadPlayer(event.getMember());
                        PlayerGuild guild2 = main.getFileHelper().loadGuild(promoter.getGuild(), event.getGuild(),null, null);
                        if (guild2.getLeader() == event.getAuthor().getIdLong())
                            if (promoted == null)
                                event.getChannel().sendMessage("Could not find a player with the mention " + args[2]).queue();
                            else if (main.getFileHelper().loadPlayer(promoted).getGuild().equals(promoter.getGuild())) {
                                guild2.promote(promoted.getUser().getIdLong());
                            } else
                                event.getChannel().sendMessage("That player is not in your guild!").queue();
                    }
                    break;
                case "disband":
                    PingPlayer pingPlayer = main.getFileHelper().loadPlayer(event.getMember());
                    PlayerGuild playerGuild = main.getFileHelper().loadGuild(pingPlayer.getGuild(), event.getGuild(),null, null);
                    if (playerGuild.getLeader() == event.getAuthor().getIdLong()) {
                        for (long member : playerGuild.getMembers())
                            main.getFileHelper().loadPlayer(event.getGuild().getMemberById(member)).setGuild(null);
                        main.getFileHelper().deleteGuild(playerGuild.getId());
                    }
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
