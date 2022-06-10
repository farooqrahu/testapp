import {Product} from "./product.model";

export class ProductSales {
  constructor(
    public id: number,
    public product: Product,
    public productId: number,
    public extra: number,
    public bundle: number,
    public quantity: number,
    public extraReturn: number,
    public bundleReturn: number,
    public quantityReturn: number,
    public userQuantityBundle: number,
    public userExtraQuantity: number,
    public userTotalQuantity: number,
    public detail: string,
    public isReturn: boolean,
  ) {
  }
}
