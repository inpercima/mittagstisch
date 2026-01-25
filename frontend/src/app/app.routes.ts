import { Routes } from '@angular/router';

export const routeConfig: Routes = [
  {
    path: '',
    redirectTo: 'today',
    pathMatch: 'full',
  },
  {
    path: '',
    loadComponent: () => import('./features/dashboard/dashboard').then((c) => c.Dashboard),
    children: [
      {
        path: 'today',
        loadComponent: () => import('./features/dashboard/components/day-view/day-view').then((c) => c.DayView),
        data: { label: 'Heute' },
      },
      {
        path: 'tomorrow',
        loadComponent: () => import('./features/dashboard/components/day-view/day-view').then((c) => c.DayView),
        data: { label: 'Morgen' },
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
