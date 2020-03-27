package com.template.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.template.states.TomatoFarmState;
import com.template.states.TomatoLogisticState;
import net.corda.core.serialization.CordaSerializable;

import java.time.LocalDate;

@CordaSerializable
public class Farm_LogisticFlowDto {
    private String logisticId;
    private String waybillNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate pickupDate;
    private String pickupLocation;
    private Double tempInTransit;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate dropDate;
    private String dropLocation;
    private String batchNo;
    private String carrierName;

    public Farm_LogisticFlowDto(String logisticId, String waybillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo, String carrierName) {
        this.logisticId = logisticId;
        this.waybillNo = waybillNo;
        this.pickupDate = pickupDate;
        this.pickupLocation = pickupLocation;
        this.tempInTransit = tempInTransit;
        this.dropDate = dropDate;
        this.dropLocation = dropLocation;
        this.batchNo = batchNo;
        this.carrierName = carrierName;
    }

    public Farm_LogisticFlowDto(){}

    public String getLogisticId() {
        return logisticId;
    }

    public void setLogisticId(String logisticId) {
        this.logisticId = logisticId;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
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

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
}








