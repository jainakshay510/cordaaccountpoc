package com.template.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.corda.core.serialization.CordaSerializable;

import java.time.LocalDate;

@CordaSerializable
public class Logistic_DistributorFlowDto {

    private String distributorName;
    private String distributorId;
    private String wayBillNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate receivedDate;
    private Double storageTemp;
    private String batchNo;

    public Logistic_DistributorFlowDto(String distributorName, String distributorId, String wayBillNo, LocalDate receivedDate, Double storageTemp, String batchNo) {
        this.distributorName = distributorName;
        this.distributorId = distributorId;
        this.wayBillNo = wayBillNo;
        this.receivedDate = receivedDate;
        this.storageTemp = storageTemp;
        this.batchNo = batchNo;
    }

    public  Logistic_DistributorFlowDto(){}

    public String getDistributorName() {
        return distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getWayBillNo() {
        return wayBillNo;
    }

    public void setWayBillNo(String wayBillNo) {
        this.wayBillNo = wayBillNo;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Double getStorageTemp() {
        return storageTemp;
    }

    public void setStorageTemp(Double storageTemp) {
        this.storageTemp = storageTemp;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
}
