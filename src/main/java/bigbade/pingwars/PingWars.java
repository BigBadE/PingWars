package bigbade.pingwars;

import bigbade.pingwars.api.CommandBase;
import bigbade.pingwars.commands.ConfigCommand;
import bigbade.pingwars.commands.StopCommand;
import bigbade.pingwars.listeners.MessageListener;
import bigbade.pingwars.storage.FlatFileHelper;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.harawata.appdirs.AppDirsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
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

    private FlatFileHelper fileHelper = new FlatFileHelper(this);

    public static final Logger LOGGER = LoggerFactory.getLogger(PingWars.class);

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
            LOGGER.error("No token is set!");
            throw new IllegalArgumentException();
        }
        if (prefix == null) {
            LOGGER.info("No prefix is set!");
            prefix = "!";
        }
        LOGGER.info("Setting up save dir");
        Path dataDir = FileSystems.getDefault().getPath(filepath);
        if(!Files.exists(dataDir))
            if(!dataDir.toFile().mkdir())
                LOGGER.error("Could not create data directory!");
        LOGGER.info("Building JDA");
        JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(token);
        builder.addEventListener(new MessageListener(this));
        LOGGER.info("Starting " + shardAmt + " shards");
        for (int i = 0; i < shardAmt; i++)
            try {
                shards.add(builder.useSharding(i, shardAmt).build());
            } catch (LoginException e) {
                LOGGER.error("Could not reach the bot, make sure the token is correct!");
                System.exit(-1);
            }
        registerCommands();
    }

    public void stop() {
        LOGGER.info("Stopping shards");
        for(JDA jda : shards)
            jda.shutdown();
        LOGGER.info("Saving cache");
        fileHelper.saveCache();
        LOGGER.info("Shutting down Java");
        System.exit(-1);
    }

    public FlatFileHelper getFileHelper() {
        return fileHelper;
    }

    public void registerCommands() {
        commands.add(new ConfigCommand(this));
        commands.add(new StopCommand(this));
    }
}
