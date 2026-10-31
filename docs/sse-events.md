# Change Stream (Server-Sent Events)

The API pushes changes of entities to connected clients, so they don't have to poll for them.
Clients only receive the changes they are allowed to see.

All changes are still made through the REST API, the change stream only notifies about them.

## Connecting

```
GET /api/v1/changes
Authorization: Bearer <access token>
Accept: text/event-stream
```

| Parameter                 | Description                                                                                                                                                         |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `types` (query, optional) | Comma-separated list of [event types](#event-types) to receive, e.g. `?types=Ticket,StaticMessage`. All types if omitted. Unknown types are rejected with `400`. |
| `Last-Event-ID` (header)  | The id of the last event received before. Lets the API send the changes that were missed while reconnecting, see [Reconnecting](#reconnecting).                  |

Responses:

* `200` with `Content-Type: text/event-stream`: the stream, which stays open until the access token expires.
* `401`: the access token is missing or invalid.
* `400`: an unknown type was requested.

## The stream

The stream follows the [server-sent events format](https://html.spec.whatwg.org/multipage/server-sent-events.html).
Every connection starts with a `Connected` event, after that the API sends change events and heartbeats:

```
event:Connected
data:{"resumed":false}

id:5b1c0c5e-0d6f-4ad5-9c49-3e8e4b3f6c1d:42
event:Ticket
data:{"type":"Ticket","action":"Updated","serverId":693263712626278553,"data":{"id":12,"state":"Open","claimer":{"id":383819291687911424}, ...}}

:heartbeat

```

### `Connected` event

Sent as the first event of every connection.

| Field     | Type    | Description                                                                                                                                       |
|-----------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `resumed` | boolean | `true` if every change that was missed since the given `Last-Event-ID` was sent again. If `false`, changes might have been missed (see below). |

When `resumed` is `false` (always on the first connection), clients should reload the state they need through the
REST API. Changes published after the `Connected` event are always received, so nothing is lost between the reload
and the following events.

Model: `net.dungeonhub.model.change.ChangeStreamConnectedModel`

### Change events

The event name is the [type](#event-types) of the changed entity, the event id is used for
[reconnecting](#reconnecting), and the data is a change event:

| Field      | Type           | Description                                                                                       |
|------------|----------------|---------------------------------------------------------------------------------------------------|
| `type`     | string         | The [type](#event-types) of the changed entity, the same as the event name.                       |
| `action`   | string         | `Created`, `Updated` or `Deleted`.                                                                |
| `serverId` | number \| null | The Discord server the entity belongs to, or `null` if it doesn't belong to one.                  |
| `data`     | object         | The model of the entity after the change, or its last state if it was deleted. Same JSON as REST. |

Model: `net.dungeonhub.model.change.ChangeEventModel`

> Discord IDs are 64-bit integers, just like in REST responses. JavaScript clients have to parse them without losing
> precision, e.g. like the dashboard's `parseApiJson`.

### Heartbeats

Every 25 seconds the API sends the comment `:heartbeat`, so that neither clients nor proxies consider the connection
dead. SSE clients ignore comments, but a client can treat a connection without any data for about a minute as dead.

## Event types

Bot and admin clients (role `bot` or `admin`) receive every change. Other clients receive the changes listed below.
"Server managers" are clients with the authority `server_<id>` for the server of the entity, the same as for the
server-specific REST endpoints.

| Type              | Model (`data`)         | Received by                                                       | Sent when                                                                                     |
|-------------------|------------------------|-------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| `Ticket`          | `TicketModel`          | Server managers, the current claimer and the previous claimer     | A ticket is created, updated (e.g. claimed, unclaimed, closed) or deleted.                    |
| `StaticMessage`   | `StaticMessageModel`   | Server managers                                                   | A static message is created, updated (including its Discord message being sent) or deleted. |
| `CarryQueue`      | `CarryQueueModel`      | Bot and admin clients only                                        | A carry queue entry is created (e.g. by an in-game log), updated or deleted (e.g. logged).    |
| `CarryType`       | `CarryTypeModel`       | Server managers                                                   | A carry type is created, updated or deleted.                                                  |
| `CarryTier`       | `CarryTierModel`       | Server managers                                                   | A carry tier is created, updated or deleted.                                                  |
| `CarryDifficulty` | `CarryDifficultyModel` | Server managers                                                   | A carry difficulty is created, updated or deleted.                                            |
| `TicketPanel`     | `TicketPanelModel`     | Server managers                                                   | A ticket panel is created, updated or deleted.                                                |
| `Score`           | `ScoreModel`           | Server managers                                                   | A score changes, e.g. after a carry was logged or scores were reset. One event per score.    |
| `Reputation`      | `ReputationModel`      | Server managers                                                   | A reputation entry is created, updated or deleted.                                            |

Notes:

* The previous claimer of a ticket receives the change that unclaimed the ticket or assigned it to someone else, so
  clients can drop tickets that aren't theirs anymore. Clients should check `claimer` and `state` of the ticket.
* `ScoreModel` has no id: a score is identified by its `carrier`, `carryType` and `scoreType`.
* When the database removes entities because their parent was deleted (e.g. the tiers of a deleted carry type), only
  the deletion of the parent is sent.

## Reconnecting

The API closes the stream once the access token used for it expires, as the permissions of the client might have
changed. It's also closed by network issues or when the API restarts. Clients are expected to reconnect, using a
fresh access token.

To not miss any changes, clients send the id of the last event they received in the `Last-Event-ID` header (also for
events of types they don't know). The API then sends every change that was published in the meantime and that the
client is allowed to see, and `Connected` has `resumed: true`. This works as long as the missed changes happened in
the last 10 minutes (at most 10,000 changes) and the API wasn't restarted in the meantime. Otherwise `resumed` is
`false` and the client should reload its state.

Event ids are opaque strings, don't parse them.

## Compatibility

New event types will be added over time. Clients have to ignore events with names they don't know (and should still
remember their id), so that older clients keep working. Requesting an unknown type with `types` is rejected, so only
request the types the client knows.

New fields may be added to the models, so clients should ignore unknown fields.

## Usage

### Kotlin client

`ChangeStreamConnection` connects, reconnects with backoff and resumes the stream automatically. Changes of unknown
types are skipped.

```kotlin
ChangeStreamConnection.authenticated().changes(setOf(ChangeEventType.Ticket)).collect { event ->
    when (event) {
        is ChangeStreamEvent.Connected -> if (!event.resumed) reloadClaimedTickets()
        is ChangeStreamEvent.Change -> {
            val ticket = event.change.data as? TicketModel ?: return@collect
            updateTicket(event.change.action, ticket)
        }
    }
}
```

The events are emitted one after another, in the order the API sent them.

### Browser

The browser's `EventSource` can't send the `Authorization` header, so the stream has to be read with `fetch`. The
dashboard's `ChangeStreamService` shows how to do that, including reconnecting with `Last-Event-ID`.

### Command line

```bash
curl -N -H "Authorization: Bearer $TOKEN" "https://api.dungeon-hub.net/api/v1/changes?types=StaticMessage"
```

## Adding a new event type

1. Add an entry to `ChangeEventType` in the `model` module, with the model class that is sent as `data`.
2. In the service of the entity on the server, call `ChangePublisher.publish(...)` after saving and after deleting,
   with the `ChangeAudience` that is allowed to see the change (usually the same as for its REST endpoints).
3. Add the type to the table above.
