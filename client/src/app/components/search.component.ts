import { HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { getMessaging, onMessage } from 'firebase/messaging';
import { Search } from '../model';
import { ParkingService } from '../parking.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit{

  @Input()
  search: Search | null=null

  searchForm!: FormGroup
  errorMessage: any =null
  name: string = sessionStorage.getItem('name') as string

  constructor(private fb: FormBuilder, private parkingSvc: ParkingService, private router: Router) { }

  ngOnInit(): void {
    sessionStorage.removeItem('errorMessage')
    this.searchForm = this.createSearch(this.search)
  }

  processSearch() {
    const search: Search = this.searchForm.value as Search
    this.storeSearchDetails(String(search.postal), String(search.radius*1000))
    this.parkingSvc.getResults(search.postal, search.radius)
      .then(result => {
        this.parkingSvc.results = result
        this.router.navigate(['/result'])
      })
      .catch(error => {
        if (error instanceof HttpErrorResponse) {
          const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message
          console.error('>>> error: ', error)
          this.errorMessage = String(constErrorMessage)
          this.listen()
        }
      })
  }

  urlSearch(radius: number, postal: number) {
    this.storeSearchDetails(String(postal), String(radius*1000))
    this.parkingSvc.getResults(postal, radius)
    .then(result => {
      console.info('>>> Search result: ', result)
      this.parkingSvc.results = result
      this.router.navigate(['/result'])
    })
    .catch(error => {
      if (error instanceof HttpErrorResponse) {
        const constErrorMessage = typeof error.error === 'string' ? error.error : error.error.message
        console.error('>>> error: ', error)
        this.errorMessage=String(constErrorMessage)
        this.listen()
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

  storeSearchDetails(postal: string, radius: string) {
    sessionStorage.setItem('postal', postal)
    sessionStorage.setItem('radius', radius)
  }

  private createSearch(search: Search | null=null): FormGroup {
    return this.fb.group({
      radius: this.fb.control(search?.radius? search.radius: '0.25', [Validators.required]),
      postal: this.fb.control(search?.postal? search.postal:'', [Validators.required])
    })
  }

}
