package com.rahu.springjwt.dto;
public class AccountTypes {
public enum AccountsEnum {
  CASH("Cash"),
  SALES_REVENUE("Sales Revenue"),
  ACCOUNTS_RECEIVABLE("Accounts Receivable"),
  ACCOUNTS_PAYABLE("Accounts Payable"),
  INVENTORY("Inventory");

  private final String description;

  AccountsEnum(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return description;
  }
}
}
