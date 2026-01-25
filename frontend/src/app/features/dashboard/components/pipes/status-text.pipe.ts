import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'statusText'
})
export class StatusTextPipe implements PipeTransform {
  transform(status: string): string {
    switch (status) {
      case 'SUCCESS':
        return 'Aktuell';
      case 'NO_DATA':
        return 'Keine Daten';
      case 'NEXT_WEEK':
        return 'Nächste Woche';
      case 'OUTDATED':
        return 'Veraltet';
      default:
        return '';
    }
  }
}
