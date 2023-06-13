import { Role } from './models/role.model';
import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from './_services/token-storage.service';
import {NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterEvent} from "@angular/router";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  private roles: Role[] = [];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  username?: string;
  password: string;
  email: string;
  name: string;
  age: number;
  surname: string;
  address: string;
  city: string;
  country: string;
  job: string;
  description: string;
  public showOverlay = true;
  constructor(private tokenStorageService: TokenStorageService, private router: Router) {
    router.events.subscribe((event: RouterEvent) => {
      this.navigationInterceptor(event)
    })
  }
  // Shows and hides the loading spinner during RouterEvent changes
  navigationInterceptor(event: RouterEvent): void {
    if (event instanceof NavigationStart) {
      this.showOverlay = true;
    }
    if (event instanceof NavigationEnd) {
      this.showOverlay = false;
    }

    // Set loading state to false in both of the below events to hide the spinner in case a request fails
    if (event instanceof NavigationCancel) {
      this.showOverlay = false;
    }
    if (event instanceof NavigationError) {
      this.showOverlay = false;
    }
  }
  ngOnInit(): void {
    this.isLoggedIn = !!this.tokenStorageService.getToken();

    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser();
      this.roles = user.roles;
      this.email = user.email;
      this.name = user.name;
      this.age = user.age;
      this.password = user.password

      this.username = user.username;
    }
  }

  logout(): void {
    this.tokenStorageService.signOut();
    this.router.navigate(['/login']);
  }
}
