import {Routes} from '@angular/router';
import {LoginComponent} from '../../pages/login/login.component';
import {ReportsComponent} from "../../pages/reports/reports.component";


export const AuthLayoutRoutes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'reports', component: ReportsComponent }

];
