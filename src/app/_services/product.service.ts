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

const PRODUCT_API = 'http://localhost:8080/api/product/';

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
  constructor(private http: HttpClient, private token: TokenStorageService) {
  }

  fileExists(id: any, number: any): Observable<boolean> {
    return this.http.get("/assets/Productimages/" + id + "/" + number + ".jpg").pipe(map(() => true),
      catchError(() => of(false)));
  }

  productrequest: ProductRequest = new ProductRequest(0, '', '', 0, 0,0,0,0, null,null, false);

  updateProduct(product: Product): Observable<any> {

    this.productrequest.convert(product);
    var id = this.productrequest.id;
    var category = this.productrequest.category;
    var company = this.productrequest.company;
    var name = this.productrequest.name;
    var price = this.productrequest.price;
    var quantityItem = this.productrequest.quantityItem;
    var quantityBundle = this.productrequest.quantityBundle;
    var extraQuantity = this.productrequest.extraQuantity;
    var quantity = this.productrequest.quantity;
    var description = this.productrequest.description;

    return this.http.post(PRODUCT_API + 'updateProduct', {//productrequest
      id, category,company, price, name, description, quantityItem,quantityBundle,extraQuantity,quantity

    }, httpOptions);
  }

  addProduct(form: FormGroup): Observable<any> {
    var category = form.get('Category').value;
    var name = form.get('name').value;
    var price = form.get('price').value;
    var quantityItem = this.productrequest.quantityItem;
    var quantityBundle = this.productrequest.quantityBundle;
    var extraQuantity = this.productrequest.extraQuantity;
    var quantity =0;
    var description = form.get('description').value;
    return this.http.post(PRODUCT_API + 'addProduct', {
      category, price,
      name
      , description, quantityItem,quantityBundle,extraQuantity,quantity
    }, httpOptions);
  }

  updateProductImage(form: FormData): Observable<any> {
    return this.http.post(PRODUCT_API + 'updateProductImage', form);
  }

  findProduct(productrequest: ProductRequest): Observable<any> {

    var id = productrequest.id;
    var category = productrequest.category.name;
    var name = productrequest.name;
    var price = productrequest.price;
    var quantity = productrequest.quantity;
    var description = productrequest.description;
    console.log(productrequest)

    return this.http.post(PRODUCT_API + 'findProduct', {

      id, category, price, name, description, quantity
    }, httpOptions);
  }

  deleteProduct(product: Product): Observable<any> {
    var id = product.id
    return this.http.post(PRODUCT_API + 'deleteProduct', {id}, httpOptions);
  }

  getAllProducts(): Observable<any> {
    return this.http.post(PRODUCT_API + 'findProduct', {}, httpOptions);
  }

  getAllCategories(): Observable<any> {
    return this.http.post(PRODUCT_API + 'getAllCategories', {}, httpOptions);
  }

  getAllCompanies(): Observable<any> {
    return this.http.post(PRODUCT_API + 'getAllCompanies', {}, httpOptions);
  }

  updateCategory(category: Category): Observable<any> {
    var id = category.id;
    var name = category.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(PRODUCT_API + 'updateCategory', {id, name}, httpOptions);
  }

  addCategory(category: Category): Observable<any> {
    var name = category.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(PRODUCT_API + 'addCategory', {name}, httpOptions);
  }

  updateCompany(company: Company): Observable<any> {
    var id = company.id;
    var name = company.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(PRODUCT_API + 'updateCompany', {id, name}, httpOptions);
  }

  addCompany(company: Company): Observable<any> {
    var name = company.name
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(PRODUCT_API + 'addCompany', {name}, httpOptions);
  }

  deleteCompany(company: Company): Observable<any> {
    var id = company.id
    var name = company.name;
    return this.http.post(PRODUCT_API + 'deleteCompany', {id, name,}, httpOptions);
  }
  deleteCategory(category: Category): Observable<any> {
    var id = category.id
    var name = category.name;
    return this.http.post(PRODUCT_API + 'deleteCategory', {id, name,}, httpOptions);
  }

  getAllShoppingCarts(): Observable<any> {
    return this.http.post(PRODUCT_API + 'getAllShoppingCarts', {}, httpOptions);
  }

  getShoppingCart(userid: any): Observable<any> {
    return this.http.post(PRODUCT_API + 'getShoppingCart', {userid}, httpOptions);
  }

  updateShoppingCartItems(userid: any, cartItems: Cartitem[]): Observable<any> {
    cartItems.forEach(element => {
      element.shoppingcart = undefined
    });
    return this.http.post(PRODUCT_API + 'updateShoppingCartItems', {userid, cartItems}, httpOptions);
  }

  removeCartItem(userid: any, productid: number): Observable<any> {

    var username = this.token.getUsername();
    var password = this.token.getPassword();

    return this.http.post(PRODUCT_API + 'removeCartItem', {
      username, password, userid, productid
    }, httpOptions);
  }

  setCompletedDate(shoppingcart: any): Observable<any> {

    var userid = shoppingcart.user.id;
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(PRODUCT_API + 'setCompletedDate', {}, httpOptions);
  }

  setShippingDate(shoppingcart: any): Observable<any> {
    var userid = shoppingcart.user.id;
    // var price=form.get('price').value;
    // var description=form.get('description').value;
    return this.http.post(PRODUCT_API + 'setShippingDate', {userid}, httpOptions);
  }
}
