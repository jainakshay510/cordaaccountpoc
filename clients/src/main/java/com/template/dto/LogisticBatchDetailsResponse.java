package com.template.dto;

import com.template.states.FarmBatchDetailsState;
import net.corda.core.identity.AbstractParty;

import java.time.LocalDate;

public class LogisticBatchDetailsResponse {

    private FarmBatchDetailsState farmBatchDetailsState;
    private AbstractParty carrier;
    private String logisiticId;
    private String wayBillNo;
    private LocalDate pickupDate;
    private String pickupLocation;
    private Double tempInTransit;
    private LocalDate dropDate;
    private String dropLocation;
    private String batchNo;

    public LogisticBatchDetailsResponse(){}

    public LogisticBatchDetailsResponse(FarmBatchDetailsState farmBatchDetailsState, AbstractParty carrier, String logisiticId, String wayBillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo) {
        this.farmBatchDetailsState = farmBatchDetailsState;
        this.carrier = carrier;
        this.logisiticId = logisiticId;
        this.wayBillNo = wayBillNo;
        this.pickupDate = pickupDate;
        this.pickupLocation = pickupLocation;
        this.tempInTransit = tempInTransit;
        this.dropDate = dropDate;
        this.dropLocation = dropLocation;
        this.batchNo = batchNo;
    }

    public FarmBatchDetailsState getFarmBatchDetailsState() {
        return farmBatchDetailsState;
    }

    public void setFarmBatchDetailsState(FarmBatchDetailsState farmBatchDetailsState) {
        this.farmBatchDetailsState = farmBatchDetailsState;
    }

    public AbstractParty getCarrier() {
        return carrier;
    }

    public void setCarrier(AbstractParty carrier) {
        this.carrier = carrier;
    }

    public String getLogisiticId() {
        return logisiticId;
    }

    public void setLogisiticId(String logisiticId) {
        this.logisiticId = logisiticId;
    }

    public String getWayBillNo() {
        return wayBillNo;
    }

    public void setWayBillNo(String wayBillNo) {
        this.wayBillNo = wayBillNo;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Double getTempInTransit() {
        return tempInTransit;
    }

    public void setTempInTransit(Double tempInTransit) {
        this.tempInTransit = tempInTransit;
    }

    public LocalDate getDropDate() {
        return dropDate;
    }

    public void setDropDate(LocalDate dropDate) {
        this.dropDate = dropDate;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
}
