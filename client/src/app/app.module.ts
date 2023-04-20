import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login.component';
import { SearchComponent } from './components/search.component';
import { FavouritesComponent } from './components/favourites.component';
import { BookingsComponent } from './components/bookings.component';

import { environment } from "../environments/environment";
import { initializeApp } from "firebase/app";
import { ResultComponent } from './components/result.component';
import { RegisterComponent } from './components/register.component';
import { ParkingService } from './parking.service';
import { ReactiveFormsModule } from '@angular/forms';
initializeApp(environment.firebase);

@NgModule({
  declarations: [
    AppComponent, LoginComponent,
    SearchComponent, FavouritesComponent,
    BookingsComponent, ResultComponent,
    RegisterComponent
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [ParkingService],
  bootstrap: [AppComponent]
})
export class AppModule { }
