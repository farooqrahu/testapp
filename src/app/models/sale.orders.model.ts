import {ProductSales} from "./product.sale.model";

export class SaleOrders {
  constructor(
    public _id: number,
    public _customerName: string,
    public _mobileNumber: string,
    public _name: string,
    public _invoiceNo:number,
    public _grandTotal:number,
    public _totalQuantity:number,
    public _productId:number,
    public _price:number,
    public _createdAt:Date,
    public _quantity:number,
    public _productSales:ProductSales[],
  ) { }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get customerName(): string {
    return this._customerName;
  }

  set customerName(value: string) {
    this._customerName = value;
  }

  get mobileNumber(): string {
    return this._mobileNumber;
  }

  set mobileNumber(value: string) {
    this._mobileNumber = value;
  }

  get name(): string {
    return this._name;
  }

  set name(value: string) {
    this._name = value;
  }

  get invoiceNo(): number {
    return this._invoiceNo;
  }

  set invoiceNo(value: number) {
    this._invoiceNo = value;
  }

  get grandTotal(): number {
    return this._grandTotal;
  }

  set grandTotal(value: number) {
    this._grandTotal = value;
  }

  get totalQuantity(): number {
    return this._totalQuantity;
  }

  set totalQuantity(value: number) {
    this._totalQuantity = value;
  }

  get productId(): number {
    return this._productId;
  }

  set productId(value: number) {
    this._productId = value;
  }

  get price(): number {
    return this._price;
  }

  set price(value: number) {
    this._price = value;
  }

  get createdAt(): Date {
    return this._createdAt;
  }

  set createdAt(value: Date) {
    this._createdAt = value;
  }

  get quantity(): number {
    return this._quantity;
  }

  set quantity(value: number) {
    this._quantity = value;
  }

  get productSales(): ProductSales[] {
    return this._productSales;
  }

  set productSales(value: ProductSales[]) {
    this._productSales = value;
  }
}
