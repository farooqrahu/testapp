import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl; // Replace this with your backend URL

  constructor() { }

  getBaseUrl(): string {
    this.baseUrl = environment.baseUrl;
    return this.baseUrl;
  }
}
