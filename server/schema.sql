create database userparking;

use userparking;

create table user_details (
  user_details_id int auto_increment not null,
  name varchar(128) not null,
  password varchar(256) not null,
  email varchar(128) not null,

  unique (email),
  
  primary key(user_details_id)
);

create table favourites (
  parking_id varchar(128) not null,
  image varchar(512) not null,
  description varchar(128) not null,
  rack_type varchar(128) not null,
  rack_count int not null,
  sheltered varchar(128) not null,

  unique (image),

  primary key(parking_id)
);

create table bookings (
  booking_id int auto_increment not null,
  email varchar(128) not null,
  booking_date varchar(128) not null,
  image varchar(512) not null,
  description varchar(128) not null,
  rack_type varchar(128) not null,
  rack_count int not null,
  sheltered varchar(128) not null,

  unique key user_date_location (email, booking_date, image),

  primary key(booking_id),
  constraint fkey_email
    foreign key(email) references user_details(email)
);

create table favourites_coord (
  coord_id int auto_increment not null,
  parking_id varchar(128) not null,
  lat decimal(64,16) not null,
  lng decimal(64,16) not null,

  unique (parking_id),

  primary key(coord_id),
  constraint fkey_parking_id
    foreign key(parking_id) references favourites(parking_id)
);

create table user_parking (
  id int auto_increment not null,
  email varchar(128) not null,
  parking_id varchar(128) not null,

  unique key user_parking_id (email, parking_id),

  primary key(id),
  constraint fk_user_email
    foreign key(email) references user_details(email),
  constraint fk_parking_id
    foreign key(parking_id) references favourites(parking_id)
);