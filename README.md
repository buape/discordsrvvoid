# DiscordSRVVoid

A tiny Paper/Spigot plugin that suppresses selected players' Minecraft-to-DiscordSRV messages while leaving everyone else alone, and provides a Discord `/playerlist` command.

Useful for staff, alts, bots, or noisy players whose join/leave/death/advancement/chat messages should not be relayed to Discord.

## What this plugin does

DiscordSRVVoid lets you configure a list of Minecraft players whose messages should **not** be relayed by DiscordSRV.

It can suppress these DiscordSRV relay types for configured players:

- Minecraft chat messages
- death messages
- advancement/achievement messages
- join messages
- leave messages

It does **not** mute the player in Minecraft. It only stops DiscordSRV from sending the selected event messages to Discord.

## Requirements

- Java 17+
- Paper/Spigot 1.20+
- DiscordSRV 1.27+

DiscordSRV must already be installed and working before this plugin can do anything useful. If DiscordSRV is broken, fix that first; do not blame the tiny filter goblin.

## Download

Download the latest jar from GitHub Releases:

https://github.com/buape/discordsrvvoid/releases/latest

The file will be named something like:

```text
DiscordSRVVoid-1.0.0.jar
```

## Install

1. Stop the Minecraft server.
2. Make sure `DiscordSRV.jar` is already in the server's `plugins/` folder.
3. Download `DiscordSRVVoid-<version>.jar` from the latest release.
4. Put the jar in the server's `plugins/` folder.
5. Start the server.
6. Confirm this folder was created:

   ```text
   plugins/DiscordSRVVoid/
   ```

7. Open:

   ```text
   plugins/DiscordSRVVoid/config.yml
   ```

8. Configure the players and message types to suppress.
9. Save the file.
10. Run this in the Minecraft console or in-game as an operator:

    ```text
    /discordsrvvoid reload
    ```

    Or restart the server if clicking reload buttons scares you, which is fair honestly.

## Config

Default config:

```yml
# Add UUIDs or exact player names here. UUIDs are preferred.
blocked-players:
  - "00000000-0000-0000-0000-000000000000"

# Toggle which Minecraft -> DiscordSRV message types are suppressed for blocked players.
blocked-events:
  chat: true
  death: true
  advancement: true
  join: true
  leave: true

# Log why a message was suppressed.
debug: false

# DO NOT MANUALLY CHANGE THIS.
config-version: 1
```

### `blocked-players`

List the players to filter.

Preferred: Minecraft UUIDs.

```yml
blocked-players:
  - "069a79f4-44e9-4726-a5be-fca90e38aaf5f"
  - "853c80ef-3c37-49fd-aa49-938b674adae6"
```

Exact player names also work:

```yml
blocked-players:
  - "Notch"
  - "jeb_"
```

UUIDs are better because players can change names. Names are supported because sometimes you need the easy button and nobody should have to suffer through UUID lookup at 2 AM.

### `blocked-events`

Choose which event types should be hidden for blocked players.

```yml
blocked-events:
  chat: true
  death: true
  advancement: true
  join: true
  leave: true
```

Set an event to `false` if you still want DiscordSRV to relay that message type.

Example: hide joins/leaves/deaths, but allow chat:

```yml
blocked-events:
  chat: false
  death: true
  advancement: true
  join: true
  leave: true
```

## Example setup

To stop one player from producing DiscordSRV join, leave, death, advancement, and chat relay messages:

```yml
blocked-players:
  - "069a79f4-44e9-4726-a5be-fca90e38aaf5f"

blocked-events:
  chat: true
  death: true
  advancement: true
  join: true
  leave: true

debug: false
config-version: 1
```

Then run:

```text
/discordsrvvoid reload
```

## Commands

### `/playerlist` (Discord)

Shows a public embed with the current online player count and alphabetically sorted Minecraft account names. An empty server shows “No players are currently online.” Long lists are truncated to Discord's embed limit with the remaining count shown.

Players hidden by DiscordSRV's supported vanish integrations are excluded from both the names and count. `blocked-players` only controls message relaying, so those players still appear unless vanished.

The command is registered through DiscordSRV in the Discord guilds its bot belongs to. No separate bot or token is needed. Restart the Minecraft server after installing the updated jar. The bot must have been invited with the `applications.commands` scope, and users need Discord's **Use Application Commands** permission in the channel. Server admins can restrict `/playerlist` through Discord's integration command permissions.

### `/discordsrvvoid reload`

Reloads `config.yml` without restarting the server.

Permission:

```text
discordsrvvoid.reload
```

Default: server operators only.

## How join/leave suppression works

DiscordSRV already supports silent join/quit permissions:

- `discordsrv.silentjoin`
- `discordsrv.silentquit`

DiscordSRVVoid temporarily applies those permissions to configured online players when needed. This targets DiscordSRV's relay behavior instead of deleting normal Minecraft join/leave behavior with a hammer like a cave admin.

## Troubleshooting

### The plugin does not load

Check:

- Java is version 17 or newer.
- DiscordSRV is installed.
- DiscordSRVVoid is in the same `plugins/` folder.
- The server log does not show dependency errors.

### Messages are still showing in Discord

Check:

1. The player is listed in `blocked-players`.
2. The UUID is correct, or the name exactly matches the Minecraft name.
3. The relevant `blocked-events` value is `true`.
4. You ran `/discordsrvvoid reload` after editing config.
5. DiscordSRV is the thing sending the message. If another plugin sends Discord messages directly, DiscordSRVVoid cannot stop that, because it is not a wizard with a net.

### Join/leave messages still show after adding a player

Run `/discordsrvvoid reload`, then have the player rejoin once. The plugin applies DiscordSRV silent permissions to online configured players on reload and join/quit handling.

### I want proof the plugin is doing something

Set:

```yml
debug: true
```

Then reload and watch the server console. Turn it back off after testing unless you enjoy log confetti.

## Build

```bash
gradle build
```

The build also runs offline tests for command registration, deferred replies, server-thread player reads, vanished-player filtering delegation, empty lists, sorting, and embed limits. These tests do not require a bot token or mutate a Minecraft server.

The jar is written to:

```text
build/libs/DiscordSRVVoid-<version>.jar
```

## Release process

GitHub Actions builds the plugin on pushes and PRs.

When a GitHub release is published, CI builds the jar and uploads it to the release automatically.
