import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { NgModule } from '@angular/core';

import { FeaturesRoutingModule } from './features-routing.module';
import { MaterialModule } from '../material/material.module';

import { DashComponent } from '../../components/dash/dash.component';

@NgModule({
  declarations: [
    DashComponent,
  ],
  imports: [
    CommonModule,
    FeaturesRoutingModule,
    FlexLayoutModule,
    MaterialModule,
  ],
})

export class FeaturesModule { }
