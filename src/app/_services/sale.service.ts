import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Sale} from "../models/sale.model";
import {ProductRequest} from "../models/productrequest.model";
import {SaleOrders} from "../models/sale.orders.model";
import {Invoice} from "../models/invoice.model";
import {ApiService} from "./ApiService";

// const PRODUCT_SALE_API ='${this.apiService.getBaseUrl()}/sale/';
// const CUSTOMER_API = '${this.apiService.getBaseUrl()}/customer/';

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
  constructor(private http: HttpClient, private token: TokenStorageService, private apiService: ApiService) {
  }

  submitSaleOrder(productSaleList: Invoice,customerId?,customerName?,mobileNumber?): Observable<any> {
    let data = productSaleList._sales;
    let grandTotal = productSaleList._grandTotal;
    return this.http.post(this.apiService.getBaseUrl() + 'sale/submitSaleOrder', {data,grandTotal,customerId,customerName,mobileNumber}, httpOptions);
  }
  findProduct(productrequest: ProductRequest): Observable<any> {

    var id = productrequest.id;
    var category = productrequest.category.name;
    var name = productrequest.name;
    var price = productrequest.price;
    var quantity = productrequest.quantity;
    var description = productrequest.description;
    console.log(productrequest)

    return this.http.post(this.apiService.getBaseUrl() + 'sale/findProduct', {

      id, category, price, name, description, quantity
    }, httpOptions);
  }

  getAllOrders(productrequest: ProductRequest): Observable<any> {
    var name = productrequest.name;
    var pagenumber = productrequest.pagenumber;
    var pagesize = productrequest.pagesize;

    return this.http.post(this.apiService.getBaseUrl() + 'sale/findOrders', {pagenumber,pagesize,name}, httpOptions);
  }

  getProductReport(productrequest: ProductRequest): Observable<any> {
    var name = productrequest.name;
    var pagenumber = productrequest.pagenumber;
    var pagesize = productrequest.pagesize;

    return this.http.post(this.apiService.getBaseUrl() + 'report/getProductReport', {pagenumber,pagesize,name}, httpOptions);
  }
  getAllCustomers(): Observable<any> {
    return this.http.get(this.apiService.getBaseUrl() + 'customer/getAllCustomers');
  }
  findReturnOrdersByInvoiceNo(invoiceNo: number): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'sale/findReturnOrdersByInvoiceNo', {invoiceNo}, httpOptions);
  }

  returnProductSale(saleOrders:SaleOrders): Observable<any> {
    // var id = saleOrders.id;
    var data = saleOrders.productSales;
    var id = saleOrders.id;
    // let json = JSON.stringify(data);
    return this.http.post(this.apiService.getBaseUrl() + 'sale/returnProductSale', {data,id}, httpOptions);
  }
  findCustomerByMobileNumber(mobileNumber?): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'customer/findCustomerByMobileNumber', {mobileNumber}, httpOptions);
  }
}
