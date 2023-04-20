export interface Search {
  radius: number
  postal: number
}

export interface LoginAttempt {
  email: string
  password: string
}

export interface RegisterUser {
  name: string
  email: string
  password: string
  confirmPassword: string
}

export interface Favourites {
  parkingId: string
  image: string
  description: string
  rackType: string
  rackCount: number
  sheltered: string
}

export interface FavouritesCoord {
  lat: number
  lng: number
}

export interface Results {
  parkingId: string
  image: string
  description: string
  distance: string
  lat: number
  lng: number
  rackType: string
  availability: number
  rackCount: number
  sheltered: string
}

export interface UserDetails {
  userDetailsId: number
  name: string
  password: string
  email: string
}

export interface Bookings {
  email: string
  bookingDate: string
  image: string
  description: string
  rackType: string
  rackCount: number
  sheltered: string
}

export interface SBResponse {
  status: string
}