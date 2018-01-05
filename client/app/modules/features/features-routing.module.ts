import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthGuard } from '../../services/auth-guard.service';

import { DashComponent } from '../../components/dash/dash.component';
import { TodayComponent } from '../../components/today/today.component';
import { TomorrowComponent } from '../../components/tomorrow/tomorrow.component';
import { WeekComponent } from '../../components/week/week.component';

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
  component: WeekComponent,
  path: 'week',
  data: { title: 'Woche'},
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
