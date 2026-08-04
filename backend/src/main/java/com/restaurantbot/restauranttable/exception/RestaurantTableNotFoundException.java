 package com.restaurantbot.restauranttable.exception;

 import java.util.UUID;

 public class RestaurantTableNotFoundException extends RuntimeException {

     public RestaurantTableNotFoundException(Long id) {
         super("Restaurant Table not found with id: " + id);
     }
 }
