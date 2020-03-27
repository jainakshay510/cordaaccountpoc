package com.template.states;

import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoDistributorContract;
import com.template.schema.TomatoDistributorStateSchemaV1;
import com.template.schema.TomatoFarmStateSchemaV1;
import net.corda.core.contracts.BelongsToContract;
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

@BelongsToContract(TomatoDistributorContract.class)
@CordaSerializable
public class TomatoDistributorState implements LinearState, QueryableState {

    private AnonymousParty distributor;
    private AnonymousParty customer;

    private String distributorId;
    private String WayBillNo;
    private LocalDate receivedDate;
    private Double storageTemp;
    private String dispatchInfo;
    private LocalDate dispatchDate;
    private String batchNo;

    public TomatoDistributorState(AnonymousParty distributor, AnonymousParty customer, String distributorId, String wayBillNo, LocalDate receivedDate, Double storageTemp, String dispatchInfo, LocalDate dispatchDate, String batchNo) {
        this.distributor = distributor;
        this.customer = customer;
        this.distributorId = distributorId;
        WayBillNo = wayBillNo;
        this.receivedDate = receivedDate;
        this.storageTemp = storageTemp;
        this.dispatchInfo = dispatchInfo;
        this.dispatchDate = dispatchDate;
        this.batchNo = batchNo;
        this.uniqueLinearId=new UniqueIdentifier("12345");
    }

    public AnonymousParty getDistributor() {
        return distributor;
    }

    public void setDistributor(AnonymousParty distributor) {
        this.distributor = distributor;
    }

    public AnonymousParty getCustomer() {
        return customer;
    }

    public void setCustomer(AnonymousParty customer) {
        this.customer = customer;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getWayBillNo() {
        return WayBillNo;
    }

    public void setWayBillNo(String wayBillNo) {
        WayBillNo = wayBillNo;
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

    public String getDispatchInfo() {
        return dispatchInfo;
    }

    public void setDispatchInfo(String dispatchInfo) {
        this.dispatchInfo = dispatchInfo;
    }

    public LocalDate getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(LocalDate dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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
        return ImmutableList.of(distributor);
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
        if (schema instanceof TomatoDistributorStateSchemaV1) {

            return new TomatoDistributorStateSchemaV1.PersistentToken(
                    this.distributor,
                    this.customer,
                    this.getDistributorId(),
                    this.getWayBillNo(),
                    this.getReceivedDate(),
                    this.getStorageTemp(),
                    this.dispatchInfo,
                    this.dispatchDate,
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
        return ImmutableList.of(new TomatoDistributorStateSchemaV1());
    }
}
