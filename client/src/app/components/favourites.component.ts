import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { getMessaging, onMessage } from 'firebase/messaging';
import { Bookings, Favourites } from '../model';
import { ParkingService } from '../parking.service';
import { Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-favourites',
  templateUrl: './favourites.component.html',
  styleUrls: ['./favourites.component.css']
})
export class FavouritesComponent implements OnInit{

  @Input()
  booking: Bookings | null=null

  favourites: Favourites[] = []
  favSize!: number 

  bookingForm!: FormGroup

  errorMessage:any = null;
  bookingErrorMessage:any = null;

  constructor(private router: Router, private parkingSvc: ParkingService, private location: Location, private fb: FormBuilder) { }

  ngOnInit(): void {
    
    if(this.getSessionUserEmail()===null) {
      this.setErrorMessage()
      this.router.navigate(['/'])
    }

    this.bookingForm = this.createBooking(this.booking)

    this.parkingSvc.getFavourites(this.getSessionUserEmail())
      .then((favourites) => {
        this.favourites = favourites
        this.favSize = this.favourites.length
        console.info('>>> favourites: ', this.favourites)
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message
          console.error('>>> error: ', error)
          this.errorMessage=String(constErrorMessage)
        }
      })
  }
  
  deleteFavourite(i: number) {
    let favourite = this.favourites[i]
    const parkingId = favourite.parkingId as string
    const email = this.getSessionUserEmail() as string
    console.info('Fav Component: To delete >>> ', parkingId)
    
    this.parkingSvc.removeFavourite(parkingId, email)
    .then(result => {
      console.info('>>> Delete favourites status: ', result)
      this.refreshPage()
    })
    .catch(error => {
      console.error('>>> Error: ', error)
    })
  }

  processBooking(i: number) {
    let favourite = this.favourites[i]
    const booking: Bookings = this.bookingForm.value as Bookings
    booking.description = favourite.description
    booking.image = favourite.image
    booking.rackCount = favourite.rackCount
    booking.rackType = favourite.rackType
    booking.sheltered = favourite.sheltered
    booking.email = this.getSessionUserEmail()
    console.info(booking)

    this.parkingSvc.addBooking(booking, sessionStorage.getItem('name') as string)
      .then(result => {
        console.info('>>> Booking status: ', result)
        this.router.navigate(['/bookings'])
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          console.info('HTTP Error Response')
          const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message;
          console.error('>>> error: ', error)
          if(String(constErrorMessage) === 'Invalid booking date' || 
            String(constErrorMessage) === 'Booking already exists' || 
            String(constErrorMessage) === 'Booking failed' ||
            String(constErrorMessage) === 'No racks available on selected date') {
            this.bookingErrorMessage=String(constErrorMessage)
            this.listen();
          }
        }
      })
  }

  getSessionUserEmail() {
    const email:string = sessionStorage.getItem('email') as string
    return email
  }

  setErrorMessage() {
    sessionStorage.setItem('errorMessage', 'Login to view your favourites')
  }

  refreshPage() {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['/favourites']);
    }); 
  }

  private createBooking(booking: Bookings | null = null): FormGroup {
    return this.fb.group({
      date: this.fb.control(booking?.bookingDate? booking.bookingDate: '', [Validators.required])
    })
  }

  listen() {
    const messaging = getMessaging();
    onMessage(messaging, (payload) => {
      console.log('Message received. ', payload);
      this.bookingErrorMessage=payload;
    });
  }

}
