package com.buape.discordsrvvoid;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import java.util.List;

final class PlayerListEmbed {
    private static final int DESCRIPTION_LIMIT = 4096;
    // Reserve enough space for a truncation notice, even for very large server populations.
    private static final int NOTICE_RESERVE = 64;

    private PlayerListEmbed() {}

    static MessageEmbed build(List<String> names) {
        List<String> sorted = names.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
        StringBuilder description = new StringBuilder();
        int shown = 0;
        for (String name : sorted) {
            // Real account names only, not formatted display names. Escape underscores in Discord Markdown.
            String line = name.replace("_", "\\_") + "\n";
            if (description.length() + line.length() > DESCRIPTION_LIMIT - NOTICE_RESERVE) break;
            description.append(line);
            shown++;
        }
        if (sorted.isEmpty()) {
            description.append("No players are currently online.");
        } else if (shown < sorted.size()) {
            description.append("\n…and ").append(sorted.size() - shown).append(" more players.");
        }
        return new EmbedBuilder()
                .setTitle("Minecraft players online: " + sorted.size())
                .setColor(0x5865F2)
                .setDescription(description.toString().trim())
                .build();
    }
}
