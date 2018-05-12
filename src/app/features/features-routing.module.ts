import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import * as config from '../../config.json';
import { AuthGuard } from '../core/auth-guard.service';
import { DashComponent } from './dash/dash.component';
import { TodayComponent } from './today/today.component';
import { TomorrowComponent } from './tomorrow/tomorrow.component';
import { NextWeekComponent } from './next-week/next-week.component';

const routes: Routes = [{
  canActivate: [AuthGuard],
  component: DashComponent,
  path: (<any>config).routes.default,
  data: { title: 'Dashboard' },
}, {
  component: TodayComponent,
  path: 'today',
  data: { title: 'Heute' },
  canActivate: [AuthGuard],
}, {
  component: TomorrowComponent,
  path: 'tomorrow',
  data: { title: 'Morgen' },
  canActivate: [AuthGuard],
}, {
  component: NextWeekComponent,
  path: 'next-week',
  data: { title: 'nächste Woche' },
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
