import {Category} from './category.model';
import {Product} from './product.model';
import {Company} from "./compnay.model";

export class ProductRequest {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public price: number,
    public retailPrice: number,
    public wholeSalePrice: number,
    public quantityItem: number,
    public quantityBundle: number,
    public extraQuantity: number,
    public quantity: number,
    public enableTQ: boolean,
    public wareHouseProduct: boolean,
    public userQuantityBundle: number,
    public userExtraQuantity: number,
    public userTotalQuantity: number,
    public category: Category,
    public company: Company,
    public images: boolean,
    private sort: string = "id",
    public sortdirection: string = "asc",
    public pagesize: number = 10,
    public pagenumber: number = 0
  ) {
  }

  convert(
    product: Product,
  ) {
    this.id = product.id
    this.name = product.name
    this.description = product.description
    this.price = product.price
    this.retailPrice = product.retailPrice
    this.wholeSalePrice = product.wholeSalePrice
    this.quantityItem = product.quantityItem
    this.quantityBundle = product.quantityBundle
    this.extraQuantity = product.extraQuantity
    this.quantity = product.quantity
    this.enableTQ = product.enableTQ
    this.wareHouseProduct = product.wareHouseProduct
    this.category = product.category
    this.company = product.company
    this.images = product.images
    this.pagenumber=product.pagenumber
    this.pagesize=product.pagesize
  }
}
