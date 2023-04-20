package com.vttp2022.BicycleParkingApp.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vttp2022.BicycleParkingApp.models.ErrorResponse;
import com.vttp2022.BicycleParkingApp.models.mongodb.BookingAvailability;
import com.vttp2022.BicycleParkingApp.models.mysql.Bookings;
import com.vttp2022.BicycleParkingApp.models.mysql.Favourites;
import com.vttp2022.BicycleParkingApp.models.mysql.UserDetails;
import com.vttp2022.BicycleParkingApp.models.parking.Parkings;
import com.vttp2022.BicycleParkingApp.models.parking.Query;
import com.vttp2022.BicycleParkingApp.models.parking.Value;
import com.vttp2022.BicycleParkingApp.models.postal.Postal;
import com.vttp2022.BicycleParkingApp.models.postal.Results;
import com.vttp2022.BicycleParkingApp.repositories.MongoRepository;
import com.vttp2022.BicycleParkingApp.repositories.UserParkingRepository;
import com.vttp2022.BicycleParkingApp.services.EmailSenderService;
import com.vttp2022.BicycleParkingApp.services.ParkingAPIService;
import com.vttp2022.BicycleParkingApp.services.PostalAPIService;
import com.vttp2022.BicycleParkingApp.services.WebNotificationService;
import com.vttp2022.BicycleParkingApp.utilities.CleanString;
import com.vttp2022.BicycleParkingApp.utilities.SortByDistance;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path="/api", 
produces = MediaType.APPLICATION_JSON_VALUE)
public class AngularController {

  private static final Logger logger = LoggerFactory.getLogger(AngularController.class);

  @Autowired
  private UserParkingRepository upRepo;
  @Autowired
  private MongoRepository mongoRepo;
  @Autowired
  private ParkingAPIService parkingSvc;
  @Autowired
  private PostalAPIService postalSvc;
  @Autowired
  private WebNotificationService wnSvc;
  @Autowired
  private EmailSenderService emailSvc;

  //Login
  @GetMapping(path="/user")
  public ResponseEntity<?> validifyUser(@RequestHeader(value = "email", required = true) String email, @RequestHeader(value = "password", required = true) String password) throws Exception {
    UserDetails ud = new UserDetails();
    ud = upRepo.checkAndReturnUserCredentials(email, password);

    if(ud == null){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Invalid email/password");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    JsonObject udJson = ud.toUserDetails();
    return ResponseEntity.ok(udJson.toString());
  }

  //Register
  @PostMapping(path="/register")
  public ResponseEntity<?> registration(@RequestHeader(value = "name", required = true) String name, @RequestHeader(value = "email", required = true) String email, @RequestHeader(value = "password", required = true) String password, @RequestHeader(value = "confirmPassword", required = true) String confirmPassword) throws Exception {
    String cleanName = CleanString.cleanName(name);

    if(!password.equals(confirmPassword)) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Passwords do not match");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    Integer success = upRepo.createUser(cleanName, password, email.toLowerCase());

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Email already exist");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
  }

  //Favourites
  @GetMapping(path="/favourites") 
  public ResponseEntity<?> getFavourites(@RequestParam(value = "email", required = true) String email) throws Exception {

    if(upRepo.getUserFavourites(email) == null) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("You have 0 saved bicycle parking location(s)");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    upRepo.getUserFavourites(email).stream()
        .forEach(f -> {
          arrayBuilder.add(f.toFavourites());
        });

    return ResponseEntity.ok(arrayBuilder.build().toString());
  }
  
  @PostMapping(path="/savefav")
  public ResponseEntity<?> addFavourites(@RequestBody String result, @RequestHeader(value = "email", required = true) String email, @RequestHeader(value = "token", required = true) String token) throws Exception {

    Favourites favourites = Favourites.createJson(result);
    Favourites checkFavourites = upRepo.checkFavouritesExist(favourites.getImage());
    Integer success;

    if(checkFavourites != null) {
      success = upRepo.addExistingFavourites(checkFavourites.getParkingId(), email);
    } else {
      success = upRepo.addNewFavourites(favourites, email);
    }

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Failed to save to favourites");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      wnSvc.sendNotification(token, "favourites");
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
  }

  @DeleteMapping(path = "/rmvfav")
  public ResponseEntity<?> removeFavourites(@RequestHeader(value = "email", required = true) String email, @RequestHeader(value = "parkingId", required = true) String parkingId) throws Exception {
    logger.info("remove favourite");
    logger.info(parkingId);
    Integer success = upRepo.deleteFavourites(parkingId, email);

    if(success == 0) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Failed to remove from favourites");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok("{\"status\":\"success\"}");
    }
  }

