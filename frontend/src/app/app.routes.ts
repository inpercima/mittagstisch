import { Routes } from '@angular/router';

export const routeConfig: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard').then((c) => c.Dashboard),
  },
  {
    path: 'today',
    loadComponent: () => import('./shared/lunch/lunch').then((c) => c.Lunch),
  },
  {
    path: 'tomorrow',
    loadComponent: () => import('./shared/lunch/lunch').then((c) => c.Lunch),
  },
];
