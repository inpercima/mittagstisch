import { OverlayModule } from '@angular/cdk/overlay';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AppRoutingPipe } from './app-routing.pipe';
import { FeaturesModule } from './features/features.module';
import { MaterialModule } from './shared/lunch-entry/material/material.module';

@NgModule({
  bootstrap: [
    AppComponent,
  ],
  declarations: [
    AppComponent,
    AppRoutingPipe,
  ],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    FeaturesModule,
    FlexLayoutModule,
    OverlayModule,
    MaterialModule,
  ],
})
export class AppModule { }
