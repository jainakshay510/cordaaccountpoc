package com.template.states;

import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoLogisticContract;
import com.template.schema.TomatoFarmStateSchemaV1;
import com.template.schema.TomatoLogisticStateSchemaV1;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

@BelongsToContract(TomatoLogisticContract.class)
@CordaSerializable
public class TomatoLogisticState implements LinearState, QueryableState {

    private AnonymousParty carrier;
    private String logisiticId;
    private String wayBillNo;
    private LocalDate pickupDate;
    private String pickupLocation;
    private Double tempInTransit;
    private LocalDate dropDate;
    private String dropLocation;
    private String batchNo;


    public TomatoLogisticState(AnonymousParty carrier, String logisiticId, String wayBillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation,String batchNo) {
        this.carrier = carrier;
        this.logisiticId = logisiticId;
        this.wayBillNo = wayBillNo;
        this.pickupDate = pickupDate;
        this.pickupLocation = pickupLocation;
        this.tempInTransit = tempInTransit;
        this.dropDate = dropDate;
        this.dropLocation = dropLocation;
        this.batchNo=batchNo;
        this.uniqueLinearId=new UniqueIdentifier("456");
    }

    public String getBatchNo() {
        return batchNo;
    }

    public String getLogisiticId() {
        return logisiticId;
    }

    public String getWayBillNo() {
        return wayBillNo;
    }

    public LocalDate getPickupDate() {
        return pickupDate;
    }

    public void setCarrier(AnonymousParty carrier) {
        this.carrier = carrier;
    }

    public void setLogisiticId(String logisiticId) {
        this.logisiticId = logisiticId;
    }

    public void setWayBillNo(String wayBillNo) {
        this.wayBillNo = wayBillNo;
    }

    public void setPickupDate(LocalDate pickupDate) {
        this.pickupDate = pickupDate;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public void setTempInTransit(Double tempInTransit) {
        this.tempInTransit = tempInTransit;
    }

    public void setDropDate(LocalDate dropDate) {
        this.dropDate = dropDate;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public Double getTempInTransit() {
        return tempInTransit;
    }

    public LocalDate getDropDate() {
        return dropDate;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public AnonymousParty getCarrier() {
        return carrier;
    }



    private  UniqueIdentifier uniqueLinearId;

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return uniqueLinearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(carrier);
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof TomatoLogisticStateSchemaV1) {

            return new TomatoLogisticStateSchemaV1.PersistentToken(
                   this.carrier,
                    this.logisiticId,
                    this.wayBillNo,
                    this.getPickupDate(),
                    this.getPickupLocation(),
                    this.getTempInTransit(),
                    this.getDropDate(),
                    this.getDropLocation(),
                    this.getBatchNo(),
                    this.uniqueLinearId.getId()
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new TomatoLogisticStateSchemaV1());
    }
}
