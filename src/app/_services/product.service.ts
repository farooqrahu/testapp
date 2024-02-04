import {Cartitem} from './../models/cartitem.model';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {catchError, map, Observable, of} from 'rxjs';
import {Category} from './../models/category.model';
import {Product} from './../models/product.model';
import {ProductRequest} from './../models/productrequest.model';
import {NumberSymbol} from '@angular/common';
import {Company} from "../models/compnay.model";
import {ApiService} from "./ApiService";

// const this.apiService.getBaseUrl() = 'http://localhost:8080/api/product/';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};
const HttpUploadOptions = {
  headers: new HttpHeaders({})
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  constructor(private http: HttpClient, private token: TokenStorageService, private apiService: ApiService) {
  }

  fileExists(id: any, number: any): Observable<boolean> {
    return this.http.get("/assets/Productimages/" + id + "/" + number + ".jpg").pipe(map(() => true),
      catchError(() => of(false)));
  }

  productrequest: ProductRequest = new ProductRequest(0, '', '',0, 0, 0,0,0,0, false,false,0,0,0,null,null, false);

  updateProduct(product: Product): Observable<any> {

    this.productrequest.convert(product);
    var id = this.productrequest.id;
    var category = this.productrequest.category;
    var company = this.productrequest.company;
    var name = this.productrequest.name;
    var price = this.productrequest.price;
    var wholeSalePrice = this.productrequest.wholeSalePrice;
    var quantityItem = this.productrequest.quantityItem;
    var quantityBundle = this.productrequest.quantityBundle;
    var extraQuantity = this.productrequest.extraQuantity;
    var quantity = this.productrequest.quantity;
    var description = this.productrequest.description;
    var enableTQ =this.productrequest.enableTQ;
    var wareHouseProduct =this.productrequest.wareHouseProduct;

    return this.http.post(this.apiService.getBaseUrl() + 'product/updateProduct', {//productrequest
      id, category,company, price,wholeSalePrice, name, description, quantityItem,quantityBundle,extraQuantity,quantity
    ,enableTQ,wareHouseProduct
    }, httpOptions);
  }

  addProduct(form: FormGroup): Observable<any> {
    var category = form.get('Category').value;
    var name = form.get('name').value;
    var price = form.get('price').value;
    var quantityItem = this.productrequest.quantityItem;
    var quantityBundle = this.productrequest.quantityBundle;
    var extraQuantity = this.productrequest.extraQuantity;
    var quantity =this.productrequest.quantity;
    var enableTQ =this.productrequest.enableTQ;
    var description = form.get('description').value;
    return this.http.post(this.apiService.getBaseUrl() + 'product/addProduct', {
      category, price,
      name
      , description, quantityItem,quantityBundle,extraQuantity,quantity,enableTQ
    }, httpOptions);
  }

  updateProductImage(form: FormData): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'product/updateProductImage', form);
  }

  findProduct(productrequest: ProductRequest): Observable<any> {
    console.log("productrequest")
    debugger;
    console.log(productrequest)
    // var id = productrequest.id;
    // var category = productrequest.category;
    var name = productrequest.name;
    // var price = productrequest.price;
    // var quantity = productrequest.quantity;
    // var description = productrequest.description;
    var pagenumber = productrequest.pagenumber;
    var pagesize = productrequest.pagesize;

    console.log(productrequest)

    return this.http.post(this.apiService.getBaseUrl() + 'product/findProduct', {

     name,pagenumber,pagesize
    }, httpOptions);
  }

  findProductHistory(productrequest: ProductRequest): Observable<any> {
debugger;
    var id = productrequest.id;
    // var category = productrequest.category.name;
    var name = productrequest.name;
    var price = productrequest.price;
    var quantity = productrequest.quantity;
    var description = productrequest.description;
    var pagenumber = productrequest.pagenumber;
    var pagesize = productrequest.pagesize;
    console.log(productrequest)

    return this.http.post(this.apiService.getBaseUrl() + 'product/findProductHistory', {

      id, price, name, description, quantity,pagenumber,pagesize
    }, httpOptions);
  }

  deleteProduct(product: Product): Observable<any> {
    var id = product.id
    return this.http.post(this.apiService.getBaseUrl() + 'product/deleteProduct', {id}, httpOptions);
  }

  getAllProducts(): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'product/findProduct', {}, httpOptions);
  }

  findProductOutOfStock(productrequest: ProductRequest): Observable<any> {
    // var category = productrequest.category.name;
    var name = productrequest.name;
    var pagenumber = productrequest.pagenumber;
    var pagesize = productrequest.pagesize;
    console.log(productrequest)

    return this.http.post(this.apiService.getBaseUrl() + 'product/findByNameInStock', {name,pagenumber,pagesize}, httpOptions);
  }

  getAllProductHistory(): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'product/findProductHistory', {}, httpOptions);
  }

  getAllCategories(): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'product/getAllCategories', {}, httpOptions);
  }

  getAllCompanies(): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'product/getAllCompanies', {}, httpOptions);
  }

  updateCategory(category: Category): Observable<any> {
    var id = category.id;
    var name = category.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(this.apiService.getBaseUrl() + 'product/updateCategory', {id, name}, httpOptions);
  }

  addCategory(category: Category): Observable<any> {
    var name = category.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(this.apiService.getBaseUrl() + 'product/addCategory', {name}, httpOptions);
  }

  updateCompany(company: Company): Observable<any> {
    var id = company.id;
    var name = company.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(this.apiService.getBaseUrl() + 'product/updateCompany', {id, name}, httpOptions);
  }

  addCompany(company: Company): Observable<any> {
    var name = company.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(this.apiService.getBaseUrl() + 'product/addCompany', {name}, httpOptions);
  }

  deleteCompany(company: Company): Observable<any> {
    var id = company.id
    var name = company.name;
    return this.http.post(this.apiService.getBaseUrl() + 'product/deleteCompany', {id, name,}, httpOptions);
  }
  deleteCategory(category: Category): Observable<any> {
    var id = category.id
    var name = category.name;
    return this.http.post(this.apiService.getBaseUrl() + 'product/deleteCategory', {id, name,}, httpOptions);
  }

  getAllShoppingCarts(): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'product/getAllShoppingCarts', {}, httpOptions);
  }

  getShoppingCart(userid: any): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'product/getShoppingCart', {userid}, httpOptions);
  }

  updateShoppingCartItems(userid: any, cartItems: Cartitem[]): Observable<any> {
    cartItems.forEach(element => {
      element.shoppingcart = undefined
    });
    return this.http.post(this.apiService.getBaseUrl() + 'product/updateShoppingCartItems', {userid, cartItems}, httpOptions);
  }

  removeCartItem(userid: any, productid: number): Observable<any> {

    var username = this.token.getUsername();
    var password = this.token.getPassword();

    return this.http.post(this.apiService.getBaseUrl() + 'product/removeCartItem', {
      username, password, userid, productid
    }, httpOptions);
  }

  setCompletedDate(shoppingcart: any): Observable<any> {

    var userid = shoppingcart.user.id;
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(this.apiService.getBaseUrl() + 'product/setCompletedDate', {}, httpOptions);
  }

  setShippingDate(shoppingcart: any): Observable<any> {
    var userid = shoppingcart.user.id;
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(this.apiService.getBaseUrl() + 'product/setShippingDate', {userid}, httpOptions);
  }
}
