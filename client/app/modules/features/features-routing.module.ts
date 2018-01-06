import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from '../../services/auth-guard.service';

import { DashComponent } from '../../components/dash/dash.component';
import { TodayComponent } from '../../components/today/today.component';
import { TomorrowComponent } from '../../components/tomorrow/tomorrow.component';
import { NextWeekComponent } from '../../components/next-week/next-week.component';

const routes: Routes = [{
  component: DashComponent,
  path: 'dash',
  data: { title: 'Dashboard'},
  canActivate: [AuthGuard],
}, {
  component: TodayComponent,
  path: 'today',
  data: { title: 'Heute'},
  canActivate: [AuthGuard],
}, {
  component: TomorrowComponent,
  path: 'tomorrow',
  data: { title: 'Morgen'},
  canActivate: [AuthGuard],
}, {
  component: NextWeekComponent,
  path: 'next-week',
  data: { title: 'nächste Woche'},
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
