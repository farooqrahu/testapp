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

const PRODUCT_API = 'http://localhost:8080/api/dashboard/';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};
const HttpUploadOptions = {
  headers: new HttpHeaders({})
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  constructor(private http: HttpClient, private token: TokenStorageService) {
  }


  getTotalSales(): Observable<any> {
    return this.http.get(PRODUCT_API + 'totalSales', { responseType: 'text' });
  }

}