  //Search | results
  @GetMapping("/results")
  public ResponseEntity<?> searchParking(@RequestParam(value = "postal", required = true) String postal, @RequestParam(value = "radius", required = true) String radius) throws Exception{

    Query q = new Query();
    Optional<Postal> optPostal = postalSvc.getPostalDetails(Integer.parseInt(postal));

    if(Postal.getFound() == 0){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Postal code invalid, please key in a valid postal code");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    List<Results> results = Postal.getResults();
    if(results.size() >= 1){
      q.setLat(results.get(0).getLatitude());
      q.setLng(results.get(0).getLongitude());
    }
    q.setRadius(Double.parseDouble(radius));
    Optional<Parkings> optParking = parkingSvc.findParking(q);

    if(optParking.isEmpty()){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("No bicycle parking bays found");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    Collections.sort(Parkings.getValue(), new SortByDistance());
    List<Value> val = Parkings.getValue();
    for(Value v: val){
      Integer a = mongoRepo.getCurrentAvailability(v.getImg());
      if(a.equals(-10)) v.setAvailability(v.getRackCount());
      else v.setAvailability(a);
    }
    
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    val.stream()
        .forEach(f -> {
          arrayBuilder.add(f.toResults());
        });
    
    return ResponseEntity.ok(arrayBuilder.build().toString());
  }

  //Bookings
  @PostMapping(path="/savebooking")
  public ResponseEntity<?> addBooking(@RequestBody String booking, @RequestHeader(value = "token", required = true) String token, @RequestHeader(value = "name", required = true) String name) throws Exception {

    Bookings b = Bookings.createJson(booking);

    LocalDate date = LocalDate.parse(b.getBookingDate(), DateTimeFormatter.ISO_DATE);
    LocalDate currentDate = LocalDate.now();
    boolean correctDate = date.isAfter(currentDate) || date.equals(currentDate);
    if(!correctDate) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Invalid booking date");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    if(upRepo.checkIfBookingsExist(b)) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("Booking already exists");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    //Mongo update availability
    BookingAvailability ba = new BookingAvailability();
    ba.setDate(b.getBookingDate());
    ba.setImage(b.getImage());
    Integer availability = mongoRepo.getAvailability(ba.getImage(), ba.getDate());
    logger.info("Integer availability >>> "+availability);
    if(availability.equals(0)){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("No racks available on selected date");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      if(availability.equals(-10)) {
        ba.setAvailability(b.getRackCount()-1);
        Integer success = upRepo.addBooking(b);

        if(success.equals(0)) {
          ErrorResponse errorResponse = new ErrorResponse();
          errorResponse.setMessage("Booking failed");
          return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        mongoRepo.insertAvailability(ba);
      }else {
        Integer availabilityToUpdate = availability - 1;
        ba.setAvailability(availabilityToUpdate);
        Integer success = upRepo.addBooking(b);

        if(success == 0) {
          ErrorResponse errorResponse = new ErrorResponse();
          errorResponse.setMessage("Booking failed");
          return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        mongoRepo.updateAvailability(ba);
      }
    }

    wnSvc.sendNotification(token, "bookings");
    emailSvc.bookingConfirmationEmail(name, b);
    return ResponseEntity.ok("{\"status\":\"success\"}");
    
  }

  @GetMapping(path="/bookings") 
  public ResponseEntity<?> getBookings(@RequestParam(value = "email", required = true) String email) throws Exception {

    if(upRepo.getBookings(email) == null) {
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("You have no bookings");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    upRepo.getBookings(email).stream()
        .forEach(b -> {
          arrayBuilder.add(b.toBookings());
        });

    return ResponseEntity.ok(arrayBuilder.build().toString());
  }

  @DeleteMapping(path = "/deletebooking")
  public ResponseEntity<?> deleteBooking(
      @RequestHeader(value = "email", required = true) String email,
      @RequestHeader(value = "bookingDate", required = true) String bookingDate,
      @RequestHeader(value = "image", required = true) String image,
      @RequestHeader(value = "description", required = true) String description
      ) throws Exception {

    Integer success = upRepo.removeBooking(email, bookingDate, description);
    
    BookingAvailability ba = new BookingAvailability();
    ba.setImage(image);
    ba.setDate(bookingDate);
    Integer currAvailability = mongoRepo.getAvailability(ba.getImage(), ba.getDate());
    ba.setAvailability(currAvailability+1);
    mongoRepo.updateAvailability(ba);

    return ResponseEntity.ok("{\"status\":\"success\"}");
  }

  //MongoDB
  @GetMapping(path = "/check")
  public ResponseEntity<?> checkAvailability(@RequestParam(value = "image", required = true) String image) throws Exception{
    Integer availability = mongoRepo.getCurrentAvailability(image);
    return ResponseEntity.ok(String.valueOf(availability));
  }

  @PostMapping(path = "/updateminus")
  public ResponseEntity<?> minusAvailability(@RequestBody String booking) throws Exception {
    BookingAvailability ba = BookingAvailability.createJson(booking);
    Integer availability = mongoRepo.getAvailability(ba.getImage(), ba.getDate());
    if(availability == -10) mongoRepo.insertAvailability(ba);
    else if(availability == 0){
      ErrorResponse errorResponse = new ErrorResponse();
      errorResponse.setMessage("No racks available on selected date");
      return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } else {
      Integer availabilityToUpdate = availability - 1;
      ba.setAvailability(availabilityToUpdate);
      mongoRepo.updateAvailability(ba);
    }
    return ResponseEntity.ok("{\"status\":\"success\"}");
  }

  @PostMapping(path = "/updateadd")
  public ResponseEntity<?> addAvailability(@RequestBody String booking) throws Exception {
    BookingAvailability ba = BookingAvailability.createJson(booking);
    Integer availability = mongoRepo.getAvailability(ba.getImage(), ba.getDate());
    Integer availabilityToUpdate = availability + 1;
    ba.setAvailability(availabilityToUpdate);
    mongoRepo.updateAvailability(ba);

    return ResponseEntity.ok("{\"status\":\"success\"}");
  }
} 
