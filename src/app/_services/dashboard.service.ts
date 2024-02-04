import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ApiService} from "./ApiService";

// const PRODUCT_API = 'http://localhost:8080/api/dashboard/';

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
  constructor(private http: HttpClient, private token: TokenStorageService, private apiService: ApiService) {
  }


  getTotalSales(): Observable<any> {
    return this.http.get(this.apiService.getBaseUrl() + 'dashboard/totalSales', { responseType: 'text' });
  }

}
