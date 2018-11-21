package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

public class PriceRange {

  private static final String FIRST_RANGE_STARTS_WITH = "Under";
  private static final String LAST_RANGE_ENDS_WITH = "And above";
  private static final int MAX_NO_OF_RANGES = 4;
  private static final int MIN_RANGE_SIZE = 5;
  private static final int MAX_RANGE_SIZE = 25;
  
  private int min;
  private int max;
  
  // neha code 
  //private static final String 

  public static List<PriceRange> buildPriceRangeList(int max) {
    List<PriceRange> list = new ArrayList<>();
    int intervalSize = max / MAX_NO_OF_RANGES;
    intervalSize = Math.max(MIN_RANGE_SIZE, Math.min(intervalSize, MAX_RANGE_SIZE));
    if(intervalSize > 5) {
      // round it to nearest divisible of 5
      intervalSize = intervalSize - (intervalSize % 5);
    } else {
      // round it to even number
      intervalSize = intervalSize - (intervalSize % 2);
    }
    int start = 0;
    for(int i = 0; i < MAX_NO_OF_RANGES; i++) {
      int end = start + intervalSize;
      if(end > max) {
        list.add(new PriceRange(start, 0));
        break;
      }
      list.add(new PriceRange(start, end));
      start = end;
    }
    list.get(list.size() - 1).max = 0;
    return list;
  }

  public static List<String> getDisplayNames(List<PriceRange> list) {
    List<String> names = new ArrayList<>();
    for (PriceRange priceRange : list) {
      names.add(priceRange.displayName());
    }
    return names;
  }

  public static PriceRange from(String name){
    if (Strings.isNullOrEmpty(name)) {
      return null;
    }
    PriceRange range = new PriceRange();
    name = name.toLowerCase();
    if(name.startsWith(FIRST_RANGE_STARTS_WITH.toLowerCase())) {
      range.max = Integer.parseInt(name.substring(FIRST_RANGE_STARTS_WITH.length() + 2));
      return range;
    }
    if(name.endsWith(LAST_RANGE_ENDS_WITH.toLowerCase())) {
      range.min = Integer.parseInt(name.substring(1, name.length() - (LAST_RANGE_ENDS_WITH.length() + 1)));
      return range;
    }
    String[] tokens = name.split("to");
    range.min = Integer.parseInt(tokens[0].trim().startsWith("$") ? tokens[0].trim().substring(1) : tokens[0].trim());
    range.max = Integer.parseInt(tokens[1].trim().startsWith("$") ? tokens[1].trim().substring(1) : tokens[1].trim());
    return range;
  }

  public PriceRange() {
  }
  public PriceRange(int min, int max) {
    this.min = min;
    this.max = max;
  }
  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  public String displayName() {
    if (min == 0) {
      return FIRST_RANGE_STARTS_WITH + " $" + max;
    }
    if (max == 0) {
      return "$"+ min + " " + LAST_RANGE_ENDS_WITH;
    }
    return "$" + min + " To $" + max;
  }
  
}