package com.vttp2022.BicycleParkingApp.repositories;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.vttp2022.BicycleParkingApp.models.mysql.Bookings;
import com.vttp2022.BicycleParkingApp.models.mysql.Favourites;
import com.vttp2022.BicycleParkingApp.models.mysql.UserDetails;

import static com.vttp2022.BicycleParkingApp.repositories.Queries.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Repository
public class UserParkingRepository {

  private static final Logger logger = LoggerFactory.getLogger(UserParkingRepository.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public Integer createUser(String name, String password, String email) throws Exception {
    return jdbcTemplate.update(SQL_INSERT_USER_DETAILS, name, password, email);
  }

  public UserDetails checkAndReturnUserCredentials(String email, String password) throws Exception {
    SqlRowSet response = jdbcTemplate.queryForRowSet(SQL_CHECK_USER_DETAILS, email, password);

    if(response.next())
      return UserDetails.create(response);
    else
      return null;
  }

  public List<Favourites> getUserFavourites(String email) throws Exception {
    final SqlRowSet response = jdbcTemplate.queryForRowSet(SQL_GET_FAVOURITES_ID_BY_USER_ID, email);

    List<String> parkingIdList = new LinkedList<>();

    while(response.next())
      parkingIdList.add(response.getString("parking_id"));
    
    if(parkingIdList.size()<=0)
      return null;
    
    List<Favourites> favouritesList = new LinkedList<>();
    
    for(String parkingId: parkingIdList) {
      final SqlRowSet resp = jdbcTemplate.queryForRowSet(SQL_GET_FAVOURITES, parkingId);

      while(resp.next())
        favouritesList.add(Favourites.create(resp));
    }

    if(favouritesList.size()<=0)
      return null;
    
    return favouritesList;
  }

  public Integer addExistingFavourites(String parkingId, String email) throws Exception {
    return jdbcTemplate.update(SQL_UPDATE_FAVOURITES_TO_USER_PARKING, email, parkingId);
  }

  public Integer deleteFavourites(String parkingId, String email) throws Exception {
    logger.info(parkingId);
    logger.info(email);
    return jdbcTemplate.update(SQL_DELETE_FAVOURITES_FROM_USER_PARKING, email, parkingId);
  }

  public Integer addNewFavourites(Favourites favourites, String email) throws Exception {
    Integer updateFav = jdbcTemplate.update(SQL_ADD_FAVOURITES, favourites.getParkingId(),
        favourites.getImage(), favourites.getDescription(),
        favourites.getRackType(), favourites.getRackCount(),
        favourites.getSheltered());
    
    Integer updateUserFav = jdbcTemplate.update(SQL_UPDATE_FAVOURITES_TO_USER_PARKING, email, 
        favourites.getParkingId());

    Integer updateFavCoord = jdbcTemplate.update(SQL_ADD_FAVOURITES_COORD, 
        favourites.getParkingId(), favourites.getCoord().getLat(), 
        favourites.getCoord().getLng());

    if(updateFav == 1 && updateUserFav == 1 && updateFavCoord == 1)
      return 1;
    else
      return 0;
  }

  public Favourites checkFavouritesExist(String image) throws Exception {
    SqlRowSet response = jdbcTemplate.queryForRowSet
        (SQL_CHECK_FAVOURITES_ALREADY_EXIST, image);

    if(response.next())
      return Favourites.create(response);
    else
      return null;
  }

  public Integer addBooking(Bookings b) {
    return jdbcTemplate.update(SQL_ADD_BOOKING, b.getEmail(), b.getBookingDate(), b.getImage(), b.getDescription(), b.getRackType(), b.getRackCount(), b.getSheltered());
  }

  public boolean checkIfBookingsExist(Bookings b) {
    final SqlRowSet response = jdbcTemplate.queryForRowSet(SQL_CHECK_IF_EXIST, b.getEmail(), b.getImage(), b.getBookingDate());

    if(response.next()) return true;

    return false;
  }

  public List<Bookings> getBookings(String email) {
    logger.info("Retrieving bookings >>> email");
    final SqlRowSet response = jdbcTemplate.queryForRowSet(SQL_GET_BOOKINGS_BY_USER_EMAIL, email);

    List<Bookings> bookingsList = new LinkedList<>();

    while(response.next()) {
      Bookings b = Bookings.create(response);
      LocalDate date = LocalDate.parse(b.getBookingDate(), DateTimeFormatter.ISO_DATE);
      LocalDate currentDate = LocalDate.now();
      boolean correctDate = date.isAfter(currentDate) || date.equals(currentDate);
      if(correctDate) bookingsList.add(b);
      else removeBooking(b.getEmail(), b.getBookingDate(), b.getDescription());
    }

    if(bookingsList.size()<=0){
      logger.info("No bookings found");
      return null;
    }

    return bookingsList;
  }

  public Integer removeBooking(String email, String date, String description) {
    return jdbcTemplate.update(SQL_REMOVE_BOOKING, email, date, description);
  }

  public List<Bookings> getTeleBookings(String email) {
    logger.info("Retrieving bookings >>> email");
    UserParkingRepository upRepo = new UserParkingRepository();
    upRepo.jdbcTemplate = new JdbcTemplate();
    final SqlRowSet response = upRepo.jdbcTemplate.queryForRowSet(SQL_GET_BOOKINGS_BY_USER_EMAIL, email);

    List<Bookings> bookingsList = new LinkedList<>();

    while(response.next()) {
      Bookings b = Bookings.create(response);
      LocalDate date = LocalDate.parse(b.getBookingDate(), DateTimeFormatter.ISO_DATE);
      LocalDate currentDate = LocalDate.now();
      boolean correctDate = date.isAfter(currentDate) || date.equals(currentDate);
      if(correctDate) bookingsList.add(b);
      else removeBooking(b.getEmail(), b.getBookingDate(), b.getDescription());
    }

    if(bookingsList.size()<=0){
      logger.info("No bookings found");
      return null;
    }

    return bookingsList;
  }
  
}
