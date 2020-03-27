package com.template.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.template.states.TomatoFarmState;

import java.time.LocalDate;

public class FarmBatchCreateDto {

    private String farmId;
    private String batchNo;
    private String location;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;
    private Double meanTemp;
    private Double meanNitrogen;
    private String category;


    public FarmBatchCreateDto(String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen,String category) {
        this.farmId = farmId;
        this.batchNo = batchNo;
        this.location = location;
        this.harvestDate = harvestDate;
        this.meanTemp = meanTemp;
        this.meanNitrogen = meanNitrogen;
        this.category=category;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public FarmBatchCreateDto(){}

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
}
