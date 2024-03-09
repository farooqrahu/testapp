export class Sale {
  constructor(
    public id: number,
    public name: string,
    public customerName: string,
    public mobileNumber: string,
    public productId:number,
    public price:number,
    public retailPrice:number,
    public wholeSalePrice:number,
    public totalQuantitySale:number,
    public userQuantityBundle: number,
    public userExtraQuantity: number,
    public userTotalQuantity: number,
    private priceSelected: string,
  ) { }
}
