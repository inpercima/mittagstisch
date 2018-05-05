import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressBarModule } from '@angular/material/progress-bar';

import { DashComponent } from './dash/dash.component';
import { FeaturesRoutingModule } from './features-routing.module';
import { NextWeekComponent } from './next-week/next-week.component';
import { TodayComponent } from './today/today.component';
import { TomorrowComponent } from './tomorrow/tomorrow.component';

@NgModule({
  declarations: [
    DashComponent,
    NextWeekComponent,
    TodayComponent,
    TomorrowComponent,
  ],
  imports: [
    CommonModule,
    FeaturesRoutingModule,
    FlexLayoutModule,
    MatCardModule,
    MatExpansionModule,
    MatProgressBarModule,
  ],
})
export class FeaturesModule { }
