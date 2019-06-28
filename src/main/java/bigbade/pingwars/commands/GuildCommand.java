package bigbade.pingwars.commands;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.*;
import bigbade.pingwars.upgrades.Upgrade;
import bigbade.pingwars.util.TimeUnit;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class GuildCommand extends CommandBase {

    private String reason = null;

    public GuildCommand(PingWars main) {
        super("guild", new String[]{"guild", "g", "guilds"}, "Command for guilds", PermissionLevel.MEMBER, main);
    }

    @Override
    public void onCommand(MessageReceivedEvent event, String[] args) {
        if (args.length == 1)
            event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.GREEN).addField("Guild", "create (name): Create a guild.\njoin (id): Join a guild you were invited to.\nwartime: Get the time left in a war.\ninfo (guild): View guild stats.\nmembers (guild): View members, guild is not needed to see your own guild.\nleave: Leave your guild.\ninvite (name) (Elder only): Invite a player\nkick (name) (reason) (Elder only): Kick a guild member\nshop (Owner only): Buy guild upgrades.\ncolor (R) (G) (B) (Owner only): Set Guild tag color\nwar (ID) (Owner only): Start a guild war.\nendwar (Owner only): End the current war, with the opponent as the winner.\npromote (Owner only): Promote another member to Owner or Elder.\ndisband: Disband the guild.", false).build()).queue();
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
                            if (name.length() > 16)
                                event.getChannel().sendMessage("Guild names must be less than 17 characters long!").queue();
                            else {
                                main.getFileHelper().loadGuild(null, event.getGuild(), event.getMember(), name.toString());
                                event.getChannel().sendMessage("Successfully created guild").queue();
                            }
                        }
                    else
                        event.getChannel().sendMessage("You cannot create a guild if you are in a guild!").queue();
                    break;
                case "shop":
                    if (args.length == 2)
                        print(1, main.getFileHelper().loadGuild(null, event.getGuild(), event.getMember(), null), (int) Math.ceil(main.upgrades.size()/5));
                    else {
                        PlayerGuild guild = main.getFileHelper().loadGuild(null, event.getGuild(), event.getMember(), null);
                        try {
                            int page = Integer.parseInt(args[1]);
                            page = (int) Math.min(page, Math.ceil(main.upgrades.size() / 5));

                            event.getChannel().sendMessage(print(page, guild, (int) Math.ceil(main.upgrades.size()/5))).queue();
                        } catch (NumberFormatException e) {
                            if (guild.getLeader() == event.getAuthor().getIdLong()) {
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
                            } else
                                event.getChannel().sendMessage("You have to be owner to get upgrades!").queue();
                        }

                    }
                    break;
                case "war":
                    if (args.length == 2)
                        event.getChannel().sendMessage(main.prefix + "guild war (Guild ID)").queue();
                    else {
                        PlayerGuild guild = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                        if (guild != null)
                            if (guild.getLeader() == event.getAuthor().getIdLong())
                                if (guild.getLeader() == event.getAuthor().getIdLong()) {
                                    PlayerGuild enemy = main.getFileHelper().loadGuild(args[2], event.getGuild(), null, null);
                                    if (enemy != null) {
                                        guild.startWar(enemy.getId());
                                        enemy.startWar(guild.getId());
                                        event.getChannel().sendMessage(guild.getName() + " declared war on " + enemy.getName() + "!").queue();
                                    } else
                                        event.getChannel().sendMessage("Could not find a guild with that ID!").queue();
                                } else
                                    event.getChannel().sendMessage("You have to be a guild leader to start a war!").queue();
                            else
                                event.getChannel().sendMessage("You have to be owner to get upgrades!").queue();
                        else
                            event.getChannel().sendMessage("You have to be in a guild to declare war!").queue();

                    }
                    break;
                case "info":
                    PlayerGuild guild;
                    if (args.length == 2) {
                        guild = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                    } else {
                        guild = main.getFileHelper().loadGuild(args[2], event.getGuild(), null, null);
                        if (guild == null) {
                            event.getChannel().sendMessage("Could not find a guild with an ID of " + args[2]).queue();
                            return;
                        }
                    }
                    long power = 0;
                    for (long member : guild.getMembers())
                        power += main.getFileHelper().loadPlayer(event.getGuild().getMemberById(member)).getPower();
                    Member leader = event.getGuild().getMemberById(guild.getLeader());
                    event.getChannel().sendMessage(new EmbedBuilder().addField(guild.getName(), "ID: " + guild.getId() + "\nGP: " + Long.toUnsignedString(guild.getPoints()) + "\nPower: " + Long.toUnsignedString(power) + "\nMembers: " + guild.getMembers().size() + "\nLeader: " + leader.getUser().getAsTag(), false).setColor(Color.GREEN).build()).queue();
                    break;
                case "leave":
                    PingPlayer player = main.getFileHelper().loadPlayer(event.getMember());
                    PlayerGuild guild1 = main.getFileHelper().loadGuild(player.getGuild(), event.getGuild(), null, null);
                    if (guild1.getLeader() == event.getAuthor().getIdLong()) {
                        event.getChannel().sendMessage("You cannot leave your own guild! You must disband it or promote someone else!").queue();
                        return;
                    }
                    player.setGuild(null);
                    guild1.getMembers().remove(event.getAuthor().getIdLong());
                    event.getChannel().sendMessage("You have left your guild").queue();
                    break;
                case "promote":
                    if (args.length == 2)
                        event.getChannel().sendMessage("You have to specify a rank to promote to.").queue();
                    else if (args.length == 3)
                        event.getChannel().sendMessage("You have to specify a player to promote (Ping them)").queue();
                    else {
                        Member promoted = event.getGuild().getMemberById(Long.parseLong(args[2].replace("<@", "").replace(">", "")));
                        PingPlayer promoter = main.getFileHelper().loadPlayer(event.getMember());
                        PlayerGuild guild2 = main.getFileHelper().loadGuild(promoter.getGuild(), event.getGuild(), null, null);
                        if (guild2.getLeader() == event.getAuthor().getIdLong())
                            if (promoted == null)
                                event.getChannel().sendMessage("Could not find a player with the mention " + args[2]).queue();
                            else if (main.getFileHelper().loadPlayer(promoted).getGuild().equals(promoter.getGuild())) {
                                guild2.promote(promoted.getUser().getIdLong(), (args[2].equalsIgnoreCase("owner")) ? "e" : "o");
                            } else
                                event.getChannel().sendMessage("That player is not in your guild!").queue();
                    }
                    break;
                case "disband":
                    PingPlayer pingPlayer = main.getFileHelper().loadPlayer(event.getMember());
                    PlayerGuild playerGuild = main.getFileHelper().loadGuild(pingPlayer.getGuild(), event.getGuild(), null, null);
                    if (playerGuild == null) {
                        event.getChannel().sendMessage("Your save file was corrupted, it has been undone but please report what happened to Big_Bad_E.").queue();
                        pingPlayer.setGuild(null);
                    } else if (playerGuild.getLeader() == event.getAuthor().getIdLong()) {
                        if (playerGuild.getWar() != null)
                            main.getFileHelper().loadGuild(playerGuild.getWar(), event.getGuild(), null, null).endWar(true, event.getGuild(), main.getFileHelper());
                        for (long member : playerGuild.getMembers())
                            main.getFileHelper().loadPlayer(event.getGuild().getMemberById(member)).setGuild(null);
                        main.getFileHelper().deleteGuild(playerGuild.getId(), event.getGuild());
                        event.getChannel().sendMessage("Disbanded guild!").queue();
                    }
                    break;
                case "invite":
                    if (args.length == 2)
                        event.getChannel().sendMessage("You must tag a player to invite them!").queue();
                    else {
                        Member tagged = event.getGuild().getMemberById(args[2].replace("<@", "").replace(">", ""));
                        if (tagged == null)
                            event.getChannel().sendMessage("Could not find player " + args[2]).queue();
                        else {
                            PlayerGuild guild2 = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                            if(guild2.isElder(event.getMember()) || guild2.getLeader() == event.getAuthor().getIdLong()) {
                                if (guild2.getMembers().size() == guild2.maxPlayers()) {
                                    event.getChannel().sendMessage("You have too many people in your guild to invite more!").queue();
                                    return;
                                }
                                PingPlayer pingPlayer1 = main.getFileHelper().loadPlayer(tagged);
                                if (pingPlayer1.getGuild() == null) {
                                    guild2.invite(tagged);
                                    tagged.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(event.getMember().getEffectiveName() + " has invited you to join the guild " + guild2.getName() + ".\nType \"" + main.prefix + "guild join " + guild2.getId() + "\" in the server " + event.getGuild().getName() + " to accept the invite.\nTHIS INVITE IS TEMPORARY, IT MAY EXPIRE.").queue()));
                                    event.getChannel().sendMessage("Invited player!").queue();
                                } else {
                                    event.getChannel().sendMessage("The player is already in a guild!").queue();
                                }
                            } else
                                event.getChannel().sendMessage("Only elders or owner can send invites!").queue();
                        }
                    }
                    break;
                case "join":
                    if (args.length == 2)
                        event.getChannel().sendMessage("You must include a guild ID!\nUsage: " + main.prefix + "guild join (id)").queue();
                    else {
                        PlayerGuild guild2 = main.getFileHelper().loadGuild(args[2], event.getGuild(), null, null);
                        if (guild2 != null)
                            if (guild2.invited(event.getMember())) {
                                if (guild2.getMembers().size() < guild2.maxPlayers()) {
                                    PingPlayer player1 = main.getFileHelper().loadPlayer(event.getMember());
                                    if (player1.getGuild() == null) {
                                        player1.setGuild(guild2.getId());
                                        guild2.addMember(event.getMember().getUser().getIdLong());
                                        guild2.removeInvite(event.getMember());
                                        if (guild2.hasUpgrade((byte) 3))
                                            event.getGuild().getController().addSingleRoleToMember(event.getMember(), event.getGuild().getRolesByName(guild2.getName(), false).get(0)).queue();
                                        event.getChannel().sendMessage("You have joined " + guild2.getName() + "!").queue();
                                    } else {
                                        event.getChannel().sendMessage("You are already in a guild!").queue();
                                    }
                                } else
                                    event.getChannel().sendMessage(guild2.getName() + " has too many members!").queue();
                            } else {
                                event.getChannel().sendMessage("You are not invited to the guild " + guild2.getName()).queue();
                            }
                        else
                            event.getChannel().sendMessage("That guild does not exist!").queue();
                    }
                    break;
                case "members":
                    PlayerGuild guild2 = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                    StringBuilder members = new StringBuilder();
                    for (long member : guild2.getMembers()) {
                        Member member1 = event.getGuild().getMemberById(member);
                        members.append(member1.getUser().getAsTag()).append(" : ").append(member1.getEffectiveName()).append("\n");
                    }
                    event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.GREEN)
                            .addField(guild2.getName(), members.toString(), false).build()).queue();
                    break;
                case "kick":
                    if (args.length <= 2)
                        event.getChannel().sendMessage("You have to specify who you are kicking (ping them)").queue();
                    else {
                        PlayerGuild guild3 = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                        if (guild3.isElder(event.getMember()) || guild3.getLeader() == event.getAuthor().getIdLong()) {
                            Member target = event.getGuild().getMemberById(args[2].replace("<@", "").replace(">", ""));
                            if (target != null) {
                                PingPlayer player1 = main.getFileHelper().loadPlayer(target);
                                if (player1.getGuild().equals(guild3.getId())) {
                                    if (args.length >= 4) {
                                        StringBuilder reasonBuilder = new StringBuilder(args[3] + " ");
                                        for (int i = 4; i < args.length; i++)
                                            reasonBuilder.append(args[i]).append(" ");
                                        reasonBuilder.deleteCharAt(reasonBuilder.length() - 1);
                                        reason = reasonBuilder.toString();
                                    }
                                    player1.setGuild(null);
                                    guild3.getMembers().remove(target.getUser().getIdLong());
                                    target.getUser().openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage("You have been kicked from " + guild3.getName() + ".\n Reason: " + reason).queue()));
                                } else
                                    event.getChannel().sendMessage("You cannot kick someone that is not in your guild!").queue();
                            } else
                                event.getChannel().sendMessage("Could not find a player with that tag!").queue();
                        } else
                            event.getChannel().sendMessage("You have to be elder to kick people!").queue();
                    }
                    break;
                case "color":
                    if (args.length == 5) {
                        PlayerGuild guild3 = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                        if (guild3.getLeader() == event.getMember().getUser().getIdLong()) {
                            event.getGuild().getRolesByName(guild3.getName(), false).get(0).getManager().setColor(formatRGB(args[2], args[3], args[4])).queue();
                        } else {
                            event.getChannel().sendMessage("You have to be owner to change the guild color!").queue();
                        }
                    } else
                        event.getChannel().sendMessage("You have to specify the RGB!").queue();
                case "wartime":
                    PlayerGuild guild3 = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                    if(guild3.checkWar()) {
                        boolean won = guild3.warScore() > guild3.warScore();
                        guild3.endWar(won, event.getGuild(), main.getFileHelper());
                    } else
                    event.getChannel().sendMessage("Time left: " + Math.floor(guild3.getWarTime()/ TimeUnit.MINUTE) + " minutes and " + Math.floor(guild3.getWarTime()/TimeUnit.SECOND) + " seconds").queue();
                case "warend":
                    PlayerGuild guild4 = main.getFileHelper().loadGuild(main.getFileHelper().loadPlayer(event.getMember()).getGuild(), event.getGuild(), null, null);
                    if(guild4.checkWar()) {
                        boolean won = guild4.warScore() > guild4.warScore();
                        guild4.endWar(won, event.getGuild(), main.getFileHelper());
                    } else {
                        guild4.endWar(false, event.getGuild(), main.getFileHelper());
                        event.getChannel().sendMessage("You have ended the war!").queue();
                    }
            }
        }

    }

    private Integer formatRGB(String r, String g, String b) {
        return Integer.parseInt(("000" + r).substring(r.length()) + ("000" + b).substring(b.length()) + ("000" + g).substring(g.length()));
    }

    private MessageEmbed print(int page, PlayerGuild guild, int maxPage) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.addField("Use " + main.prefix + "buy (name) to buy a generator.", "", false);
        for (int i = page*5; i < Math.min(page * 5 + 5, main.generators.size()); i++) {
            Generator generator = main.generators.get(i);
            boolean owned;
            owned = guild.getUpgrades().get(generator.getId());
            builder.addField(generator.getName(), generator.getDescription() + "\nPrice: " + generator.getPrice() + " Bought: " + ((owned) ? "Yes" : "No"), false);
            builder.setFooter("Page " + (page+1) + "/" + (maxPage+1), null);
        }
        return builder.build();
    }
}