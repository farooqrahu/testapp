import {Sale} from "./sale.model";

export class Invoice {
  constructor(
    public _sales:Sale[],
    public _totalQuantity:number=0,
    public _grandTotal:number=0,
  ) {}


  get getSales(): Sale[] {
    return this._sales;
  }

  set setSales(value: Sale[]) {
    this._sales = value;
  }

  get totalQuantity(): number {
    return this._totalQuantity;
  }

  set setTotalQuantity(value: any) {
    this._totalQuantity = value;
  }

  get getGrandTotal(): number {
    return this._grandTotal;
  }

  set setGrandTotal(value:any) {
    this._grandTotal = value;
  }
}
