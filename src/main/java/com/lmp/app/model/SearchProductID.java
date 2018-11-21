package com.lmp.app.model;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmp.db.pojo.Currency;

@Component
public class SearchProductID {

 
    private List<String> listOfUpc;

    @Override
    public String toString() {
        return "{" +
            " listOfUpc='" + getListOfUpc() + "'" +
            "}";
    }

    public List<String> getListOfUpc() {
        return this.listOfUpc;
    }

    public void setListOfUpc(List<String> listOfUpc) {
        this.listOfUpc = listOfUpc;
    }

    
  public SearchProductID() {
  }


}

