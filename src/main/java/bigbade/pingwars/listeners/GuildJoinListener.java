package bigbade.pingwars.listeners;

import bigbade.pingwars.PingWars;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class GuildJoinListener extends ListenerAdapter {
    private String filepath;

    public GuildJoinListener(String filepath) { this.filepath = filepath; }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            Files.createDirectory(FileSystems.getDefault().getPath(filepath+"\\data\\"+event.getGuild().getId()));
        } catch (IOException e) {
            PingWars.LOGGER.error("Could not create folder for guild " + event.getGuild().getName());
        }
    }
}
