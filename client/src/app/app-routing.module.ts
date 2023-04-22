import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BookingsComponent } from './components/bookings.component';
import { FavouritesComponent } from './components/favourites.component';
import { LoginComponent } from './components/login.component';
import { RegisterComponent } from './components/register.component';
import { ResultComponent } from './components/result.component';
import { SearchComponent } from './components/search.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
  { path: 'search', component: SearchComponent },
  { path: 'result', component: ResultComponent},
  { path: 'favourites', component: FavouritesComponent },
  { path: 'bookings', component: BookingsComponent},
  { path: '**', redirectTo: 'search', pathMatch: 'full' }
];

@NgModule({
  //imports: [RouterModule.forRoot(routes)],
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
