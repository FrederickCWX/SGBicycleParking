package com.vttp2022.BicycleParkingApp.repositories;

import java.time.LocalDate;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.vttp2022.BicycleParkingApp.models.mongodb.BookingAvailability;

@Repository
public class MongoRepository {

  public static final String C_BOOKING_BAY = "bookingbay";

  private static final Logger logger = LoggerFactory.getLogger(MongoRepository.class);

  @Autowired
  private MongoTemplate mongoTemplate;

  public Integer getCurrentAvailability(String image) throws Exception{
    String now = LocalDate.now().toString();
    Criteria c = Criteria
        .where("image").is(image)
        .andOperator(
          Criteria.where("date").is(now)
        );
    Query q = Query.query(c);

    BookingAvailability response = mongoTemplate.findOne(q, BookingAvailability.class, C_BOOKING_BAY);
    Integer empty = -10;
    if(response == null) return empty;
    logger.info("Image >>> "+image+", Availability >>> "+response.getAvailability());
    return Integer.valueOf(response.getAvailability());
  }

  public Integer getAvailability(String image, String date) throws Exception{
    Criteria c = Criteria
        .where("image").is(image)
        .andOperator(
          Criteria.where("date").is(date)
        );
    Query q = Query.query(c);
    BookingAvailability response = mongoTemplate.findOne(q, BookingAvailability.class, C_BOOKING_BAY);
    Integer empty = -10;
    if(response == null) return empty;
    logger.info("Availability found >>> "+response.getAvailability());
    return Integer.valueOf(response.getAvailability());
  }

  public void insertAvailability(BookingAvailability ba) throws Exception {
    logger.info("Inserting ba into mongodb");
    mongoTemplate.insert(ba, C_BOOKING_BAY);
  }

  public void updateAvailability(BookingAvailability ba) throws Exception {
    Criteria c = Criteria
        .where("image").is(ba.getImage())
        .andOperator(
          Criteria.where("date").is(ba.getDate())
        );
    Query query = Query.query(c);
    Update update = new Update().set("availability", ba.getAvailability());

    mongoTemplate.updateFirst(query, update, C_BOOKING_BAY);
  }
}
