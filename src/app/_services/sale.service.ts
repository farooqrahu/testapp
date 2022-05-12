import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Sale} from "../models/sale.model";
import {ProductRequest} from "../models/productrequest.model";
import {SaleOrders} from "../models/sale.orders.model";

const PRODUCT_SALE_API = 'http://localhost:8080/api/sale/';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};
const HttpUploadOptions = {
  headers: new HttpHeaders({})
}

@Injectable({
  providedIn: 'root'
})
export class SaleService {
  constructor(private http: HttpClient, private token: TokenStorageService) {
  }

  addProduct(data: Sale[]): Observable<any> {
    // let json = JSON.stringify(data);
    return this.http.post(PRODUCT_SALE_API + 'submitSaleOrder', {data}, httpOptions);
  }
  findProduct(productrequest: ProductRequest): Observable<any> {

    var id = productrequest.id;
    var category = productrequest.category.name;
    var name = productrequest.name;
    var price = productrequest.price;
    var quantity = productrequest.quantity;
    var description = productrequest.description;
    console.log(productrequest)

    return this.http.post(PRODUCT_SALE_API + 'findOrders', {
      id, category, price, name, description, quantity
    }, httpOptions);
  }

  getAllOrders(): Observable<any> {
    return this.http.post(PRODUCT_SALE_API + 'findOrders', {}, httpOptions);
  }

  returnProductSale(saleOrders:SaleOrders): Observable<any> {
    // var id = saleOrders.id;
    var data = saleOrders.productSales;
    var id = saleOrders.id;
    // let json = JSON.stringify(data);
    return this.http.post(PRODUCT_SALE_API + 'returnProductSale', {data,id}, httpOptions);
  }

}
