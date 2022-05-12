import {Routes} from '@angular/router';

import {DashboardComponent} from '../../pages/dashboard/dashboard.component';
import {UserProfileComponent} from '../../pages/user-profile/user-profile.component';
import {ProductsComponent} from '../../pages/products/products.component';
import {UsersComponent} from 'src/app/pages/users/users.component';
import {ShoppingCartsComponent} from 'src/app/pages/shopping-carts/shopping-carts.component';
import {RegisterComponent} from "../../pages/register/register.component";
import {CategoriesComponent} from "../../pages/categories/categories.component";
import {CompaniesComponent} from "../../pages/companies/companies.component";
import {SalesComponent} from "../../pages/saleproducts/sales.component";
import {SaleOrdersComponent} from "../../pages/viewsales/sale.orders.component";

export const AdminLayoutRoutes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: 'user-profile', component: UserProfileComponent },
  { path: 'products', component: ProductsComponent },
  { path: 'categories', component: CategoriesComponent },
  { path: 'companies', component: CompaniesComponent },
  { path: 'sales', component: SalesComponent },
  { path: 'sale-orders', component: SaleOrdersComponent },
  { path: 'users', component: UsersComponent },
  { path: 'shopping-carts', component: ShoppingCartsComponent },
  { path: 'register', component: RegisterComponent }
];
