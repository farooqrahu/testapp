package com.rahu.springjwt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utility {



  public static String formatDate(Date date, String pattern){
//    String pattern = "dd-MM-yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    return simpleDateFormat.format(date);
  }

  public static String convertObjectToJsonString(Object payload) {
    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = null;
    try {
      jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
    } catch (IOException e) {

    }
    return jsonInString;
  }

  /**
   * the following method is used to parse data from json string
   *
   * @param payload - json string to parse
   * @return Map<String, String> - object after parsing json string
   */
  public static Map<String, String> convertJsonStringToMapString(String payload) {
    Map<String, String> myResponse = new HashMap<>();
    try {
      ObjectMapper mapper = new ObjectMapper();
      myResponse = mapper.readValue(payload, LinkedHashMap.class);
    } catch (JsonProcessingException e) {
    } catch (IOException e) {
      e.printStackTrace();
    }
    return myResponse;
  }

  /**
   * the following method is used to clean phone number
   *
   * @param phoneNumber - phone number to clean
   * @return String - phone number after cleaning
   */
  public static String cleanPhoneNumber(String phoneNumber) {
    return phoneNumber.trim().replaceAll(" ", "");
  }

  /**
   * the following method is used to convert string
   * to given date format
   *
   * @param inputStringFormat - format of current string
   * @param date - string to convert
   * @return Date - converted string to date format
   */
  public static Date parseStringToDate(String date, String inputStringFormat) {
    SimpleDateFormat inputParser = new SimpleDateFormat(inputStringFormat, Locale.US);
    try {
      return inputParser.parse(date);
    } catch (java.text.ParseException e) {
      return new Date(0);
    }
  }

  /**
   * the following method is used to convert string
   * to default date format
   *
   * @param date - string to convert
   * @return Date - converted string to date format
   */
  public static Date parseStringToDate(String date) {
    SimpleDateFormat inputParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    try {
      return inputParser.parse(date);
    } catch (java.text.ParseException e) {
      return new Date(0);
    }
  }

  /**
   * the following method is used to convert date
   * to string
   *
   * @param date - date to convert
   * @return String - converted date to required format
   */
  public static String parseDateToString(Date date) {
    SimpleDateFormat inputParser = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    return inputParser.format(date);
  }

  /**
   * the following method is used to convert date
   * timestamp to string
   *
   * @param date - date timestamp to convert
   * @return String - converted date to required format
   */
  public static String parseDateToString(Long date) {
    SimpleDateFormat inputParser = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    return inputParser.format(date);
  }

  /**
   * the following method is used to convert string
   * to local date time format
   *
   * @param date - string to convert
   * @return Date - converted string to date format
   */
  public static LocalDateTime parseStringToLocalDateTime(String date) {
    DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    return LocalDateTime.parse(date, inputFormat);
  }

  /**
   * the following method is used to get
   * current time stamp and return as string
   *
   * @return String - current time stamp as string
   */
  public static String getCurrentTimeStamp() {
    return String.valueOf(System.currentTimeMillis());
  }

  /**
   * the following method is used to convert string
   * by given separator into array
   *
   * @return String[] - converted array
   */
  public static String[] convertStringBySeparatorToArray(String keyword, String separator) {
    return keyword.trim().split(separator);
  }

  /**
   * the following method is used to sort custom
   * object list by given index
   *
   * @param previous - previous object to compare
   * @param next - next object to compare
   * @param key - index to compare
   * @return Integer - result of the comparison
   */
  public static Integer sortCustomObjectListByKey(Object previous, Object next, Integer key) {
    Object[] previousValue = (Object[]) previous;
    Object[] nextValue = (Object[]) next;
    return Integer.compare(((Integer) previousValue[key]), ((Integer) nextValue[key]));
  }
}
