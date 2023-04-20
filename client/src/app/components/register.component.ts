import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { getMessaging, onMessage } from 'firebase/messaging';
import { RegisterUser } from '../model';
import { ParkingService } from '../parking.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit{

  @Input()
  registerUser: RegisterUser | null=null

  registerForm!: FormGroup

  errorMessage:any = null;

  constructor(private fb: FormBuilder, private parkingSvc: ParkingService, private router: Router) { }

  ngOnInit(): void {
      this.registerForm = this.registration(this.registerUser)
  }

  processRegistration() {
    const registerUser: RegisterUser = this.registerForm.value as RegisterUser
    
    this.parkingSvc.registerNewUser(registerUser.name, registerUser.email, registerUser.password, registerUser.confirmPassword)
      .then(result => {
        console.info('>>> Registration status: ', result)
        sessionStorage.setItem('name', registerUser.name)
        sessionStorage.setItem('email', registerUser.email)
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message;
          console.error('>>> error: ', error)
          this.errorMessage=String(constErrorMessage)
          this.listen();
        }
      })
  }

  listen() {
    const messaging = getMessaging();
    onMessage(messaging, (payload) => {
      console.log('Message received. ', payload);
      this.errorMessage=payload;
    });
  }

  private registration(registerUser: RegisterUser | null=null): FormGroup {
    return this.fb.group({
      name: this.fb.control(registerUser?.name? registerUser.name: '', [Validators.required]),
      email: this.fb.control(registerUser?.email? registerUser.email: '', [Validators.required]),
      password: this.fb.control(registerUser?.password? registerUser.password: '', [Validators.required]),
      confirmPassword: this.fb.control(registerUser?.confirmPassword? registerUser.confirmPassword: '', [Validators.required]),
    })
  }

}
