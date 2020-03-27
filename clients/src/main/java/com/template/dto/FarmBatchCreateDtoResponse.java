package com.template.dto;

import java.time.LocalDate;

public class FarmBatchCreateDtoResponse extends FarmBatchCreateDto {

    private String supplierName;
    private String suppliedToName;

    public FarmBatchCreateDtoResponse(String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen, String category, String supplierName, String suppliedToName) {
        super(farmId, batchNo, location, harvestDate, meanTemp, meanNitrogen, category);
        this.supplierName = supplierName;
        this.suppliedToName = suppliedToName;
    }

    public FarmBatchCreateDtoResponse(){}

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSuppliedToName() {
        return suppliedToName;
    }

    public void setSuppliedToName(String suppliedToName) {
        this.suppliedToName = suppliedToName;
    }
}
