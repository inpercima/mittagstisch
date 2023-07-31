import { Routes } from '@angular/router';

export const ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard.component').then((c) => c.DashboardComponent),
  },
  {
    path: 'today',
    loadComponent: () => import('./shared/lunch/lunch.component').then((c) => c.LunchComponent),
  },
  {
    path: 'tomorrow',
    loadComponent: () => import('./shared/lunch/lunch.component').then((c) => c.LunchComponent),
  },
  {
    path: 'next-week',
    loadComponent: () => import('./shared/lunch/lunch.component').then((c) => c.LunchComponent),
  },
];
