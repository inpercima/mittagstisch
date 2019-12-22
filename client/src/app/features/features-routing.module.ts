import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from '../core/auth-guard.service';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NextWeekComponent } from './next-week/next-week.component';
import { TodayComponent } from './today/today.component';
import { TomorrowComponent } from './tomorrow/tomorrow.component';
import { environment } from '../../environments/environment';

const routes: Routes = [{
  canActivate: [AuthGuard],
  component: DashboardComponent,
  path: environment.defaultRoute,
  data: { title: 'Dashboard' },
}, {
  canActivate: [AuthGuard],
  component: TodayComponent,
  path: 'today',
  data: { title: 'Heute' },
}, {
  canActivate: [AuthGuard],
  component: TomorrowComponent,
  path: 'tomorrow',
  data: { title: 'Morgen' },
}, {
  canActivate: [AuthGuard],
  component: NextWeekComponent,
  path: 'next-week',
  data: { title: 'nächste Woche' },
}];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [
    RouterModule,
  ],
})
export class FeaturesRoutingModule {

  public static ROUTES = routes;

}
