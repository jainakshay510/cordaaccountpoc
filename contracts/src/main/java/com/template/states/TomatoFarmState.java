package com.template.states;

import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoHarvestContract;
import com.template.schema.TomatoFarmStateSchemaV1;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@BelongsToContract(TomatoHarvestContract.class)
@CordaSerializable
public class TomatoFarmState implements LinearState, QueryableState {

    private AnonymousParty supplier;
    private AnonymousParty suppliedTo;
  private String farmId;
  private String batchNo;
  private String location;
  private LocalDate harvestDate;
  private Double meanTemp;
  private Double meanNitrogen;
  private String category;
    private  UniqueIdentifier linearId;


    public TomatoFarmState(AnonymousParty supplier, AnonymousParty suppliedTo, String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen, String category) {
        this.supplier = supplier;
        this.suppliedTo = suppliedTo;
        this.farmId = farmId;
        this.batchNo = batchNo;
        this.location = location;
        this.harvestDate = harvestDate;
        this.meanTemp = meanTemp;
        this.meanNitrogen = meanNitrogen;
        this.category = category;
        this.linearId = new UniqueIdentifier("12345");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public AbstractParty getSupplier() {
        return supplier;
    }

    public AbstractParty getSuppliedTo() {
        return suppliedTo;
    }

    public String getFarmId() {
        return farmId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public Double getMeanTemp() {
        return meanTemp;
    }

    public Double getMeanNitrogen() {
        return meanNitrogen;
    }





    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public void setSupplier(AnonymousParty supplier) {
        this.supplier = supplier;
    }

    public void setSuppliedTo(AnonymousParty suppliedTo) {
        this.suppliedTo = suppliedTo;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public void setMeanTemp(Double meanTemp) {
        this.meanTemp = meanTemp;
    }

    public void setMeanNitrogen(Double meanNitrogen) {
        this.meanNitrogen = meanNitrogen;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        ArrayList<AbstractParty> list=new ArrayList<>();
        list.add(supplier);
        return list;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof TomatoFarmStateSchemaV1) {

            System.out.println("Akshay+"+this.supplier);
                return new TomatoFarmStateSchemaV1.PersistentToken(
                        this.supplier==null?null:this.supplier.toString(),this.suppliedTo==null?null:this.suppliedTo.toString(),
                        this.farmId,
                        this.batchNo,
                        this.location,
                        this.harvestDate,
                        this.meanTemp,
                        this.meanNitrogen,
                        this.category,
                        this.getLinearId().getId()
                );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return  ImmutableList.of(new TomatoFarmStateSchemaV1());
    }
}
