package com.lmp.app.model;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmp.db.pojo.Currency;

@Component
public class UploadmultiInventory {

    private List<UploadInventory> inventoryList;

    public List<UploadInventory> getInventoryList() {
        return this.inventoryList;
    }

    public void setInventoryList(List<UploadInventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @Override
    public String toString() {
        return "{" +
            " inventoryList='" + getInventoryList() + "'" +
            "}";
    }
  

    public UploadmultiInventory() {
    }

    
    

}
