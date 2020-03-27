package com.template.schema;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
public class TomatoLogisticStateSchemaV1 extends MappedSchema {
    public TomatoLogisticStateSchemaV1() {
        super(TomatoDistributorStateSchema.class, 1, ImmutableList.of(PersistentToken.class));
    }

    @Entity
    @Table(name = "tomatoLogistic_states")
    public static class PersistentToken extends PersistentState {


        @Column(name = "carrier")
        private AbstractParty carrier;


        @Column(name = "logisiticId")
        private String logisiticId;
        @Column(name = "wayBillNo")
        private String wayBillNo;
        @Column(name = "pickupDate")
        private LocalDate pickupDate;
        @Column(name = "pickupLocation")
        private String pickupLocation;
        @Column(name = "tempInTransit")
        private Double tempInTransit;
        @Column(name = "dropDate")
        private LocalDate dropDate;

        @Column(name = "dropLocation")
        private String dropLocation;

        @Column(name = "batchNo")
        private String batchNo;

        @Column(name = "linearId")
        private final UUID linearId;

        public PersistentToken(AbstractParty carrier, String logisiticId, String wayBillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo, UUID linearId) {
            this.carrier = carrier;
            this.logisiticId = logisiticId;
            this.wayBillNo = wayBillNo;
            this.pickupDate = pickupDate;
            this.pickupLocation = pickupLocation;
            this.tempInTransit = tempInTransit;
            this.dropDate = dropDate;
            this.dropLocation = dropLocation;
            this.batchNo = batchNo;
            this.linearId = linearId;
        }

        public PersistentToken() {
            this.carrier = null;
            this.logisiticId = null;
            this.wayBillNo = null;
            this.pickupDate = null;
            this.pickupLocation = null;
            this.tempInTransit = 0.0;
            this.dropDate = null;
            this.dropLocation = null;
            this.batchNo = null;
            this.linearId = null;
        }

        public AbstractParty getCarrier() {
            return carrier;
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

        public String getBatchNo() {
            return batchNo;
        }

        public UUID getLinearId() {
            return linearId;
        }
    }



}
