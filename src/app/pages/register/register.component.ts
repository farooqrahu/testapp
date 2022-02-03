import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/_services/auth.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import Swal from "sweetalert2";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  form: any = {
    username: null,
    email: null,
    password: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(private authService: AuthService,private router: Router,   private tokenStorage: TokenStorageService) { }

  ngOnInit(): void {
    // if(this.tokenStorage.getUser()!=null)
    // this.router.navigate(['/user-profile']);
  }

  onSubmit(): void {
    const { username, email, password } = this.form;

    this.authService.register(username, email, password).subscribe(
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

  logout(): void {
    this.tokenStorage.signOut();
    this.router.navigate(['/login']);
    window.location.reload;
  }
  home(): void {
    this.router.navigate(['/dashboard']);
  }
}
