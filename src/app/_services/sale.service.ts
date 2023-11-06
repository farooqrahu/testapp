import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Sale} from "../models/sale.model";
import {ProductRequest} from "../models/productrequest.model";
import {SaleOrders} from "../models/sale.orders.model";
import {Invoice} from "../models/invoice.model";

const PRODUCT_SALE_API = 'http://localhost:8080/api/sale/';
const CUSTOMER_API = 'http://localhost:8080/api/customer/';

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

  submitSaleOrder(productSaleList: Invoice,customerId?,customerName?,mobileNumber?): Observable<any> {
    let data = productSaleList._sales;
    let grandTotal = productSaleList._grandTotal;
    return this.http.post(PRODUCT_SALE_API + 'submitSaleOrder', {data,grandTotal,customerId,customerName,mobileNumber}, httpOptions);
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
  getAllCustomers(): Observable<any> {
    return this.http.get(CUSTOMER_API + 'getAllCustomers');
  }
  findReturnOrdersByInvoiceNo(invoiceNo: number): Observable<any> {
    return this.http.post(PRODUCT_SALE_API + 'findReturnOrdersByInvoiceNo', {invoiceNo}, httpOptions);
  }

  returnProductSale(saleOrders:SaleOrders): Observable<any> {
    // var id = saleOrders.id;
    var data = saleOrders.productSales;
    var id = saleOrders.id;
    // let json = JSON.stringify(data);
    return this.http.post(PRODUCT_SALE_API + 'returnProductSale', {data,id}, httpOptions);
  }
  findCustomerByMobileNumber(mobileNumber?): Observable<any> {
    return this.http.post(PRODUCT_SALE_API + 'findCustomerByMobileNumber', {mobileNumber}, httpOptions);
  }
}
