package com.rahu.springjwt.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {


  public static String formatDate(Date date,String pattern){
//    String pattern = "dd-MM-yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    return simpleDateFormat.format(date);
  }
}
