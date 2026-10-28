import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'statusTextClass'
})
export class StatusTextClassPipe implements PipeTransform {
  transform(status: string): string {
    switch (status) {
      case 'SUCCESS':
        return 'bg-emerald-50 text-emerald-700 ring-emerald-600/20 dark:bg-emerald-900/30 dark:text-emerald-300';
      case 'NO_DATA':
        return 'bg-red-50 text-red-700 ring-red-600/20 dark:bg-red-900/30 dark:text-red-300';
      case 'NEXT_WEEK':
        return 'bg-blue-50 text-blue-700 ring-blue-600/20 dark:bg-blue-900/30 dark:text-blue-300';
      case 'OUTDATED':
        return 'bg-amber-50 text-amber-700 ring-amber-600/20 dark:bg-amber-900/30 dark:text-amber-300';
      default:
        return '';
    }
  }
}
