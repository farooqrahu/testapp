import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginComponent } from 'src/app/pages/login/login.component';
import { AuthService } from 'src/app/_services/auth.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';

declare interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
}
export var adminROUTES: RouteInfo[] = [
  { path: '/dashboard', title: 'Dashboard',  icon: 'ni-tv-2 text-primary', class: '' },
  //{ path: '/icons', title: 'Icons',  icon:'ni-planet text-blue', class: '' },
  // { path: '/maps', title: 'Maps',  icon:'ni-pin-3 text-orange', class: '' },
  { path: '/products', title: 'Products', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/productshisotry', title: 'Product History', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/categories', title: 'Categories', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/companies', title: 'Companies', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/sales', title: 'Create Sales', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/sale-orders', title: 'View Sales', icon: 'ni-bullet-list-67 text-red', class: '' },
  { path: '/users', title: 'Users', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/shopping-carts', title: 'Shopping Carts', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/reports', title: 'Reports', icon: 'ni-bullet-list-67 text-red', class: '' },
];
export var SALES_MAN_ROUT: RouteInfo[] = [
  // { path: '/sales', title: 'Create Sales', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/sale-orders', title: 'View Sales', icon: 'ni-bullet-list-67 text-red', class: '' },

];
export var ROUTES: RouteInfo[] = [
  // { path: '/products', title: 'Products', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/categories', title: 'Categories', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/companies', title: 'Companies', icon: 'ni-bullet-list-67 text-red', class: '' },

];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public menuItems: any[];
  public isCollapsed = true;
  public islogged: boolean;
  profileimg: string;

  constructor(private router: Router, private authService: AuthService, private tokenStorage: TokenStorageService) { }

  ngOnInit() {
    this.islogged = this.tokenStorage.getUser() != null;
    this.loadImage();
    if (this.islogged)
      if (this.tokenStorage.isAdmin()){
        this.menuItems = adminROUTES.filter(menuItem => menuItem);
      }
      else if (this.tokenStorage.isSalesMan()) {
        this.menuItems = SALES_MAN_ROUT.filter(menuItem => menuItem);
      }else  if (this.tokenStorage.isDEO()) {
        this.menuItems = ROUTES.filter(menuItem => menuItem);
      }
    else (this.logout())
    this.router.events.subscribe((event) => {
      this.isCollapsed = true;

    });
  }
  loadImage(): void {
    this.authService.getProfileImage().subscribe(
      data => {
        this.profileimg =data.user.file;
      },
      err => {
        (err);
      }
    );
  }

  logout(): void {
    this.islogged = false;
    this.tokenStorage.signOut();
      window.location.reload;

    this.router.navigate(['/login']);
  }
}
