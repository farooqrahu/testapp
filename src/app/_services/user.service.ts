import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {ApiService} from "./ApiService";

// const this.apiService.getBaseUrl() = 'http://localhost:8080/api/test/';
// const AUTH_API = 'http://localhost:8080/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient, private apiService: ApiService) { }

  getPublicContent(): Observable<any> {
    return this.http.get(this.apiService.getBaseUrl() + 'auth/all', { responseType: 'text' });
  }

  getUserBoard(): Observable<any> {
    return this.http.get(this.apiService.getBaseUrl() + 'auth/user', { responseType: 'text' });
  }

  getModeratorBoard(): Observable<any> {
    return this.http.get(this.apiService.getBaseUrl() + 'auth/mod', { responseType: 'text' });
  }

  getAdminBoard(): Observable<any> {
    return this.http.get(this.apiService.getBaseUrl() + 'auth/admin', { responseType: 'text' });
  }

  getAllRoles(): Observable<any> {
    return this.http.post(this.apiService.getBaseUrl() + 'auth/getAllRoles', {}, httpOptions);
  }
  register(username: string,name: string, email: string, password: string,roleId: any): Observable<any> {
    // debugger;
    console.log("role.id"+roleId)
    return this.http.post(this.apiService.getBaseUrl() + 'auth/register', {
      username,
      name,
      email,
      password,
      roleId
    }, httpOptions);
  }
}
