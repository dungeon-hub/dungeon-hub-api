# Ticket panel permissions

Since 0.8.0, ticket panel permissions use types owned by the API instead of Kord's `Permissions`. This came out of #70 and was implemented in #81.

This document covers:
- the model in `model`;
- its JSON format;
- how its defaults are filled in;
- how the server stores it;
- how the bot applies it.

The consumer-side code lives in dungeon-hub-server#42, dungeon-hub-application#82 and dungeon-hub-dashboard#43.

## What it replaced

Before 0.8.0, `TicketPanelModel.permissions` was a `Map<TicketPermissionCandidate, Map<TicketPermissionType, Permissions>>`:

- `TicketPermissionType` was `Allowed` / `Denied`, and the values were Kord bit sets.
- The server stored them in ten `*_permissions` columns.
- These maps only controlled the Discord channel overwrites. Every check for who could close, claim or delete a ticket was hard-coded in the bot.
- There was no difference between open and closed tickets, apart from a hard-coded "the creator gets no overwrite while the ticket is closed".

The new model covers both the channel overwrites and the ticket actions, separately for open and closed tickets. `TicketPanelPermissionsModel.DEFAULT` reproduces the old behaviour exactly.

## Building blocks

All of these are in `net.dungeonhub.enums`.

### `TicketPhase`

There are two phases. A ticket's phase comes from its state via `TicketPhase.of(state)`:

| `TicketState` | `TicketPhase` |
|---|---|
| `Creating`, `Open` | `Open` |
| `Closed`, `Deleted` | `Closed` |

A third "claimed" phase was considered and rejected, because tickets wouldn't work as intended with it. A claimed ticket is still an open ticket: the claimer is a candidate, and the "support team is hidden while claimed" rule lives in the bot (see [Channel overwrites](#channel-overwrites-in-the-bot)).

### `TicketPermissionCandidate`

The groups a member can belong to for a ticket. The model doesn't know who belongs to which candidate; the bot works that out (`Member.getTicketPermissionCandidates`):

| Candidate | A member matches it if … |
|---|---|
| `SupportTeam` | they have one of the panel's `supportRoles` |
| `AdditionalRoles` | they have one of the panel's `additionalRoles` |
| `TicketCreator` | they are `ticket.user` |
| `TicketClaimer` | they are `ticket.claimer` |
| `Everyone` | always |

A member usually matches several candidates at once. For example, a supporter who claimed a ticket is `Everyone`, `SupportTeam` and `TicketClaimer`.

### `TicketAction`

The actions the bot checks. Each action has the `phases` it applies to:

| Action | Phases | Checked by (bot) |
|---|---|---|
| `Reopen` | Closed | open button |
| `Close` | Open | close button, `/ticket close` (only if the panel is `closeable`) |
| `Claim` | Open | claim button, `/ticket claim` while the ticket has no claimer |
| `Unclaim` | Open | claim button, `/ticket claim` while the ticket has a claimer |
| `Delete` | Open, Closed | delete button. Also the close button and `/ticket close` on panels that aren't `closeable` |
| `AddUser` | Open | `/ticket add` |
| `GenerateTranscript` | Open, Closed | transcript button |

`TicketAction.forPhase(phase)` returns the actions of a phase, in enum order.

### `DiscordChannelPermission`

The Discord permissions that can be overwritten on a ticket channel. Each one carries its Discord permission bit:

| Entry | `bit` |
|---|---|
| `ViewChannel` | `1 shl 10` |
| `SendMessages` | `1 shl 11` |
| `ManageMessages` | `1 shl 13` |
| `EmbedLinks` | `1 shl 14` |
| `AttachFiles` | `1 shl 15` |
| `ReadMessageHistory` | `1 shl 16` |
| `MentionEveryone` | `1 shl 17` |

The model doesn't use `bit` itself. It's there so that a consumer without Kord can still build overwrites. The bot maps each entry to Kord's `Permission` with an exhaustive `when` (`toKordPermission`), and a test checks that both agree on the bit.

### Ordinals are part of the storage format

The server stores `TicketPhase`, `TicketPermissionCandidate`, `TicketAction` and `DiscordChannelPermission` by **ordinal**, like the other enums in its schema. So:

- existing entries must never be reordered or removed;
- new entries go at the end.

Each of the four enums has a comment saying this.

## The model

