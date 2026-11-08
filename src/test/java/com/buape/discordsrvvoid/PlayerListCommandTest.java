package com.buape.discordsrvvoid;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.InteractionHook;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.WebhookMessageAction;
import github.scarsz.discordsrv.util.PlayerUtil;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class PlayerListCommandTest {
    @Test
    void registersExpectedCommandAndDefersPublicReply() throws Exception {
        var provider = new PlayerListCommand(mock(JavaPlugin.class));
        var commands = provider.getSlashCommands();
        assertEquals(1, commands.size());
        assertEquals("playerlist", commands.iterator().next().getCommandData().getName());
        var annotation = PlayerListCommand.class.getMethod("onPlayerList", SlashCommandEvent.class)
                .getAnnotation(SlashCommand.class);
        assertEquals("playerlist", annotation.path());
        assertTrue(annotation.deferReply());
        assertFalse(annotation.deferEphemeral());
    }

    @Test
    @SuppressWarnings("unchecked")
    void readsVisiblePlayersOnlyInsideScheduledServerTask() {
        var plugin = mock(JavaPlugin.class);
        var server = mock(Server.class);
        var scheduler = mock(BukkitScheduler.class);
        var event = mock(SlashCommandEvent.class);
        var hook = mock(InteractionHook.class);
        var action = mock(WebhookMessageAction.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        when(event.getHook()).thenReturn(hook);
        when(hook.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(action);
        var player = mock(Player.class);
        when(player.getName()).thenReturn("Steve");

        try (var players = mockStatic(PlayerUtil.class)) {
            players.when(() -> PlayerUtil.getOnlinePlayers(true)).thenReturn(List.of(player));
            new PlayerListCommand(plugin).onPlayerList(event);
            players.verifyNoInteractions();
            verifyNoInteractions(hook);
            var task = ArgumentCaptor.forClass(Runnable.class);
            verify(scheduler).runTask(eq(plugin), task.capture());
            task.getValue().run();
            players.verify(() -> PlayerUtil.getOnlinePlayers(true));
            var embed = ArgumentCaptor.forClass(MessageEmbed.class);
            verify(hook).sendMessageEmbeds(embed.capture());
            assertEquals("Steve", embed.getValue().getDescription());
            verify(action).queue(any(), any());
        }
    }
}
