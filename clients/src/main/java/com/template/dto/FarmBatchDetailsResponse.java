package com.template.dto;

import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;

import java.time.LocalDate;

public class FarmBatchDetailsResponse {

    private AbstractParty supplier;
    private String farmId;
    private String batchNo;
    private String location;
    private LocalDate harvestDate;
    private Double meanTemp;
    private Double meanNitrogen;
    private String category;
    private UniqueIdentifier linearId;

    public FarmBatchDetailsResponse(){}

    public FarmBatchDetailsResponse(AbstractParty supplier, String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen, String category, UniqueIdentifier linearId) {
        this.supplier = supplier;
        this.farmId = farmId;
        this.batchNo = batchNo;
        this.location = location;
        this.harvestDate = harvestDate;
        this.meanTemp = meanTemp;
        this.meanNitrogen = meanNitrogen;
        this.category = category;
        this.linearId = linearId;
    }

    public AbstractParty getSupplier() {
        return supplier;
    }

    public void setSupplier(AbstractParty supplier) {
        this.supplier = supplier;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public Double getMeanTemp() {
        return meanTemp;
    }

    public void setMeanTemp(Double meanTemp) {
        this.meanTemp = meanTemp;
    }

    public Double getMeanNitrogen() {
        return meanNitrogen;
    }

    public void setMeanNitrogen(Double meanNitrogen) {
        this.meanNitrogen = meanNitrogen;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }
}
