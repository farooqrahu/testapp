import {Product} from "./product.model";

export class ProductSales {
  constructor(
    public id: number,
    public product: Product,
    public productId: number,
    public extraSale: number,
    public bundleSale: number,
    public totalQuantitySale: number,
    public extraReturn: number,
    public bundleReturn: number,
    public totalQuantityReturn: number,
    public userQuantityBundle: number,
    public userExtraQuantity: number,
    public userTotalQuantity: number,
    public detail: string,
    public isReturn: boolean,
  ) {
  }
}
