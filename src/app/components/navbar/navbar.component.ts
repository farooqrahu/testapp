import {Component, OnInit, ElementRef} from '@angular/core';
import {ROUTES} from '../sidebar/sidebar.component';
import {Location, LocationStrategy, PathLocationStrategy} from '@angular/common';
import {Router} from '@angular/router';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {AuthService} from 'src/app/_services/auth.service';
import {ProductService} from "../../_services/product.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  public focus;
  public listTitles: any[];
  public location: Location;

  constructor(location: Location, private element: ElementRef, private router: Router,
              private tokenStorage: TokenStorageService, private authService: AuthService, private productService: ProductService) {
    this.location = location;
    this.outOfStock();
  }

  currentUser = this.tokenStorage.getUser();
  outOfStockCounter: number;
  profileimg: string;

  ngOnInit() {
    this.listTitles = ROUTES.filter(listTitle => listTitle);
    // if (this.currentUser != null)
    //   this.profileimg = '/assets/userimages/' + this.currentUser.id + '/profile.jpg';
    this.loadImage();

  }

  public loadImage(): void {
    this.authService.getProfileImage().subscribe(
      data => {
        this.profileimg = data.user.file;
      },
      err => {
        (err);
      }
    );
  }

  public outOfStock(): void {
    this.productService.outOfStock().subscribe(
      data => {
        this.outOfStockCounter = data;
      },
      err => {
        (err);
      }
    );
  }

  getTitle() {
    var titlee = this.location.prepareExternalUrl(this.location.path());
    if (titlee.charAt(0) === '#') {
      titlee = titlee.slice(1);
    }

    for (var item = 0; item < this.listTitles.length; item++) {
      if (this.listTitles[item].path === titlee) {
        return this.listTitles[item].title;
      }
    }
    return 'Dashboard';
  }

  logout(): void {
    this.tokenStorage.signOut();
    this.router.navigate(['/login']);
    window.location.reload;

  }
}
