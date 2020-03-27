//package com.template.config;
//
//import net.corda.core.contracts.UniqueIdentifier;
//import net.corda.core.identity.AbstractParty;
//
//import java.time.LocalDate;
//
//public class TomatoFarmStateProxy {
//
//    private AbstractParty proxiedSupplier;
//    private AbstractParty proxiedSuppliedTo;
//    private String proxiedFarmId;
//    private String proxiedBatchNo;
//    private String proxiedLocation;
//    private LocalDate proxiedHarvestDate;
//    private Double proxiedMeanTemp;
//    private Double proxiedMeanNitrogen;
//    private UniqueIdentifier proxyLinearId;
//
//
//    public void setProxiedSupplier(AbstractParty proxiedSupplier) {
//        this.proxiedSupplier = proxiedSupplier;
//    }
//
//    public void setProxiedSuppliedTo(AbstractParty proxiedSuppliedTo) {
//        this.proxiedSuppliedTo = proxiedSuppliedTo;
//    }
//
//    public void setProxiedFarmId(String proxiedFarmId) {
//        this.proxiedFarmId = proxiedFarmId;
//    }
//
//    public void setProxiedBatchNo(String proxiedBatchNo) {
//        this.proxiedBatchNo = proxiedBatchNo;
//    }
//
//    public void setProxiedLocation(String proxiedLocation) {
//        this.proxiedLocation = proxiedLocation;
//    }
//
//    public void setProxiedHarvestDate(LocalDate proxiedHarvestDate) {
//        this.proxiedHarvestDate = proxiedHarvestDate;
//    }
//
//    public void setProxiedMeanTemp(Double proxiedMeanTemp) {
//        this.proxiedMeanTemp = proxiedMeanTemp;
//    }
//
//    public void setProxiedMeanNitrogen(Double proxiedMeanNitrogen) {
//        this.proxiedMeanNitrogen = proxiedMeanNitrogen;
//    }
//
//    public UniqueIdentifier getProxyLinearId() {
//        return proxyLinearId;
//    }
//
//    public void setProxyLinearId(UniqueIdentifier proxyLinearId) {
//        this.proxyLinearId = proxyLinearId;
//    }
//
//    public AbstractParty getProxiedSupplier() {
//        return proxiedSupplier;
//    }
//
//    public AbstractParty getProxiedSuppliedTo() {
//        return proxiedSuppliedTo;
//    }
//
//    public String getProxiedFarmId() {
//        return proxiedFarmId;
//    }
//
//    public String getProxiedBatchNo() {
//        return proxiedBatchNo;
//    }
//
//    public String getProxiedLocation() {
//        return proxiedLocation;
//    }
//
//    public LocalDate getProxiedHarvestDate() {
//        return proxiedHarvestDate;
//    }
//
//    public Double getProxiedMeanTemp() {
//        return proxiedMeanTemp;
//    }
//
//    public Double getProxiedMeanNitrogen() {
//        return proxiedMeanNitrogen;
//    }
//
//    public TomatoFarmStateProxy(AbstractParty proxiedSupplier, AbstractParty proxiedSuppliedTo, String proxiedFarmId, String proxiedBatchNo, String proxiedLocation, LocalDate proxiedHarvestDate, Double proxiedMeanTemp, Double proxiedMeanNitrogen, UniqueIdentifier proxyLinearId) {
//        this.proxiedSupplier = proxiedSupplier;
//        this.proxiedSuppliedTo = proxiedSuppliedTo;
//        this.proxiedFarmId = proxiedFarmId;
//        this.proxiedBatchNo = proxiedBatchNo;
//        this.proxiedLocation = proxiedLocation;
//        this.proxiedHarvestDate = proxiedHarvestDate;
//        this.proxiedMeanTemp = proxiedMeanTemp;
//        this.proxiedMeanNitrogen = proxiedMeanNitrogen;
//        this.proxyLinearId = proxyLinearId;
//    }
//}
