import { Category } from './category.model';
import {Company} from "./compnay.model";
export class Product {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public price: number,
    public category: Category,
    public company: Company,
    public images: boolean,
    public quantityItem: number,
    public quantityBundle: number,
    public extraQuantity: number,
    public quantity: number,
    public enableTQ: boolean,
    public file: string,
    public createdAt: Date,
    public userName: string,
    public userQuantityBundle: number,
    public userExtraQuantity: number,
    public userTotalQuantity: number,

  ) { }

}
