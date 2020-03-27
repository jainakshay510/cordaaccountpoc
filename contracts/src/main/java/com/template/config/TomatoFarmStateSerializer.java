//package com.template.config;
//
//import com.template.states.TomatoFarmState;
//import net.corda.core.serialization.SerializationCustomSerializer;
//
//public class TomatoFarmStateSerializer implements SerializationCustomSerializer<TomatoFarmState,TomatoFarmStateProxy> {
//    @Override
//    public TomatoFarmState fromProxy(TomatoFarmStateProxy tomatoFarmStateProxy) {
//        return new TomatoFarmState(tomatoFarmStateProxy.getProxiedSupplier(),
//                                    tomatoFarmStateProxy.getProxiedSuppliedTo(),
//                                    tomatoFarmStateProxy.getProxiedFarmId(),
//                                    tomatoFarmStateProxy.getProxiedBatchNo(),
//                                    tomatoFarmStateProxy.getProxiedLocation(),
//                                    tomatoFarmStateProxy.getProxiedHarvestDate(),
//                                    tomatoFarmStateProxy.getProxiedMeanTemp(),
//                                    tomatoFarmStateProxy.getProxiedMeanNitrogen(),
//                                    tomatoFarmStateProxy.getProxyLinearId());
//    }
//
//    @Override
//    public TomatoFarmStateProxy toProxy(TomatoFarmState tomatoFarmState) {
//        return new TomatoFarmStateProxy(tomatoFarmState.getSupplier(),
//                                        tomatoFarmState.getSuppliedTo(),
//                                        tomatoFarmState.getFarmId(),
//                                        tomatoFarmState.getBatchNo(),
//                                        tomatoFarmState.getLocation(),
//                                        tomatoFarmState.getHarvestDate(),
//                                        tomatoFarmState.getMeanTemp(),
//                                        tomatoFarmState.getMeanNitrogen(),
//                tomatoFarmState.getLinearId());
//    }
//}
