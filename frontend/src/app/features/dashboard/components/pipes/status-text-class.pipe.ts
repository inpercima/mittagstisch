import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'statusTextClass'
})
export class StatusTextClassPipe implements PipeTransform {
  transform(status: string): string {
    switch (status) {
      case 'SUCCESS':
        return 'bg-emerald-100 text-emerald-700';
      case 'NO_DATA':
        return 'bg-red-100 text-red-700';
      case 'NEXT_WEEK':
        return 'bg-blue-100 text-blue-700';
      case 'OUTDATED':
        return 'bg-amber-100 text-amber-700';
      default:
        return '';
    }
  }
}
