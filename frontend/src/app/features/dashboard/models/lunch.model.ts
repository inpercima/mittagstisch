import { BistroModel } from './bistro.model';

export type LunchModel = {
  bistro: BistroModel;

  lunches: string;

  status: string;

  importDate: Date;
};
