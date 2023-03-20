import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Role} from "../../models/role.model";
import Swal from "sweetalert2";
import {Router} from "@angular/router";
import {UserService} from "../../_services/user.service";
import {Product} from "../../models/product.model";

@Component({
  selector: 'app-userform',
  templateUrl: './adduserform.component.html',
  styleUrls: ['./adduserform.component.css']
})
export class AdduserformComponent implements OnInit {
  form: any = {
    username: null,
    email: null,
    password: null,
    password2: null,
    role: Role
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';
  confirmPassError = false;

  errors: String;
  roles: Role[];


  constructor(
    public dialogRef: MatDialogRef<AdduserformComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Product, private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
    this.getAllRoles();
  }

  getAllRoles(): any {
    this.userService.getAllRoles().subscribe(
      data => {
        this.roles = data.roles;

      },
      err => {
        (err);
      }
    );
  }

  onSubmit(): void {
    console.log("submit")
    const {username, email, password} = this.form;
    debugger;
    this.userService.register(username, email, password).subscribe(
      data => {
        (data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        Swal.fire(
          'Congrats!',
          'New Account created successfully!',
          'success'
        )
        this.router.navigate(['/users']);
      },
      err => {
        this.errorMessage = err.error.message;
        console.log(this.errorMessage);
        this.isSignUpFailed = true;
      }
    );
  }
  validate(): void {
    this.confirmPassError = this.form.password != this.form.password2;
  }
  home(): void {
    this.router.navigate(['/dashboard']);
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
