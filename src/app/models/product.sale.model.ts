import {Product} from "./product.model";

export class ProductSales {
  constructor(
    public id: number,
    public product: Product,
    public productId: number,
    public quantity: number,
    public returnQuantity: number,
    public detail: string,
  ) {
  }
}
