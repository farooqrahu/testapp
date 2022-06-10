import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {RouterModule} from '@angular/router';
import {AppComponent} from './app.component';
import {AdminLayoutComponent} from './layouts/admin-layout/admin-layout.component';
import {AuthLayoutComponent} from './layouts/auth-layout/auth-layout.component';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDialogModule} from '@angular/material/dialog';
import {BrowserModule} from '@angular/platform-browser';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {MatInputModule} from "@angular/material/input";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatTableModule} from "@angular/material/table";
import {MatSortModule} from "@angular/material/sort";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {AppRoutingModule} from './app.routing';
import {ComponentsModule} from './components/components.module';
import {ProductformComponent} from './modal/productform/productform.component';
import {MatSelectModule} from '@angular/material/select';
import {CategoryformComponent} from './modal/categoryform/categoryform.component';
import {CompanyformComponent} from './modal/companyform/companyform.component';
import {SaleformComponent} from "./modal/saleform/saleform.component";
import {MessageboxComponent} from './modal/messagebox/messagebox.component';
import {UsersComponent} from './pages/users/users.component';
import {SalesComponent} from './pages/saleproducts/sales.component';
import {ShoppingCartsComponent} from './pages/shopping-carts/shopping-carts.component';
import {InvoiceComponent} from './modal/invoice/invoice.component';
import {EditshoppingcartComponent} from './modal/editshoppingcart/editshoppingcart.component';
import {AuthInterceptor} from "./_helpers/auth.interceptor";
import {BarcodeComponent} from "./modal/barcode/barcode.component";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatListModule} from "@angular/material/list";
import {MatTooltipModule} from "@angular/material/tooltip";
import {SaleOrdersComponent} from "./pages/viewsales/sale.orders.component";
import {SalesInvoiceComponent} from "./modal/saleinvoice/sales-invoice.component";
import {SalesListComponent} from "./modal/viewsalesform/sales-list.component";
import {ProductHistoryComponent} from "./pages/producthistory/producthistory.component";
import {AddProductFormComponent} from "./modal/addproductform/addproductform.component";
import {SaleListReturn} from "./modal/viewsalesform/sale-list-return";

@NgModule({
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    HttpClientModule,
    ComponentsModule,
    NgbModule,
    RouterModule,
    AppRoutingModule,
    MatSelectModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatCheckboxModule,
    MatListModule,
    MatTooltipModule,
  ],
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    AuthLayoutComponent,
    ProductformComponent,
    CategoryformComponent,
    CompanyformComponent,
    SaleformComponent,
    SalesInvoiceComponent,
    SalesListComponent,
    SaleOrdersComponent,
    BarcodeComponent,
    MessageboxComponent,
    UsersComponent,
    SalesComponent,
    ShoppingCartsComponent,
    InvoiceComponent,
    EditshoppingcartComponent,
    ProductHistoryComponent,
    AddProductFormComponent,
    SaleListReturn
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
