package com.buape.discordsrvvoid;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.util.PlayerUtil;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/** DiscordSRV owns command registration and interaction acknowledgement. */
public final class PlayerListCommand implements SlashCommandProvider {
    private final JavaPlugin plugin;

    public PlayerListCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(new PluginSlashCommand(plugin,
                new CommandData("playerlist", "Show the players currently online on the Minecraft server")));
    }

    @SlashCommand(path = "playerlist", deferReply = true)
    public void onPlayerList(SlashCommandEvent event) {
        // JDA invokes this off-thread. Read Bukkit and vanish state only on the server thread.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            List<String> names = PlayerUtil.getOnlinePlayers(true).stream()
                    .map(Player::getName)
                    .toList();
            event.getHook().sendMessageEmbeds(PlayerListEmbed.build(names)).queue(
                    ignored -> {},
                    error -> plugin.getLogger().warning("Could not send /playerlist reply: " + error.getMessage()));
        });
    }
}
