import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';

import { MaterialModule } from '../shared/lunch-entry/material/material.module';
import { FeaturesRoutingModule } from './features-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NextWeekComponent } from './next-week/next-week.component';
import { TodayComponent } from './today/today.component';
import { TomorrowComponent } from './tomorrow/tomorrow.component';

@NgModule({
  declarations: [
    DashboardComponent,
    NextWeekComponent,
    TodayComponent,
    TomorrowComponent,
  ],
  imports: [
    CommonModule,
    FeaturesRoutingModule,
    HttpClientModule,
    MaterialModule,
  ],
})
export class FeaturesModule { }
