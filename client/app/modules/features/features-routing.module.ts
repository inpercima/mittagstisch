import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from '../../services/auth-guard.service';

import { DashComponent } from '../../components/dash/dash.component';

const routes: Routes = [{
  component: DashComponent,
  path: 'dash',
  canActivate: [AuthGuard],
}];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule
  ],
})

export class FeaturesRoutingModule {

  public static ROUTES = routes;

}
