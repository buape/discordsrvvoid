package com.buape.discordsrvvoid;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AchievementMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.DeathMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordSRVVoid extends JavaPlugin implements Listener {
    private final Set<UUID> blockedUuids = new HashSet<>();
    private final Set<String> blockedNames = new HashSet<>();
    private final Set<UUID> silentPermissionAttachments = new HashSet<>();

    private boolean blockChat;
    private boolean blockDeath;
    private boolean blockAdvancement;
    private boolean blockJoin;
    private boolean blockLeave;
    private boolean debug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();

        getServer().getPluginManager().registerEvents(this, this);
        DiscordSRV.api.subscribe(this);

        getLogger().info("DiscordSRVVoid enabled.");
    }

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(this);
        silentPermissionAttachments.clear();
        getLogger().info("DiscordSRVVoid disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadSettings();
            sender.sendMessage("DiscordSRVVoid config reloaded.");
            return true;
        }

        sender.sendMessage("Usage: /discordsrvvoid reload");
        return true;
    }

    private void loadSettings() {
        blockedUuids.clear();
        blockedNames.clear();

        List<String> players = getConfig().getStringList("blocked-players");
        for (String value : players) {
            String trimmed = value.trim();
            if (trimmed.isEmpty()) continue;

            try {
                blockedUuids.add(UUID.fromString(trimmed));
            } catch (IllegalArgumentException ignored) {
                blockedNames.add(trimmed.toLowerCase(Locale.ROOT));
            }
        }

        blockChat = getConfig().getBoolean("blocked-events.chat", true);
        blockDeath = getConfig().getBoolean("blocked-events.death", true);
        blockAdvancement = getConfig().getBoolean("blocked-events.advancement", true);
        blockJoin = getConfig().getBoolean("blocked-events.join", true);
        blockLeave = getConfig().getBoolean("blocked-events.leave", true);
        debug = getConfig().getBoolean("debug", false);

        for (Player player : getServer().getOnlinePlayers()) {
            applyDiscordSrvSilentPermissions(player);
        }
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onGameChat(GameChatMessagePreProcessEvent event) {
        if (!blockChat || !isBlocked(event.getPlayer())) return;
        event.setCancelled(true);
        debug("Suppressed chat relay for " + event.getPlayer().getName());
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onDeathMessage(DeathMessagePreProcessEvent event) {
        if (!blockDeath || !isBlocked(event.getPlayer())) return;
        event.setCancelled(true);
        debug("Suppressed death relay for " + event.getPlayer().getName());
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onAchievementMessage(AchievementMessagePreProcessEvent event) {
        if (!blockAdvancement || !isBlocked(event.getPlayer())) return;
        event.setCancelled(true);
        debug("Suppressed advancement relay for " + event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        applyDiscordSrvSilentPermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        applyDiscordSrvSilentPermissions(event.getPlayer());
    }

    private boolean isBlocked(Player player) {
        if (player == null) return false;
        if (blockedUuids.contains(player.getUniqueId())) return true;
        return blockedNames.contains(player.getName().toLowerCase(Locale.ROOT));
    }

    private void applyDiscordSrvSilentPermissions(Player player) {
        if (!isBlocked(player)) return;

        UUID playerId = player.getUniqueId();
        if (silentPermissionAttachments.contains(playerId)) return;

        PermissionAttachment attachment = player.addAttachment(this);
        if (blockJoin) attachment.setPermission("discordsrv.silentjoin", true);
        if (blockLeave) attachment.setPermission("discordsrv.silentquit", true);
        silentPermissionAttachments.add(playerId);

        debug("Applied DiscordSRV silent permissions for " + player.getName());
    }

    private void debug(String message) {
        if (debug) {
            getLogger().info(message);
        }
    }
}
