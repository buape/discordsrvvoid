package com.buape.discordsrvvoid;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class PlayerListEmbedTest {
    @Test
    void emptyServerHasHelpfulMessage() {
        var embed = PlayerListEmbed.build(List.of());
        assertEquals("Minecraft players online: 0", embed.getTitle());
        assertEquals("No players are currently online.", embed.getDescription());
    }

    @Test
    void sortsNamesAndEscapesUnderscores() {
        var embed = PlayerListEmbed.build(List.of("Zebra", "alice_123", "Bob"));
        assertEquals("Minecraft players online: 3", embed.getTitle());
        assertEquals("alice\\_123\nBob\nZebra", embed.getDescription());
    }

    @Test
    void largeListFitsDiscordLimitsAndReportsOmittedPlayers() {
        var names = IntStream.range(0, 1000).mapToObj(i -> "Player___" + String.format("%04d", i)).toList();
        var embed = PlayerListEmbed.build(names);
        assertEquals("Minecraft players online: 1000", embed.getTitle());
        assertTrue(embed.getDescription().length() <= 4096);
        assertTrue(embed.getLength() <= 6000);
        long shown = embed.getDescription().lines().filter(line -> line.startsWith("Player")).count();
        assertTrue(shown > 0 && shown < 1000);
        assertTrue(embed.getDescription().endsWith("…and " + (1000 - shown) + " more players."));
    }

    @Test
    void singlePlayerIsListedWithoutTruncation() {
        var embed = PlayerListEmbed.build(List.of("Steve"));
        assertEquals("Minecraft players online: 1", embed.getTitle());
        assertEquals("Steve", embed.getDescription());
    }
}
