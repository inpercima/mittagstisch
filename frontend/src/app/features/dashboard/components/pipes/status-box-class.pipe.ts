import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'statusBoxClass'
})
export class StatusBoxClassPipe implements PipeTransform {
  transform(status: string): string {
    switch (status) {
      case 'SUCCESS':
        return 'border-t-emerald-400';
      case 'NO_DATA':
        return 'border-t-red-400';
      case 'NEXT_WEEK':
        return 'border-t-blue-400';
      case 'OUTDATED':
        return 'border-t-amber-400';
      default:
        return '';
    }
  }
}
