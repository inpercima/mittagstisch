import { NgModule } from '@angular/core';

import { AuthService } from './auth.service';
import { AuthGuard } from './auth-guard.service';
import { LunchService } from './lunch.service';
import { RequestService } from './request.service';

@NgModule({
  providers: [
    AuthService,
    AuthGuard,
    LunchService,
    RequestService,
  ],
})
export class CoreModule { }
