import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'statusTextClass'
})
export class StatusTextClassPipe implements PipeTransform {
  transform(status: string): string {
    switch (status) {
      case 'status-success':
        return 'bg-emerald-100 text-emerald-700';
      case 'status-error':
        return 'bg-amber-100 text-amber-700';
      case 'pending':
        return '';
      default:
        return '';
    }
  }
}
