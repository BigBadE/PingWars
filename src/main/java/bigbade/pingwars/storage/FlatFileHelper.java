package bigbade.pingwars.storage;

import bigbade.pingwars.PingWars;
import bigbade.pingwars.api.PlayerGuild;
import bigbade.pingwars.upgrades.Upgrade;
import bigbade.pingwars.util.ByteUtils;
import bigbade.pingwars.api.GuildConfig;
import bigbade.pingwars.api.PingPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FlatFileHelper {
    private List<PingPlayer> cache = new ArrayList<>();
    private List<GuildConfig> configs = new ArrayList<>();
    private List<PlayerGuild> guilds = new ArrayList<>();
    private ByteUtils utils = new ByteUtils();

    private PingWars main;

    private Random random = new Random();

    public FlatFileHelper(PingWars main) {
        this.main = main;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public PingPlayer loadPlayer(Member player) {
        for (PingPlayer pingPlayer : cache)
            if (pingPlayer.getMember().equals(player))
                return pingPlayer;
        if (cache.size() > 50)
            saveCache();
        Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + player.getGuild().getId() + "\\" + player.getUser().getId() + ".dat");
        if (Files.exists(path)) {
            byte[] bytes = null;
            try {
                bytes = Files.readAllBytes(path);
            } catch (IOException e) {
                PingWars.LOGGER.error("Could not read player file", e);
            }
            assert bytes != null;
            PingPlayer pingPlayer = PingPlayer.loadPlayer(bytes, player, utils);
            cache.add(pingPlayer);
            return pingPlayer;
        }
        PingPlayer pingPlayer = new PingPlayer(player, 0, 0, 0, null, System.currentTimeMillis(), new HashMap<>());
        cache.add(pingPlayer);
        return pingPlayer;
    }

    //We know it's a guild, so we use it to check guilds in GuildConfig#equals
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public GuildConfig loadGuildConfig(Guild guild) {
        for (GuildConfig config : configs) {
            if (config.equals(guild))
                return config;
        }
        if (configs.size() > 50)
            saveCache();
        Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + guild.getId() + "\\config.dat");
        configLoad:
        if (Files.exists(path)) {
            byte[] bytes = null;
            try {
                bytes = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert bytes != null;
            if (bytes.length == 0) break configLoad;
            GuildConfig config = GuildConfig.load(bytes, utils, guild);
            configs.add(config);
            return config;
        }
        GuildConfig config = new GuildConfig(null, null, guild);
        configs.add(config);
        return config;
    }

    public PlayerGuild loadGuild(String id, Guild guild, Member leader, String name) {
        if (id != null) {
            for (PlayerGuild pGuild : guilds) {
                if (pGuild.getId().equals(id))
                    return pGuild;
            }
            if (guilds.size() > 50)
                saveCache();
            Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + guild.getId() + "\\guilds\\" + id + ".dat");
            if (Files.exists(path)) {
                byte[] bytes = null;
                try {
                    bytes = Files.readAllBytes(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert bytes != null;
                PlayerGuild pGuild = PlayerGuild.load(bytes, utils, main, guild);
                guilds.add(pGuild);
                return pGuild;
            } else {
                return null;
            }
        }
        if (leader != null) {
            Map<Byte, Boolean> upgrades = new LinkedHashMap<>();
            for (Upgrade upgrade : main.upgrades)
                upgrades.put(upgrade.getId(), false);
            Set<Long> players = new HashSet<>();
            players.add(leader.getUser().getIdLong());
            Set<Long> elders = new HashSet<>();
            elders.add(leader.getUser().getIdLong());
            PlayerGuild pGuild = new PlayerGuild(random.nextLong(), leader.getUser().getIdLong(), 0, upgrades, null, players, elders, name, leader.getGuild());
            loadPlayer(leader).setGuild(pGuild.getId());
            guilds.add(pGuild);
            return pGuild;
        }
        return null;
    }

    public void saveCache() {
        PingWars.LOGGER.info("Saving cache");
        for (PingPlayer pingPlayer : cache) {
            try {
                Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + pingPlayer.getMember().getGuild().getId() + "\\" + pingPlayer.getMember().getUser().getId() + ".dat");
                if (!Files.exists(path)) {
                    if (!path.toFile().createNewFile())
                        PingWars.LOGGER.error("Could not create player data file");
                }
                Files.write(path, pingPlayer.save(utils));
            } catch (IOException e) {
                PingWars.LOGGER.error("Could not write data to save file", e);
            }
        }
        for (GuildConfig config : configs) {
            try {
                Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + config.getGuild().getId() + "\\config.dat");
                if (!Files.exists(path))
                    if (!path.toFile().createNewFile())
                        PingWars.LOGGER.error("Could not create config data file!");
                Files.write(path, config.save(utils));
            } catch (IOException e) {
                PingWars.LOGGER.error("Could not write config data to save file", e);
            }
        }
        for (PlayerGuild guild : guilds) {
            try {
                System.out.println("Saving " + guild);
                Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + guild.getGuild().getId() + "\\guilds\\" + guild.getId() + ".dat");
                if (!Files.exists(path))
                    if (!path.toFile().createNewFile())
                        PingWars.LOGGER.error("Could not create guild data file!");
                Files.write(path, guild.save(utils));
            } catch (IOException e) {
                PingWars.LOGGER.error("Could not write guild data to save file", e);
            }
        }
    }

    public void deleteGuild(String id, Guild guild) {
        PlayerGuild remove = null;
        for (PlayerGuild guild1 : guilds)
            if (guild1.getId().equals(id))
                remove = guild1;
        if (remove != null)
            guilds.remove(remove);
        Path path = FileSystems.getDefault().getPath(main.filepath + "\\data\\" + guild.getId() + "\\guilds\\" + id + ".dat");
        if (Files.exists(path))
            try {
                Files.delete(path);
            } catch (IOException e) {
                PingWars.LOGGER.error("Could not delete guild.", e);
            }
    }
}
