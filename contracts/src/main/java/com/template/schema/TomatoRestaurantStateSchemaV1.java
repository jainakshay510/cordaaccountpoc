package com.template.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@CordaSerializable
public class TomatoRestaurantStateSchemaV1 extends MappedSchema {
    public TomatoRestaurantStateSchemaV1() {
        super(TomatoRestaurantStateSchema.class, 1, ImmutableList.of(PersistentToken.class));
    }


    @Entity
    @Table(name = "tomatoRestaurant_states")
    public static class PersistentToken extends PersistentState {


        @Column(name = "restaurant")
        private AbstractParty restaurant;


        @Column(name = "restaurantId")
        private String restaurantId;
        @Column(name = "name")
        private String name;
        @Column(name = "wareHouseInfo")
        private String wareHouseInfo;
        @Column(name = "purchaseOrder")
        private String purchaseOrder;
        @Column(name = "receivedDate")
        private LocalDate receivedDate;

        @Column(name = "batchNo")
        private String batchNo;


        @Column(name = "linearId")
        private final UUID linearId;

        public PersistentToken(AbstractParty restaurant, String restaurantId, String name, String wareHouseInfo, String purchaseOrder, LocalDate receivedDate, String batchNo, UUID linearId) {
            this.restaurant = restaurant;
            this.restaurantId = restaurantId;
            this.name = name;
            this.wareHouseInfo = wareHouseInfo;
            this.purchaseOrder = purchaseOrder;
            this.receivedDate = receivedDate;
            this.batchNo = batchNo;
            this.linearId = linearId;
        }

        public PersistentToken() {
            this.restaurant = null;
            this.restaurantId = null;
            this.name = null;
            this.wareHouseInfo = null;
            this.purchaseOrder = null;
            this.receivedDate = null;

            this.batchNo = null;
            this.linearId = null;
        }

        public AbstractParty getRestaurant() {
            return restaurant;
        }

        public String getRestaurantId() {
            return restaurantId;
        }

        public String getName() {
            return name;
        }

        public String getWareHouseInfo() {
            return wareHouseInfo;
        }

        public String getPurchaseOrder() {
            return purchaseOrder;
        }

        public LocalDate getReceivedDate() {
            return receivedDate;
        }



        public String getBatchNo() {
            return batchNo;
        }

        public UUID getLinearId() {
            return linearId;
        }
    }

}
