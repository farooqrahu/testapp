import {ProductSales} from "./product.sale.model";

export class SaleOrders {
  constructor(
    public id: number,
    public name: string,
    public invoiceNo:number,
    public grandTotal:number,
    public totalQuantity:number,
    public productId:number,
    public price:number,
    public createdAt:Date,
    public quantity:number,
    public productSales:ProductSales[],
  ) { }
}
