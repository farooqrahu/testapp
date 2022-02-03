import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {Router} from '@angular/router';
import {User} from 'src/app/models/user';
import {TokenStorageService} from 'src/app/_services/token-storage.service';
import {AuthService} from './../../_services/auth.service';
import Swal from "sweetalert2";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  columnsToDisplay = ["id", "username", "fullname", "email", "status", "roles", "action"];
  users: User[] = null;
  dataSource: MatTableDataSource<User> = null;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private token: TokenStorageService, private authService: AuthService, private router: Router) {
  }

  ngOnInit() {
    if (!this.token.isAdmin()) {
      this.token.signOut();
    }
    this.getAllUsers()
  }

  getAllUsers() {
    this.authService.getAllUsers().subscribe(
      data => {
        this.users = data.users;
        this.dataSource = new MatTableDataSource(this.users);
        setTimeout(() => {
          this.dataSource.sort = this.sort;
          this.dataSource.paginator = this.paginator;
        });
      },
      err => {
        (err);
      }
    );
  }

  public doFilter = (value: string, type: String) => {
    switch (type) {
      case 'user':
        this.dataSource.filter = value.trim().toLocaleLowerCase();

    }
  }

  deleteUser(user: User): any {


    Swal.fire({
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
      if (result.isConfirmed) {
        this.authService.deleteUser(user).subscribe(
          data => {
            this.getAllUsers();
            Swal.fire(
              'Deleted!',
              'Account has been deleted.',
              'success'
            )
          },
          err => {
            console.log(err);
          }
        );
      }
    })

  }

}
