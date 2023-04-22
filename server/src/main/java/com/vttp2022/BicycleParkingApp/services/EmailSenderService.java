package com.vttp2022.BicycleParkingApp.services;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.vttp2022.BicycleParkingApp.models.mysql.Bookings;

@Service
public class EmailSenderService {

  private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class); 

  @Autowired
  private JavaMailSender mailSender;

  public void bookingConfirmationEmail(String name, Bookings b) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("bicycleparkingapp@gmail.com");
    message.setTo(b.getEmail());
    message.setText(String.format("Dear %s,\n\nYour bicycle parking rack booking has been confirmed on %s, at the following bicycle parking bay location.\n\nDescription: %s\nRack Type: %s\nRack Count: %s\nSheltered: %s\n\nThank you for using SG Bicycle Parking.\n\nWarm Regards,\nSG Bicycle Parking team.\n\nThis is an automatically generated email. Please do not reply.", 
        name,
        b.getBookingDate(),
        b.getDescription(),
        b.getRackType(),
        b.getRackCount(),
        b.getSheltered()));
    message.setSubject("Bicycle Parking Booking Confirmation");

    mailSender.send(message);

    logger.info("Booking confirmation email successfully to "+b.getEmail());
  }

  // public void sendEmail(String toEmail, String subject, String body) {
  //   SimpleMailMessage message = new SimpleMailMessage();
  //   message.setFrom("bicycleparkingapp@gmail.com");
  //   message.setTo(toEmail);
  //   message.setText(body);
  //   message.setSubject(subject);

  //   mailSender.send(message);
  //   logger.info("Mail Sent successfully to "+toEmail);
    
  // }

  // public void sendFavouritesEmail(String toEmail, String example) {
  //   SimpleMailMessage message = new SimpleMailMessage();
  //   message.setFrom("bicycleparkingapp@gmail.com");
  //   message.setTo(toEmail);
  //   message.setText(String.format("The following location has been added to your favourites.\n\nWarm Regards,\nBicycle Parking Team", example));
  //   message.setSubject("Bicycle parking added to favourites");

  //   mailSender.send(message);
  //   logger.info("Mail Sent successfully to "+toEmail);
  // }
  
}
