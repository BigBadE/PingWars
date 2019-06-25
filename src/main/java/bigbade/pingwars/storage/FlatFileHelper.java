package bigbade.pingwars.storage;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.util.ByteUtils;
import bigbade.pingwars.util.GuildConfig;
import bigbade.pingwars.util.PingPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlatFileHelper {
    private List<PingPlayer> cache = new ArrayList<>();
    private List<GuildConfig> guilds = new ArrayList<>();

    private ByteUtils utils = new ByteUtils();

    private PingWars main;

    public FlatFileHelper(PingWars main) {this.main = main; }

    @SuppressWarnings("SuspiciousMethodCalls")
    public PingPlayer loadPlayer(Member player) {
        for(PingPlayer pingPlayer : cache)
            if(pingPlayer.getMember().equals(player))
                return pingPlayer;
        if(cache.size() > 50)
            saveCache();
        Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + player.getGuild().getId() + "\\" + player.getUser().getId() + ".dat");
        if(Files.exists(path)) {
            byte[] bytes = null;
            try {
                bytes = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert bytes != null;
            PingPlayer pingPlayer = PingPlayer.loadPlayer(bytes, player, utils);
            cache.add(pingPlayer);
            return pingPlayer;
        }
        PingPlayer pingPlayer = new PingPlayer(player, 0, 0, 0, -1, System.currentTimeMillis(), new HashMap<>());
        cache.add(pingPlayer);
        return pingPlayer;
    }

    //We know it's a guild, so we use it to check guilds in GuildConfig#equals
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public GuildConfig loadGuild(Guild guild) {
        for(GuildConfig config : guilds) {
            if (config.equals(guild))
                return config;
        }
        if(guilds.size() > 50)
            saveCache();
        Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + guild.getId() + "\\config.dat");
        if(Files.exists(path)) {
            byte[] bytes = null;
            try {
                bytes = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert bytes != null;
            GuildConfig config = GuildConfig.load(bytes, utils, guild);
            guilds.add(config);
            return config;
        }
        GuildConfig config = new GuildConfig(null, null, guild);
        guilds.add(config);
        return config;
    }

    public void saveCache() {
        PingWars.LOGGER.info("Saving cache");
        for(PingPlayer pingPlayer : cache) {
            try {
                Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + pingPlayer.getMember().getGuild().getId() + "\\" + pingPlayer.getMember().getUser().getId() + ".dat");
                if(!Files.exists(path))
                    if(!path.toFile().createNewFile())
                        PingWars.LOGGER.error("Could not create player data file");
                Files.write(path, pingPlayer.save(utils));
            } catch (IOException e) {
                PingWars.LOGGER.error("Could not write data to save file", e);
            }
        }
        for(GuildConfig config : guilds) {
            try {
                Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + config.getGuild().getId() + "\\config.dat");
                if(!Files.exists(path))
                    if(!path.toFile().createNewFile())
                        PingWars.LOGGER.error("Could not create guild data file!");
                Files.write(path, config.save(utils));
            } catch (IOException e) {
                PingWars.LOGGER.error("Could not write guild data to save file", e);
            }
        }
    }
}
