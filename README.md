# DiscordSRVVoid

A tiny Paper/Spigot plugin that suppresses selected players' Minecraft-to-DiscordSRV messages while leaving everyone else alone.

Useful for staff, alts, bots, or noisy players whose join/leave/death/advancement/chat messages should not be relayed to Discord.

## Requirements

- Java 17+
- Paper/Spigot 1.20+
- DiscordSRV 1.27+

## Install

1. Download the jar from the latest GitHub release.
2. Put it in your server's `plugins/` folder.
3. Restart the server.
4. Edit `plugins/DiscordSRVVoid/config.yml`.
5. Run `/discordsrvvoid reload` or restart again.

## Config

```yml
blocked-players:
  - "00000000-0000-0000-0000-000000000000"
  - "Notch"

blocked-events:
  chat: true
  death: true
  advancement: true
  join: true
  leave: true
```

`blocked-players` entries may be UUIDs or exact player names. UUIDs are preferred because names are tiny unreliable goblins.

## What gets blocked

- `chat`: DiscordSRV game chat relay messages
- `death`: DiscordSRV death messages
- `advancement`: DiscordSRV advancement/achievement messages
- `join`: DiscordSRV join messages
- `leave`: DiscordSRV leave messages

Join/leave suppression is implemented through DiscordSRV's `discordsrv.silentjoin` and `discordsrv.silentquit` permissions for configured players, so it targets DiscordSRV behavior instead of nuking normal server messages.

## Build

```bash
gradle build
```

The jar is written to `build/libs/DiscordSRVVoid-<version>.jar`.
