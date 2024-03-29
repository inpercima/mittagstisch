import { Pipe, PipeTransform } from '@angular/core';
import { Routes } from '@angular/router';

@Pipe({
  name: 'appRouting',
  standalone: true,
})
export class AppRoutingPipe implements PipeTransform {
  transform(items: Routes): Routes {
    return items.filter((item) => item.path !== '' && item.path !== '**');
  }
}
