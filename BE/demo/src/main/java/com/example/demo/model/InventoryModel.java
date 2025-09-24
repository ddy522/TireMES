package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryModel {
    private String partCode;
    private String partName;
    private String std;
    private String company;
    private Integer safeQty;
    private Integer currInventory;
    private String stockStatus;
    private String unit;
}