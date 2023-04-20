package com.vttp2022.BicycleParkingApp.models.mysql;

import java.util.List;

import org.slf4j.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class UserDetails{

  private Integer userDetailsId;
  private String name;
  private String password;
  private String email;

  //To be confirmed
  private List<Favourites> favourites;

  public Integer getId() { return userDetailsId; }
  public void setId(Integer userDetailsId) { this.userDetailsId = userDetailsId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public List<Favourites> getFavourites() { return favourites; }
  public void setFavourites(List<Favourites> favourites) { this.favourites = favourites; }

  public static UserDetails create(SqlRowSet rs) {
    UserDetails ud = new UserDetails();

    ud.setId(rs.getInt("user_details_id"));
    ud.setName(rs.getString("name"));
    ud.setEmail(rs.getString("email"));

    return ud;
  }

  public JsonObject toUserDetails() {
    return Json.createObjectBuilder()
        .add("user_details_id", userDetailsId)
        .add("name", name)
        .add("email", email)
        .build();
  }

}