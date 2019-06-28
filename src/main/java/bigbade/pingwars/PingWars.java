package bigbade.pingwars;

import bigbade.pingwars.api.Boss;
import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.Generator;
import bigbade.pingwars.commands.*;
import bigbade.pingwars.generators.*;
import bigbade.pingwars.listeners.GuildJoinListener;
import bigbade.pingwars.listeners.MessageListener;
import bigbade.pingwars.storage.FlatFileHelper;
import bigbade.pingwars.upgrades.Upgrade;
import bigbade.pingwars.upgrades.maxplayers.PlayerMaxOne;
import bigbade.pingwars.util.SimpleLogger;
import bigbade.pingwars.util.TimeUnit;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.harawata.appdirs.AppDirsFactory;

import javax.security.auth.login.LoginException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PingWars {

    //Token and prefix
    private String token;
    public String prefix;

    //Amount of shards
    private int shardAmt = 1;

    //Path to the data directory
    public String filepath = AppDirsFactory.getInstance().getUserDataDir("PingBot", null, "Big_Bad_E") + "\\data\\";

    //List of JDA instances
    private List<JDA> shards = new ArrayList<>();

    //All commands
    public List<CommandBase> commands = new ArrayList<>();
    //All generators
    public List<Generator> generators = new ArrayList<>();
    //All upgrades
    public List<Upgrade> upgrades = new ArrayList<>();

    //FileHelper to load players, guilds, and configs
    private FlatFileHelper fileHelper = new FlatFileHelper(this);

    //Map of war start times
    private Map<Guild, Long> timeMap = new HashMap<>();

    //Random for boss spawning
    private Random random = new Random();

    //My own logger instance because log4j wouldn't work
    public static final SimpleLogger LOGGER = new SimpleLogger();

    //Map of all bosses and their member
    private Map<Member, Boss> bosses = new HashMap<>();

    public static void main(String[] args) {
        PingWars main = new PingWars();
        main.load(args);
        main.start();
    }

    /**
     * Parses the args
     *
     * @param args program args
     */
    private void load(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--shards":
                    shardAmt = Integer.parseInt(args[i + 1]);
                    i++;
                    break;
                case "--token":
                    token = args[i + 1];
                    i++;
                    break;
                case "--prefix":
                    prefix = args[i + 1];
                    i++;
            }
        }
    }

    /**
     * Start the bot shards
     */
    private void start() {
        if (token == null) {
            LOGGER.info("No token is set!");
            throw new IllegalArgumentException();
        }
        if (prefix == null) {
            LOGGER.info("No prefix is set!");
            prefix = "!";
        }
        LOGGER.info("Setting up save directory");
        Path dataDir = FileSystems.getDefault().getPath(filepath);
        if (!Files.exists(dataDir))
            if (!dataDir.toFile().mkdirs() && !dataDir.toFile().mkdir())
                LOGGER.error("Could not create data directory!");
        LOGGER.info("Building JDA");
        JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(token);
        builder.addEventListener(new MessageListener(this));
        builder.addEventListener(new GuildJoinListener(filepath));
        LOGGER.info("Starting " + shardAmt + " shard(s)");
        for (int i = 0; i < shardAmt; i++)
            try {
                shards.add(builder.useSharding(i, shardAmt).build());
            } catch (LoginException e) {
                LOGGER.error("Could not reach the bot, make sure the token is correct!");
                System.exit(-1);
            }
        registerCommands();
        registerGenerators();
        registerUpgrades();
    }

    /**
     * Stop the bot
     */
    public void stop() {
        LOGGER.info("Stopping shards");
        for (JDA jda : shards)
            jda.shutdown();
        LOGGER.info("Saving cache");
        fileHelper.saveCache();
        LOGGER.info("Shutting down Java");
        System.exit(-1);
    }

    /**
     * File helper getter
     * @return file helper
     */
    public FlatFileHelper getFileHelper() {
        return fileHelper;
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        commands.add(new ConfigCommand(this));
        commands.add(new StopCommand(this));
        commands.add(new InfoCommand(this));
        commands.add(new BuyCommand(this));
        commands.add(new ClaimCommand(this));
        commands.add(new GuildCommand(this));
        commands.add(new HelpCommand(this));
        commands.add(new PrestigeCommand(this));
    }

    /**
     * Register generators
     */
    private void registerGenerators() {
        byte id = 0;
        generators.add(new TierOneGenerator(id));
        id++;
        generators.add(new TierTwoGenerator(id));
        id++;
        generators.add(new TierThreeGenerator(id));
        id++;
        generators.add(new TierFourGenerator(id));
        id++;
        generators.add(new TierFiveGenerator(id));
        id++;
        generators.add(new TierSixGenerator(id));
        id++;
        generators.add(new TierSevenGenerator(id));
        id++;
        generators.add(new TierEightGenerator(id));
        id++;
        generators.add(new TierNineGenerator(id));
        id++;
        generators.add(new TierTenGenerator(id));
    }

    /**
     * Check if a boss is spawned
     * @param guild
     * @return
     */
    public boolean checkBoss(Guild guild) {
        try {
            if (System.currentTimeMillis() - timeMap.get(guild) >= TimeUnit.MINUTE*5) {
                timeMap.replace(guild, System.currentTimeMillis());
                return random.nextInt(2) == 1;
            }
        } catch (NullPointerException e) {
            timeMap.put(guild, System.currentTimeMillis());
        }
        return false;
    }

    /**
     * Register upgrades
     */
    private void registerUpgrades() {
        byte id = 0;
        upgrades.add(new PlayerMaxOne(id));
    }

    /**
     * Get all bosses
     * @return bosses hashmap
     */
    public Map<Member, Boss> getBosses() {
        return bosses;
    }

    /**
     * Add a boss
     * @param boss member that is the boss
     * @param hp hp left
     * @param message the original message for editing later
     */
    public void addBoss(Member boss, long hp, Message message) {
        bosses.put(boss, new Boss(hp, message));
    }
}
