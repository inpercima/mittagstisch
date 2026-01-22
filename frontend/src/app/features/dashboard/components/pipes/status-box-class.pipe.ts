import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'statusBoxClass'
})
export class StatusBoxClassPipe implements PipeTransform {
  transform(status: string): string {
    switch (status) {
      case 'status-success':
        return 'bg-white';
      case 'status-error':
        return 'bg-slate-50';
      case 'status-pending':
        return '';
      default:
        return '';
    }
  }
}
