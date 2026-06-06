# Integration Guide: Using @dungeon-hub/api-client in Angular Dashboard

This guide shows how to integrate the TypeScript API client into your Angular dashboard.

## Quick Start

### 1. Install the client

**Option A: Published package (when available)**
```bash
npm install @dungeon-hub/api-client
```

**Option B: Local development**
```bash
# In dungeon-hub-api/typescript-client
npm link

# In your dashboard project
npm link @dungeon-hub/api-client
```

### 2. Configure your Angular app

**app.config.ts** (Standalone components):
```typescript
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ApiModule, Configuration } from '@dungeon-hub/api-client';
import { authInterceptor } from './core/auth.interceptor';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    ApiModule.forRoot(() => new Configuration({
      basePath: 'https://api.dungeon-hub.net',
      // Or from environment: environment.apiUrl
    })).providers || [],
  ]
};
```

**app.module.ts** (NgModule-based):
```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ApiModule, Configuration } from '@dungeon-hub/api-client';
import { AuthInterceptor } from './core/auth.interceptor';
import { AppComponent } from './app.component';

export function apiConfigFactory(): Configuration {
  return new Configuration({
    basePath: 'https://api.dungeon-hub.net'
  });
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    ApiModule.forRoot(apiConfigFactory)
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### 3. Create an Auth Interceptor

**auth.interceptor.ts**:
```typescript
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
```

**auth.service.ts** (example):
```typescript
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private accessToken: string | null = null;

  getAccessToken(): string | null {
    // Get from memory, localStorage, or session
    return this.accessToken || localStorage.getItem('access_token');
  }

  setAccessToken(token: string): void {
    this.accessToken = token;
    localStorage.setItem('access_token', token);
  }

  clearAccessToken(): void {
    this.accessToken = null;
    localStorage.removeItem('access_token');
  }
}
```

## Usage Examples

### Ticket Panel Management

**ticket-panel.component.ts**:
```typescript
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import {
  TicketPanelService,
  TicketPanelModel,
  TicketPanelUpdateModel
} from '@dungeon-hub/api-client';

@Component({
  selector: 'app-ticket-panel-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div *ngIf="panel">
      <h2>Edit: {{ panel.displayName || panel.name }}</h2>
      
      <form [formGroup]="form" (ngSubmit)="save()">
        <label>
          Internal Name
          <input formControlName="name" type="text">
        </label>
        
        <label>
          Display Name
          <input formControlName="displayName" type="text">
        </label>
        
        <label>
          Emoji
          <input formControlName="emoji" type="text">
        </label>
        
        <label>
          <input formControlName="requiresLinking" type="checkbox">
          Require Linked Account
        </label>
        
        <button type="submit" [disabled]="!form.valid || saving">
          {{ saving ? 'Saving...' : 'Save Changes' }}
        </button>
      </form>
    </div>
  `
})
export class TicketPanelEditComponent implements OnInit {
  private ticketPanelApi = inject(TicketPanelService);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);

  panel?: TicketPanelModel;
  form!: FormGroup;
  saving = false;

  ngOnInit() {
    const serverId = this.route.snapshot.params['serverId'];
    const panelId = this.route.snapshot.params['panelId'];

    this.form = this.fb.group({
      name: [''],
      displayName: [''],
      emoji: [''],
      requiresLinking: [false]
    });

    this.loadPanel(serverId, panelId);
  }

  loadPanel(serverId: string, panelId: string) {
    this.ticketPanelApi.getById(serverId, panelId).subscribe({
      next: (panel) => {
        this.panel = panel;
        this.form.patchValue({
          name: panel.name,
          displayName: panel.displayName || '',
          emoji: panel.emoji || '',
          requiresLinking: panel.requiresLinking || false
        });
      },
      error: (err) => console.error('Failed to load panel', err)
    });
  }

  save() {
    if (!this.panel || !this.form.valid) return;

    this.saving = true;
    const update: TicketPanelUpdateModel = this.form.value;

    this.ticketPanelApi.updateTicketPanel(
      this.panel.discordServer.id,
      this.panel.id,
      update
    ).subscribe({
      next: (updated) => {
        this.panel = updated;
        this.saving = false;
        console.log('Saved successfully!');
      },
      error: (err) => {
        console.error('Save failed', err);
        this.saving = false;
      }
    });
  }
}
```

### CNT Request Management

**cnt-request-list.component.ts**:
```typescript
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  CntRequestService,
  CntRequestModel,
  CntRequestPage
} from '@dungeon-hub/api-client';

@Component({
  selector: 'app-cnt-request-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <h2>CNT Requests</h2>
    
    <div class="request-list">
      <div *ngFor="let request of requests" class="request-card">
        <h3>Request #{{ request.id }}</h3>
        <p>Type: {{ request.requestType }}</p>
        <p>Status: {{ request.completed ? 'Completed' : 'Pending' }}</p>
        <a [routerLink]="['/server', serverId, 'cnt-request', request.id]">
          Edit
        </a>
      </div>
    </div>
    
    <div class="pagination">
      <button (click)="previousPage()" [disabled]="page === 0">
        Previous
      </button>
      <span>Page {{ page + 1 }}</span>
      <button (click)="nextPage()" [disabled]="!hasMore">
        Next
      </button>
    </div>
  `
})
export class CntRequestListComponent implements OnInit {
  private cntRequestApi = inject(CntRequestService);
  
  serverId = "123456789"; // Get from route or service
  requests: CntRequestModel[] = [];
  page = 0;
  hasMore = false;

  ngOnInit() {
    this.loadRequests();
  }

