# @dungeon-hub/api-client

TypeScript/Angular client for the Dungeon Hub API. Auto-generated from OpenAPI specification.

## Installation

### From npm (production)
```bash
npm install @dungeon-hub/api-client
```

### Local development (npm link)
```bash
# In api-client directory
npm run generate  # or generate.bat on Windows
npm run build
npm run link

# In your Angular app directory
npm link @dungeon-hub/api-client
```

## Quick Start

### 1. Configure the API

```typescript
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideApi } from '@dungeon-hub/api-client';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),
    provideApi({
      basePath: 'https://api.dungeon-hub.net'
    })
  ]
};
```

### 2. Use the services

```typescript
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TicketPanelControllerService, TicketPanelModel } from '@dungeon-hub/api-client';

@Component({
  selector: 'app-ticket-panel',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="panel">
      <h2>{{ panel.displayName || panel.name }}</h2>
      <p>ID: {{ panel.id }}</p>
    </div>
  `
})
export class TicketPanelComponent implements OnInit {
  private ticketPanelService = inject(TicketPanelControllerService);
  panel?: TicketPanelModel;

  ngOnInit() {
    this.ticketPanelService.getById1(123456789, 1).subscribe({
      next: (panel) => this.panel = panel,
      error: (err) => console.error('Failed to load panel', err)
    });
  }
}
```

## Authentication

Configure Bearer token authentication:

```typescript
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('access_token');
  
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  
  return next(req);
};

// In app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])),
    // ... rest of config
  ]
};
```

## Available Services

All Spring Boot controllers are available as Angular services:

- `TicketPanelControllerService` - Ticket panel management
- `TicketControllerService` - Ticket operations
- `CntRequestControllerService` - CNT request management
- `DiscordServerControllerService` - Discord server operations
- `DiscordUserControllerService` - Discord user operations
- `DiscordRoleControllerService` - Discord role management
- `DiscordChannelControllerService` - Discord channel management
- `WarnsService` - Warning system
- `ReputationControllerService` - Reputation management
- `CarryQueueService` - Queue operations
- `StaticMessageControllerService` - Static message management

## Important Notes

### Discord Snowflake IDs
All Discord IDs (server IDs, user IDs, etc.) are represented as **strings** in TypeScript to prevent precision loss. JavaScript's `number` type cannot safely represent 64-bit integers.

```typescript
// ✅ Correct
const serverId: string = "1023684107877761200";

// ❌ Wrong - will lose precision
const serverId: number = 1023684107877761200;
```
- `ContentService` - Content operations
- And more...

## Models

All DTOs are available as TypeScript interfaces in the `model` directory:

```typescript
import {
  TicketPanelModel,
  TicketPanelCreationModel,
  TicketPanelUpdateModel,
  CntRequestModel,
  DiscordServerModel,
  // ... etc
} from '@dungeon-hub/api-client';
```

## Development

### Local Development with npm link

```bash
# In dungeon-hub-api/typescript-client
npm link

# In your Angular project
npm link @dungeon-hub/api-client
```

Changes to the client are immediately available in your project.

### Regenerating the Client

When the API changes, regenerate the client:

```bash
# Ensure Spring Boot server is running on http://localhost:8080
npm run generate

# Or from a different API URL
API_URL=https://api.dungeon-hub.net npm run generate
```

## Configuration Options

The `Configuration` class accepts these options:

```typescript
new Configuration({
  basePath: 'https://api.dungeon-hub.net',  // API base URL
  apiKeys: { /* API key auth if needed */ },
  username: 'user',                          // Basic auth username
  password: 'pass',                          // Basic auth password
  accessToken: 'token',                      // Bearer token (or function)
  withCredentials: true,                     // Include cookies
})
```

For dynamic token handling, use an interceptor (see Authentication section above).

## Version Compatibility

| @dungeon-hub/api-client | Angular | Dungeon Hub API |
|------------------------|---------|-----------------|
| 0.7.x                  | >=16.0  | 1.0.x          |

## Support

- Website: [dungeon-hub.net](https://dungeon-hub.net)
- Discord: [discord.dungeon-hub.net](https://discord.dungeon-hub.net)
- Issues: [GitHub Issues](https://github.com/dungeon-hub/dungeon-hub-api/issues)

## License

GPL-3.0 - See [LICENSE](../LICENSE) for details.

## Related Projects

- [dungeon-hub-api](https://github.com/dungeon-hub/dungeon-hub-api) - Kotlin API client
- [dungeon-hub-server](https://github.com/dungeon-hub/dungeon-hub-server) - Spring Boot server
