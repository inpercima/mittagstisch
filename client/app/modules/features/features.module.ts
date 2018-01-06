import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { NgModule } from '@angular/core';

import { FeaturesRoutingModule } from './features-routing.module';
import { MaterialModule } from '../material/material.module';

import { DashComponent } from '../../components/dash/dash.component';
import { TodayComponent } from '../../components/today/today.component';
import { TomorrowComponent } from '../../components/tomorrow/tomorrow.component';
import { NextWeekComponent } from '../../components/next-week/next-week.component';

import { LunchService } from '../../services/lunch.service';

@NgModule({
  declarations: [
    DashComponent,
    TodayComponent,
    TomorrowComponent,
    NextWeekComponent,
  ],
  imports: [
    CommonModule,
    FeaturesRoutingModule,
    FlexLayoutModule,
    MaterialModule,
  ],
  providers: [
    LunchService,
  ],
})

export class FeaturesModule { }
