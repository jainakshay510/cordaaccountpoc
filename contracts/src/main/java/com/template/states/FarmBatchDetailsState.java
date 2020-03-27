package com.template.states;

import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoHarvestContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

@BelongsToContract(TomatoHarvestContract.class)
public class FarmBatchDetailsState implements LinearState {

    private AbstractParty supplier;
    private String farmId;
    private String batchNo;
    private String location;
    private LocalDate harvestDate;
    private Double meanTemp;
    private Double meanNitrogen;
    private String category;
    private UniqueIdentifier linearId;

    public FarmBatchDetailsState(){}

    public FarmBatchDetailsState(AbstractParty supplier, String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen, String category) {
        this.supplier = supplier;
        this.farmId = farmId;
        this.batchNo = batchNo;
        this.location = location;
        this.harvestDate = harvestDate;
        this.meanTemp = meanTemp;
        this.meanNitrogen = meanNitrogen;
        this.category = category;
        this.linearId = new UniqueIdentifier("909090");
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

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(supplier);
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId ;
    }
}
