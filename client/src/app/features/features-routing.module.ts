import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './dashboard/dashboard.component';
import { NextWeekComponent } from './next-week/next-week.component';
import { TodayComponent } from './today/today.component';
import { TomorrowComponent } from './tomorrow/tomorrow.component';
import { environment } from '../../environments/environment';

const routes: Routes = [{
  component: DashboardComponent,
  path: environment.defaultRoute,
  data: { title: 'Dashboard' },
}, {
  component: TodayComponent,
  path: 'today',
  data: { title: 'Heute' },
}, {
  component: TomorrowComponent,
  path: 'tomorrow',
  data: { title: 'Morgen' },
}, {
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
