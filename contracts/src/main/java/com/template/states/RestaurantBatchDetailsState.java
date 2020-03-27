package com.template.states;

import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoRestaurantContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

@BelongsToContract(TomatoRestaurantContract.class)
public class RestaurantBatchDetailsState implements LinearState {

    private DistributorBatchDetailsState distributorBatchDetailsState;
    private AbstractParty restaurant;
    private String restaurantId;
    private String name;
    private String wareHouseInfo;
    private String purchaseOrder;
    private LocalDate receivedDate;
    private String batchNo;

    public RestaurantBatchDetailsState(DistributorBatchDetailsState distributorBatchDetailsState, AbstractParty restaurant, String restaurantId, String name, String wareHouseInfo, String purchaseOrder, LocalDate receivedDate, String batchNo) {
        this.distributorBatchDetailsState = distributorBatchDetailsState;
        this.restaurant = restaurant;
        this.restaurantId = restaurantId;
        this.name = name;
        this.wareHouseInfo = wareHouseInfo;
        this.purchaseOrder = purchaseOrder;
        this.receivedDate = receivedDate;
        this.batchNo = batchNo;
    }

    public DistributorBatchDetailsState getDistributorBatchDetailsState() {
        return distributorBatchDetailsState;
    }

    public void setDistributorBatchDetailsState(DistributorBatchDetailsState distributorBatchDetailsState) {
        this.distributorBatchDetailsState = distributorBatchDetailsState;
    }

    public AbstractParty getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(AbstractParty restaurant) {
        this.restaurant = restaurant;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWareHouseInfo() {
        return wareHouseInfo;
    }

    public void setWareHouseInfo(String wareHouseInfo) {
        this.wareHouseInfo = wareHouseInfo;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return distributorBatchDetailsState.getLinearId();
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(restaurant);
    }
}
