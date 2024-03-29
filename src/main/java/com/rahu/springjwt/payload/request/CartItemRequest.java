package com.rahu.springjwt.payload.request;

import java.util.ArrayList;

import com.rahu.springjwt.models.CartItem;

import lombok.Data;

@Data
public class CartItemRequest {
  private String username;
  private String password;
  private Long userid;
  private ArrayList<CartItem> cartItems;
}
