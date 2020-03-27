package com.template.dto;

import net.corda.core.identity.AbstractParty;
import net.corda.core.serialization.CordaSerializable;

import java.time.LocalDate;

@CordaSerializable
public class Distributor_RestaurantFlowDto {
    private String restaurantPartyName;
    private String restaurantId;
    private String name;
    private String wareHouseInfo;
    private String purchaseOrder;
    private LocalDate receivedDate;
    private String batchNo;

    public Distributor_RestaurantFlowDto(String restaurantPartyName, String restaurantId, String name, String wareHouseInfo, String purchaseOrder, LocalDate receivedDate, String batchNo) {
        this.restaurantPartyName = restaurantPartyName;
        this.restaurantId = restaurantId;
        this.name = name;
        this.wareHouseInfo = wareHouseInfo;
        this.purchaseOrder = purchaseOrder;
        this.receivedDate = receivedDate;
        this.batchNo = batchNo;
    }

    public Distributor_RestaurantFlowDto(){}

    public String getRestaurantPartyName() {
        return restaurantPartyName;
    }

    public void setRestaurantPartyName(String restaurantPartyName) {
        this.restaurantPartyName = restaurantPartyName;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWareHouseInfo() {
        return wareHouseInfo;
    }

    public void setWareHouseInfo(String wareHouseInfo) {
        this.wareHouseInfo = wareHouseInfo;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
}
