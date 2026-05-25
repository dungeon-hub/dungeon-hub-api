## Dungeon Hub API

This Project contains all relevant code related to the [Dungeon Hub API](https://api.dungeon-hub.net/). \
These packages are directly used by the Dungeon Hub Discord Bot and the Dungeon Hub API to communicate, meaning that not only this is the official implementation, but also the best way to work with the API. \
An documentation about this project and more can be found [here](https://docs.dungeon-hub.net/).

### Model
This repository contains all relevant model classes which can be used to interact with the API. \
They can be found under `/model`. \
![Maven Central Version](https://img.shields.io/maven-central/v/net.dungeon-hub.api/model) \
![Translation Status](https://weblate.dungeon-hub.net/widget/dungeon-hub/dungeon-hub-api-model/287x66-grey.png)

### Clients

#### Kotlin Client (JVM)

Under `/client` you can find the Kotlin/JVM client implementation that can be used to directly interact with the API. \
![Maven Central Version](https://img.shields.io/maven-central/v/net.dungeon-hub.api/client)

**Installation:**

```kotlin
dependencies {
    implementation("net.dungeon-hub.api:client:<latest-version>")
}
```

#### TypeScript Client (Angular)

Under `/typescript-client` you can find the TypeScript/Angular client auto-generated from the OpenAPI specification. \
![npm version](https://img.shields.io/npm/v/@dungeon-hub/api-client)

**Installation:**

```bash
npm install @dungeon-hub/api-client
```

**Usage:**

```typescript
import { TicketPanelService } from '@dungeon-hub/api-client';

// Inject and use in your components
constructor(private ticketPanelApi: TicketPanelService) {}
```

See the [TypeScript client README](./typescript-client/README.md) for full documentation.
