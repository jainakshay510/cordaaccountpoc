package com.template.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.PersistentStateRef;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@CordaSerializable
public class TomatoDistributorStateSchemaV1 extends MappedSchema {

    public TomatoDistributorStateSchemaV1() {
        super(TomatoDistributorStateSchema.class, 1, ImmutableList.of(PersistentToken.class));
    }

    @Entity
    @Table(name = "tomatoDistributor_states")
    public static class PersistentToken extends PersistentState {


        @Column(name = "distributor")
        private AbstractParty distributor;
        @Column(name = "customer")
        private AbstractParty customer;
        @Column(name = "distributorId")
        private String distributorId;
        @Column(name = "wayBillNo")
        private String wayBillNo;
        @Column(name = "receivedDate")
        private LocalDate receivedDate;
        @Column(name = "storageTemp")
        private Double storageTemp;
        @Column(name = "dispatchInfo")
        private String dispatchInfo;
        ;

        @Column(name = "dispatchDate")
        private LocalDate dispatchDate;

        @Column(name = "batchNo")
        private String batchNo;

        @Column(name = "linearId")
        private final UUID linearId;


        public PersistentToken(AbstractParty distributor, AbstractParty customer, String distributorId, String wayBillNo, LocalDate receivedDate, Double storageTemp, String dispatchInfo, LocalDate dispatchDate, String batchNo, UUID linearId) {
            this.distributor = distributor;
            this.customer = customer;
            this.distributorId = distributorId;
            this.wayBillNo = wayBillNo;
            this.receivedDate = receivedDate;
            this.storageTemp = storageTemp;
            this.dispatchInfo = dispatchInfo;
            this.dispatchDate = dispatchDate;
            this.batchNo = batchNo;
            this.linearId = linearId;
        }

        public PersistentToken() {
            this.distributor = null;
            this.customer = null;
            this.distributorId = null;
            this.wayBillNo = null;
            this.receivedDate = null;
            this.storageTemp = 0.0;
            this.dispatchInfo = null;
            this.dispatchDate = null;
            this.batchNo = null;
            this.linearId = null;
        }

        public AbstractParty getDistributor() {
            return distributor;
        }

        public AbstractParty getCustomer() {
            return customer;
        }

        public String getDistributorId() {
            return distributorId;
        }

        public String getWayBillNo() {
            return wayBillNo;
        }

        public LocalDate getReceivedDate() {
            return receivedDate;
        }

        public Double getStorageTemp() {
            return storageTemp;
        }

        public String getDispatchInfo() {
            return dispatchInfo;
        }

        public LocalDate getDispatchDate() {
            return dispatchDate;
        }

        public String getBatchNo() {
            return batchNo;
        }

        public UUID getLinearId() {
            return linearId;
        }
    }
}