```kotlin
data class TicketPanelPermissionsModel(
    val open: Map<TicketPermissionCandidate, TicketCandidatePermissionsModel> = emptyMap(),
    val closed: Map<TicketPermissionCandidate, TicketCandidatePermissionsModel> = emptyMap()
)

data class TicketCandidatePermissionsModel(
    val actions: Map<TicketAction, Boolean> = emptyMap(),
    val channel: Map<DiscordChannelPermission, Boolean> = emptyMap()
)
```

So the structure is phase → candidate → `actions` / `channel`. `forPhase(phase)` returns `open` or `closed`.

### What the values mean

**`actions[action]`** says whether this candidate grants the action.
- `true` grants it.
- `false` only means "this candidate doesn't grant it". It isn't a deny. If another candidate of the same member grants the action, the member may perform it (see [`isAllowed`](#checking-an-action-isallowed)).
- A missing key falls back to `DEFAULT`.

**`channel[permission]`** is the Discord overwrite for this candidate.
- `true` allows the permission.
- `false` denies it.
- A missing key means the permission isn't overwritten, so it's inherited from the category and server.

### Actions and channel permissions fall back differently

| What's missing | Actions | Channel permissions |
|---|---|---|
| The whole phase or the whole candidate | `DEFAULT` for that candidate | `DEFAULT` for that candidate |
| A single key of a candidate that is given | `DEFAULT` for that action | **not overwritten** (no `DEFAULT`) |

Actions are filled in one by one. The channel map of a candidate that is given is used as it is. `{}` therefore means "no overwrite at all" for that candidate, and it's the only way to remove an overwrite that the defaults would add. Merging channel defaults key by key would make it impossible to drop a default key like `ReadMessageHistory`.

Example: with `"open": { "TicketCreator": { "channel": { "AttachFiles": true } } }`, the creator gets **only** `AttachFiles` allowed while the ticket is open. They don't also get the default View/Send/History. Their actions are still all the defaults.

## JSON format

The keys are the enum names. Moshi (clients) and Gson (server) write exactly the same JSON:

```json
{
  "open": {
    "SupportTeam": {
      "actions": { "Close": true, "Claim": true, "Unclaim": false, "Delete": true, "AddUser": true, "GenerateTranscript": true },
      "channel": { "ViewChannel": true, "SendMessages": true, "ReadMessageHistory": true }
    },
    "Everyone": {
      "actions": { "Close": false, "Claim": false, "Unclaim": false, "Delete": false, "AddUser": false, "GenerateTranscript": false },
      "channel": { "ViewChannel": false }
    }
  },
  "closed": {}
}
```

Behaviour of the two parsers:

