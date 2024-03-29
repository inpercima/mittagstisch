import { OverlayModule } from '@angular/cdk/overlay';
import { provideHttpClient } from '@angular/common/http';
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ROUTES } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [importProvidersFrom(BrowserModule, OverlayModule), provideHttpClient(), provideAnimations(), provideRouter(ROUTES)],
};
