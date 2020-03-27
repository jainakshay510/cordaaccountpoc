package com.template.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.PersistentStateRef;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@CordaSerializable
public class TomatoFarmStateSchemaV1 extends MappedSchema {

    public TomatoFarmStateSchemaV1(){super(TomatoFarmStateSchema.class,1, ImmutableList.of(PersistentToken.class));}

    @Entity
    @Table(name="tomatoFarm_states")
    public static class PersistentToken extends PersistentState {


        @Column(name="supplier")
        private final String supplier;
        @Column(name="suppliedTo")
        private final String suppliedTo;
        @Column(name="farmId")
        private final String farmId;
        @Column(name="batchNo")
        private final String batchNo;
        @Column(name="location")
        private final String location;
        @Column(name="harvestDate")
        private final LocalDate harvestDate;
        @Column(name="meanTemp")
        private final Double meanTemp;

        @Column(name="meanNitrogen")
        private final Double meanNitrogen;

        @Column(name="category")
        private final String category;

        @Column(name="linearId")
        private final UUID linearId;

        public PersistentToken(String supplier, String suppliedTo, String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen,String category, UUID linearId) {
            this.supplier = supplier;
            this.suppliedTo = suppliedTo;
            this.farmId = farmId;
            this.batchNo = batchNo;
            this.location = location;
            this.harvestDate = harvestDate;
            this.meanTemp = meanTemp;
            this.meanNitrogen = meanNitrogen;
            this.category=category;
            this.linearId = linearId;
        }

        public PersistentToken(){
            this.supplier = null;
            this.suppliedTo = null;
            this.farmId = null;
            this.batchNo = null;
            this.location = null;
            this.harvestDate = null;
            this.meanTemp = 0.0;
            this.meanNitrogen = 0.0;
            this.category=null;
            this.linearId = null;
        }

        public String getSupplier() {
            return supplier;
        }

        public String getSuppliedTo() {
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

        public UUID getLinearId() {
            return linearId;
        }

        public String getCategory() {
            return category;
        }
    }
}