- **Missing fields:** both Moshi and Gson use the Kotlin defaults (`emptyMap()`). Every constructor parameter has a default, so Kotlin generates a no-arg constructor, and Gson uses it.
- **Null map values** are dropped when writing.
- **Unknown keys, Moshi:** an unknown candidate, action or channel permission fails in the parser with a `JsonDataException`. On clients, ktor wraps that in a `JsonConvertException`, and `dhApiRequest` returns `null`. Any new enum entry is therefore **a breaking change for old clients** that receive it, like the bot or the mod (see dungeon-hub-carry-helper#6).
- **Unknown keys and nulls, Gson:** Gson ignores Kotlin's nullability.
  - An unknown enum key becomes a `null` key.
  - `"open": null`, `"actions": null` or `"Close": null` arrive as real `null`s, even though the types are non-null.

  `resolved()` deals with this: its private helpers take nullable types on purpose, and it throws `InvalidUpdateException` for these cases (see the next section). The server turns that into a 400.

## Filling in the defaults: `resolved()`

`resolved()` validates a (possibly partial) model and returns a complete one. The server calls it on every write and on every read.

For each phase:

1. A `null` phase map counts as empty. A `null` candidate key throws `InvalidUpdateException` ("Unknown ticket permission candidate").
2. For **every** `TicketPermissionCandidate`:
   - **Candidate not given, or `null`:** take `DEFAULT` for that phase and candidate as it is.
   - **Candidate given:**
     - **actions:**
       - A `null` action key throws (unknown action).
       - A `null` value throws ("can't be null").
       - Then the result holds exactly `TicketAction.forPhase(phase)`: the given value if there is one, otherwise the `DEFAULT` value.
       - Actions that don't apply to the phase (e.g. `Reopen` under `open`) are **dropped silently**.
     - **channel:**
       - A `null` permission key throws (unknown channel permission).
       - A `null` value is treated like a missing key.
       - The remaining entries are kept as given, sorted in enum order.
       - Nothing is filled in from `DEFAULT`.

What a resolved model looks like:

- Both phases contain every candidate.
- Every candidate has exactly the actions of its phase.
- The channel maps are in enum order.
- `resolved()` is idempotent, and `DEFAULT.resolved() == DEFAULT`; `testDefaultsAreComplete` checks this.

Unknown keys are rejected on purpose instead of being ignored. Otherwise a typo in a dashboard import would silently fall back to the defaults.

## Checking an action: `isAllowed`

```kotlin
fun isAllowed(phase: TicketPhase, candidates: Set<TicketPermissionCandidate>, action: TicketAction): Boolean
```

1. If `phase !in action.phases`, the result is `false`, whatever is configured. For example, `Reopen` is never allowed on an open ticket.
2. Otherwise the result is `true` if **any** of the given candidates grants the action. Each candidate's value is the configured one, or `DEFAULT` if the candidate or the action is missing.

Step 2 means `isAllowed` also works on models that weren't resolved. Because of the any-candidate rule, configuring `false` for a candidate can't take an action away from someone who also matches another candidate that grants it. To restrict an action, every candidate the member matches has to say `false`.

Not part of the model; these checks happen in the bot:

- Members with **Administrator** or **Manage Channels** pass every check (`Member.isAllowedTo`).
- The panel switches `closeable` and `claimable`:
  - `claimable = false` makes `resolveTicketClaimAction` return `NotClaimable` before any permission is checked.
  - `closeable = false` makes the close button and `/ticket close` delete the ticket, so they check `Delete` instead of `Close`.
- Claiming and unclaiming also need the ticket state to be exactly `Open`, so not `Creating`.

## Channel overwrites in the bot

```kotlin
fun channelPermissions(phase: TicketPhase, candidate: TicketPermissionCandidate): Map<DiscordChannelPermission, Boolean>
```

This returns the candidate's channel map for the phase. If the model doesn't contain the candidate, it returns the `DEFAULT` map.

`TicketSystem.updateTicketPermissions` (bot) builds the channel's overwrites from it. It runs when a ticket is created, reopened, closed, claimed and unclaimed.

| Candidate | Overwrite target | Rule |
|---|---|---|
| `SupportTeam` | each of the panel's `supportRoles` (role overwrite) | **skipped while `ticket.claimer != null`** |
| `AdditionalRoles` | each of the panel's `additionalRoles` (role overwrite) | always |
| `TicketCreator` | `ticket.user` (member overwrite) | always |
| `TicketClaimer` | `ticket.claimer` (member overwrite) | only if there is a claimer |
| `Everyone` | the `@everyone` role (role id = guild id) | always |

Rules for building the overwrites:

- **Empty map:** no overwrite is added for that target.
- **Hiding the support team while claimed:** this is fixed in the bot, not configurable. While a ticket is claimed, the `SupportTeam` channel permissions of that phase aren't applied at all.
- **Creator who is also the claimer:** their two maps are merged into one member overwrite. Per permission, `true` wins (`existing || new`). Discord only allows one overwrite per member.
- **`/ticket add`:** checks `AddUser` and gives the added user the **claimer's** channel permissions of the current phase. If that map is empty, the command replies with an error instead of adding nobody.
  - Added users aren't persisted. The next `updateTicketPermissions` call rebuilds the overwrites from scratch, so their overwrite is dropped.
  - Persisting added users is planned as a separate feature.

## Defaults (`DEFAULT`)

### Actions

✓ = granted, – = not granted.

Open phase:

| Candidate | Close | Claim | Unclaim | Delete | AddUser | GenerateTranscript |
|---|---|---|---|---|---|---|
| `SupportTeam` | ✓ | ✓ | – | ✓ | ✓ | ✓ |
| `AdditionalRoles` | ✓ | ✓ | – | ✓ | ✓ | ✓ |
| `TicketCreator` | ✓ | – | – | ✓ | ✓ | ✓ |
| `TicketClaimer` | – | – | ✓ | – | – | – |
| `Everyone` | – | – | – | – | – | – |

Closed phase:

| Candidate | Reopen | Delete | GenerateTranscript |
|---|---|---|---|
| `SupportTeam` | ✓ | ✓ | ✓ |
| `AdditionalRoles` | ✓ | ✓ | ✓ |
| `TicketCreator` | ✓ | ✓ | ✓ |
| `TicketClaimer` | – | – | – |
| `Everyone` | – | – | – |

Notes:
- The claimer can only unclaim through `TicketClaimer`. Usually the claimer is also on the support team, though, and gets that team's actions too.
- The creator deleting their own open ticket is intentional.

### Channel permissions

"Standard" means `ViewChannel`, `SendMessages` and `ReadMessageHistory` allowed.

| Candidate | Open | Closed |
|---|---|---|
| `SupportTeam` | standard | standard |
| `AdditionalRoles` | standard | standard |
| `TicketCreator` | standard | `{}`, so no overwrite: the creator loses access when the ticket is closed |
| `TicketClaimer` | standard | standard |
| `Everyone` | `ViewChannel` denied | `ViewChannel` denied |

## Creating and updating panels

| Model | `permissions == null` | `permissions != null` |
|---|---|---|
| `TicketPanelCreationModel` | `DEFAULT` | `permissions.resolved()` |
| `TicketPanelUpdateModel` | stored permissions stay unchanged | **replaces** the stored permissions with `permissions.resolved()` |

An update is **not merged** with what's stored. Missing parts fall back to `DEFAULT`, not to the current values. So an update that only contains `open.TicketCreator` resets every other candidate, and the whole `closed` phase, to the defaults. To change a single value, a client sends the panel's current (resolved) permissions with that value changed. The dashboard's import always sends a complete configuration: the one from the file.

`TicketPanelModel.permissions` is always resolved when it comes from the server.

## Storage (server)

Migration `V1_14__Ticket_Panel_Permissions` (H2, MariaDB, PostgreSQL):

- drops the ten old `*_permissions` columns of `ticket_panel`;
- creates two tables.

The old Kord values are **not migrated**. They could never be changed, so they always held the defaults, and the new defaults reproduce them.

| Table | Columns | Primary key |
|---|---|---|
| `ticket_panel_action_permission` | `ticket_panel`, `phase`, `candidate`, `ticket_action`, `allowed` | `(ticket_panel, phase, candidate, ticket_action)` |
| `ticket_panel_channel_permission` | `ticket_panel`, `phase`, `candidate`, `channel_permission`, `allowed` | `(ticket_panel, phase, candidate, channel_permission)` |

- All columns are `NOT NULL`. The enums are stored as ordinal `INT`s.
- `ticket_panel` references `ticket_panel (id)` with `ON DELETE CASCADE`.
- JPA: `TicketPanelActionPermission` and `TicketPanelChannelPermission`, each with an `@EmbeddedId` (`…Id`) and `@MapsId("ticketPanelId")`. `TicketPanel` holds them as `@OneToMany(cascade = ALL, orphanRemoval = true)`.

### What's stored

- **Actions:** one row per phase × candidate × action of the phase. That's 45 rows per panel today: 5 candidates × (6 open + 3 closed actions). Defaults are stored like everything else.
- **Channel permissions:** one row per permission that's allowed or denied. A missing row means "not overwritten". A candidate whose map is `{}` has no rows at all.

### Writing: `TicketPanel.setPermissions(model)`

1. Resolves the model.
2. Flattens it into maps keyed by `(phase, candidate, action/permission)`.
3. Goes through the existing rows:
   - a row whose key is still there gets its `allowed` updated in place;
   - a row whose key is gone is removed (orphan removal deletes it).
4. Adds the keys that are left as new rows.

An update therefore only writes the rows that actually change.

### Reading: `TicketPanel.getPermissions()`

1. Groups the rows into phase → candidate → maps.
2. Leaves out a candidate that has **no rows at all**, in either table. Without this step, a candidate with no channel rows would turn into `channel = {}`, which means "no overwrite", instead of the defaults.
3. Calls `resolved()`.

Reading after a schema change:

- **Panels from before V1_14:** they have no rows, so they read as `DEFAULT`. They get their rows on their next permission update. Creation always writes rows.
- **New actions or candidates:** their rows are missing, so they fall back to `DEFAULT` the same way.

Note: once written, a candidate's channel permissions are always explicit. A candidate with action rows but no channel rows reads back as `channel = {}` (no overwrite), not as the defaults. That matches what was written, because `resolved()` always produces explicit channel maps.

## Other consumers

- **Dashboard** (`ticket-panel-transfer.ts`):
  - Exports format **version 2** with the model above.
  - Imports validate version 2 permissions strictly: known phases, candidates and keys; only `actions` / `channel` on a candidate; boolean values.
  - **Version 1** files (Kord bit sets) are still accepted. Their permissions are replaced by `{ open: {}, closed: {} }`, which means all defaults, on creation and on overwrite.
  - The dashboard has no editor for permissions. Editing a panel sends `permissions: undefined`, so they stay unchanged.
- **TypeScript client:** generated from the server's `openapi.json`, with `TicketPanelPermissionsModel` and `TicketCandidatePermissionsModel` as schemas.
- **Carry-helper:** only parses panels. It needs client 0.8.0, because older versions can't read the new format.

## Extending the model

### Adding a ticket action

1. Append it to `TicketAction` with its `phases`.
2. Grant it in `DEFAULT` to the candidates that should have it, via the `vararg` of `defaultPermissions`. Anything not listed defaults to `false`.
3. **Server:** nothing to do. Stored panels have no rows for it yet, so they fall back to `DEFAULT` until their next permission update writes them.
4. **Bot:** check it with `Member.isAllowedTo(action, ticket)`.
5. **Dashboard:** add it to the set of valid actions in `ticket-panel-transfer.ts`.
6. Regenerate `openapi.json` and the TypeScript client.
7. Bump the model version. Old Moshi clients fail on the new key, so the rollout has to be coordinated.

### Adding a channel permission

1. Append it to `DiscordChannelPermission` with its Discord bit.
2. **Bot:** add a branch to `toKordPermission`. The exhaustive `when` won't compile without it, and the bit test checks the value.
3. **Dashboard:** add it to the dashboard's set of valid channel permissions.
4. Regenerate the spec and the client, and bump the version, as for actions.

### Adding a candidate

1. Append it to `TicketPermissionCandidate`.
2. Add `DEFAULT` entries for **both** phases. `isAllowed`, `channelPermissions` and `resolved()` use `getValue` on `DEFAULT`, so they would throw without them. `testDefaultsAreComplete` catches a missing entry.
3. **Bot:**
   - match the candidate in `getTicketPermissionCandidates`;
   - give it an overwrite target in `updateTicketPermissions`.
4. **Dashboard:** add it to the dashboard's set of valid candidates.

### Adding a phase

This is a larger change:

- `TicketPanelPermissionsModel` has fixed `open` / `closed` fields;
- `forPhase` and `TicketPhase.of` need new branches;
- `resolved()` lists both phases explicitly;
- the dashboard validates the phase names.

A "claimed" phase was already rejected, see [`TicketPhase`](#ticketphase).

### Removing or renaming an entry

Don't. The ordinals are stored and the names are the JSON keys. If an entry has to go, leave it in place and stop using it.

## Tests

- **`model/.../TicketPanelPermissionsModelTest`** covers:
  - `TicketPhase.of`;
  - that `DEFAULT` is complete and that `DEFAULT.resolved() == DEFAULT`;
  - the default actions and channel permissions;
  - resolving empty and partial models;
  - that an explicit `false` is kept;
  - that channel maps are taken as given;
  - that actions of the other phase are dropped;
  - the any-candidate rule, the phase check and the fallback to defaults in `isAllowed`.
- **`model/.../TicketPanelPermissionsSerializationTest`** covers:
  - the exact JSON;
  - Moshi ↔ Gson round trips;
  - missing fields;
  - Gson nulls;
  - unknown keys (Gson → `InvalidUpdateException`, Moshi → `JsonDataException`);
  - the panel, creation and update models.
- **Server:**
  - `TicketPanelPermissionsTest` covers creation and update through the service, with request bodies parsed by Gson;
  - `TicketPanelPermissionsMappingTest` covers the rows that get stored, round trips, in-place updates, removing channel rows and the fallback for missing rows.
- **Bot:**
  - `TicketPermissionOverwritesTest` covers the overwrites for open, claimed, closed, and closed-and-claimed tickets; configured creator permissions while closed; merging creator and claimer; empty maps; and the Kord bit mapping.