  loadRequests() {
    this.cntRequestApi.getCntRequests(this.serverId, this.page, 10).subscribe({
      next: (response: CntRequestPage) => {
        this.requests = response.requests;
        this.hasMore = response.hasNextPage();
      },
      error: (err) => console.error('Failed to load requests', err)
    });
  }

  nextPage() {
    if (this.hasMore) {
      this.page++;
      this.loadRequests();
    }
  }

  previousPage() {
    if (this.page > 0) {
      this.page--;
      this.loadRequests();
    }
  }
}
```

### Server Selection

**server-list.component.ts**:
```typescript
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  DiscordServerService,
  DiscordServerModel
} from '@dungeon-hub/api-client';

@Component({
  selector: 'app-server-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <h2>Your Servers</h2>
    
    <div class="server-grid">
      <a *ngFor="let server of servers" 
         [routerLink]="['/server', server.id]"
         class="server-card">
        <img [src]="getIconUrl(server)" [alt]="server.name">
        <h3>{{ server.name }}</h3>
      </a>
    </div>
  `
})
export class ServerListComponent implements OnInit {
  private serverApi = inject(DiscordServerService);
  servers: DiscordServerModel[] = [];

  ngOnInit() {
    this.loadServers();
  }

  loadServers() {
    // Assuming you have an endpoint that returns user's servers
    // This might need to be adjusted based on your actual API
    this.serverApi.getAllServers().subscribe({
      next: (servers) => this.servers = servers,
      error: (err) => console.error('Failed to load servers', err)
    });
  }

  getIconUrl(server: DiscordServerModel): string {
    return server.icon 
      ? `https://cdn.discordapp.com/icons/${server.id}/${server.icon}.png`
      : '/assets/default-server-icon.png';
  }
}
```

## Advanced: Form State Preservation

Replace your manual localStorage implementation with Angular's built-in state management:

**form-state.service.ts**:
```typescript
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class FormStateService {
  private readonly STORAGE_PREFIX = 'form_state_';
  private readonly EXPIRY_MS = 30 * 60 * 1000; // 30 minutes

  save(formId: string, data: any): void {
    const state = {
      data,
      timestamp: Date.now()
    };
    localStorage.setItem(
      this.STORAGE_PREFIX + formId,
      JSON.stringify(state)
    );
  }

  restore(formId: string): any | null {
    const stored = localStorage.getItem(this.STORAGE_PREFIX + formId);
    if (!stored) return null;

    try {
      const state = JSON.parse(stored);
      const age = Date.now() - state.timestamp;
      
      if (age < this.EXPIRY_MS) {
        return state.data;
      } else {
        this.clear(formId);
        return null;
      }
    } catch {
      return null;
    }
  }

  clear(formId: string): void {
    localStorage.removeItem(this.STORAGE_PREFIX + formId);
  }
}
```

**Usage in component**:
```typescript
export class TicketPanelEditComponent implements OnInit {
  private formState = inject(FormStateService);
  private formId = 'ticket-panel-edit';

  ngOnInit() {
    // ... create form

    // Restore saved state
    const saved = this.formState.restore(this.formId);
    if (saved) {
      this.form.patchValue(saved);
    }

    // Auto-save on changes
    this.form.valueChanges.pipe(
      debounceTime(500)
    ).subscribe(value => {
      this.formState.save(this.formId, value);
    });
  }

  save() {
    // Clear saved state on successful save
    this.ticketPanelApi.updateTicketPanel(...).subscribe({
      next: () => {
        this.formState.clear(this.formId);
      }
    });
  }
}
```

## Error Handling

Create a global error handler:

**error-handler.interceptor.ts**:
```typescript
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const errorHandlerInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        // Unauthorized - redirect to login
        authService.clearAccessToken();
        router.navigate(['/login'], {
          queryParams: { returnUrl: router.url }
        });
      } else if (error.status === 403) {
        // Forbidden
        console.error('Access denied');
      } else if (error.status >= 500) {
        // Server error
        console.error('Server error:', error);
      }

      return throwError(() => error);
    })
  );
};
```

## Environment Configuration

**environment.ts**:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

**environment.prod.ts**:
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.dungeon-hub.net'
};
```

**app.config.ts** (use environment):
```typescript
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    // ...
    ApiModule.forRoot(() => new Configuration({
      basePath: environment.apiUrl
    })).providers || [],
  ]
};
```

## Migration from Kotlin Client

If migrating from your existing Ktor/kotlinx-html implementation:

| Old (Kotlin) | New (Angular + TypeScript Client) |
|--------------|-----------------------------------|
| `call.sessions.get<UserSession>()` | `authService.getAccessToken()` |
| `TicketPanelConnection[serverId].authenticated(session).getById(id)` | `ticketPanelService.getById(serverId, id)` |
| `kotlinx-html` DSL | Angular templates |
| `call.receiveParameters()` | `FormGroup.value` |
| Manual token refresh | HTTP interceptor |
| Manual form state | RxJS + localStorage |

## Next Steps

1. **Generate the client**: Run `./generate.sh` in `typescript-client/`
2. **Link locally**: Use `npm link` for development
3. **Create Angular app**: Use Angular CLI to scaffold
4. **Integrate API client**: Follow this guide
5. **Migrate routes**: One page at a time from Kotlin to Angular

## Resources

- [Angular Documentation](https://angular.io/docs)
- [RxJS Documentation](https://rxjs.dev/)
- [Angular Forms Guide](https://angular.io/guide/forms-overview)
- [TypeScript Client README](./README.md)
