package bigbade.pingwars;

import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.api.Generator;
import bigbade.pingwars.commands.*;
import bigbade.pingwars.generators.WeakGenerator;
import bigbade.pingwars.listeners.GuildJoinListener;
import bigbade.pingwars.listeners.MessageListener;
import bigbade.pingwars.storage.FlatFileHelper;
import bigbade.pingwars.upgrades.Upgrade;
import bigbade.pingwars.util.SimpleLogger;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.harawata.appdirs.AppDirsFactory;

import javax.security.auth.login.LoginException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PingWars {

    private String token;
    public String prefix;

    private int shardAmt = 1;

    public String filepath = AppDirsFactory.getInstance().getUserDataDir("PingBot", null, "Big_Bad_E");

    public List<JDA> shards = new ArrayList<>();
    public List<CommandBase> commands = new ArrayList<>();
    public List<Generator> generators = new ArrayList<>();
    public List<Upgrade> upgrades = new ArrayList<>();

    private FlatFileHelper fileHelper = new FlatFileHelper(this);

    public static final SimpleLogger LOGGER = new SimpleLogger();

    public static void main(String[] args) {
        PingWars main = new PingWars();
        main.load(args);
        main.start();
    }

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

    public void stop() {
        LOGGER.info("Stopping shards");
        for (JDA jda : shards)
            jda.shutdown();
        LOGGER.info("Saving cache");
        fileHelper.saveCache();
        LOGGER.info("Shutting down Java");
        System.exit(-1);
    }

    public FlatFileHelper getFileHelper() {
        return fileHelper;
    }

    private void registerCommands() {
        commands.add(new ConfigCommand(this));
        commands.add(new StopCommand(this));
        commands.add(new InfoCommand(this));
        commands.add(new BuyCommand(this));
        commands.add(new ClaimCommand(this));
        commands.add(new GuildCommand(this));
    }

    private void registerGenerators() {
        byte id = 0;
        generators.add(new WeakGenerator(id));
    }

    private void registerUpgrades() {

    }
}
