import { Category } from './category.model';
import { Product } from './product.model';
import {Company} from "./compnay.model";
export class ProductRequest {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public price: number,
    public quantity: number,
    public category: Category,
    public company: Company,
    public images: boolean,
    private sort: string = "id",
    private sortdirection: string = "asc",
    private pagesize: number = 10,
    private pagenumber: number = 0
  ) { }
  convert(
    product: Product,
  ) {
    this.id = product.id
    this.name = product.name
    this.description = product.description
    this.price = product.price
    this.quantity = product.quantity
    this.category = product.category
    this.company = product.company
    this.images = product.images
  }
}
