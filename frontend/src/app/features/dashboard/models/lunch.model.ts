import { BistroModel } from './bistro.model';
import { DishModel } from './dish.model';

export type LunchModel = {
  bistro: BistroModel;

  dishes: DishModel[];

  status: string;

  importDate: Date;
};
