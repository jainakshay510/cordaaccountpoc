package com.template.webserver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName;
import com.template.dto.*;
import com.template.flows.*;
import com.template.flows.accounts.*;
import com.template.schema.TomatoDistributorStateSchemaV1;
import com.template.schema.TomatoFarmStateSchemaV1;
import com.template.schema.TomatoLogisticStateSchemaV1;
import com.template.schema.TomatoRestaurantStateSchemaV1;
import com.template.states.*;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.*;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
@CrossOrigin("*")
public class Controller {

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }


    @PostMapping(value = "/tomatoBatchFarmGenerate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    private ResponseEntity<MainResponseDTO<String>> tomatoBatchFarmGenerate(@RequestBody FarmBatchCreateDto requestDto) {

        MainResponseDTO<String> response=new MainResponseDTO();
        String batchNo = requestDto.getBatchNo();
        if (batchNo == null) {
            response.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(TomatoBatchIssueFarmInitiator.class,
                    requestDto.getFarmId(),
                    requestDto.getBatchNo(),
                    requestDto.getLocation(),
                    requestDto.getHarvestDate(),
                    requestDto.getMeanTemp(),
                    requestDto.getMeanNitrogen(),
                    requestDto.getCategory()).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());

            response.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            response.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping(value = "/farmLogisticFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<MainResponseDTO<String>> farmLogisticFlow(@RequestBody Farm_LogisticFlowDto requestDto) {

        MainResponseDTO<String> responseDTO=new MainResponseDTO<>();
        String batchNo = requestDto.getBatchNo();
        String carrierName = requestDto.getCarrierName();


        if (carrierName == null) {
            responseDTO.setResponse("Query parameter 'supplierName' must not be null.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        if (batchNo == null) {
            responseDTO.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        CordaX500Name partyX500Name = CordaX500Name.parse(carrierName);
        Party carrier = proxy.wellKnownPartyFromX500Name(partyX500Name);

        if (!(carrier instanceof Party)) {
            responseDTO.setResponse("Party named " + carrierName +" cannot be found.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }


        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(TomatoFarm_LogisticInitiator.class,
                    requestDto.getLogisticId(),
                    requestDto.getWaybillNo(),
                    requestDto.getPickupDate(),
                    requestDto.getPickupLocation(),
                    requestDto.getTempInTransit(),
                    requestDto.getDropDate(),
                    requestDto.getDropLocation(),
                    requestDto.getBatchNo(),
                    carrier).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());
            responseDTO.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            responseDTO.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }
    @PostMapping(value = "/logisticDistributorFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<MainResponseDTO<String>> logisticDistributorFlow(@RequestBody Logistic_DistributorFlowDto requestDto) {

        MainResponseDTO<String> responseDTO=new MainResponseDTO<>();
        String batchNo = requestDto.getBatchNo();
        String distributorName = requestDto.getDistributorName();


        if (distributorName == null) {
            responseDTO.setResponse("Query parameter 'supplierName' must not be null.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        if (batchNo == null) {
            responseDTO.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        CordaX500Name partyX500Name = CordaX500Name.parse(distributorName);
        Party distributor = proxy.wellKnownPartyFromX500Name(partyX500Name);

        if (!(distributor instanceof Party)) {
            responseDTO.setResponse("Party named "+ distributorName+" cannot be found.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }


        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(TomatoLogistic_DistributorInitiator.class,
                   requestDto.getDistributorId(),
                    requestDto.getWayBillNo(),
                    requestDto.getReceivedDate(),
                    requestDto.getStorageTemp(),
                    requestDto.getBatchNo(),
                    distributor).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());
            responseDTO.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            responseDTO.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    @PostMapping(value = "/distributorRestaurantFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<MainResponseDTO<String>> distributorRestaurantFlow(@RequestBody Distributor_RestaurantFlowDto requestDto) {

        MainResponseDTO<String> responseDTO=new MainResponseDTO<>();
        String batchNo = requestDto.getBatchNo();
        String restaurantPartyName = requestDto.getRestaurantPartyName();


        if (restaurantPartyName == null) {
            responseDTO.setResponse("Query parameter 'supplierName' must not be null.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        if (batchNo == null) {
            responseDTO.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        CordaX500Name partyX500Name = CordaX500Name.parse(restaurantPartyName);
        Party restaurant = proxy.wellKnownPartyFromX500Name(partyX500Name);

        if (!(restaurant instanceof Party)) {
            responseDTO.setResponse("Party named " +restaurantPartyName +" cannot be found.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }


        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(TomatoDistributor_RestaurantInitiator.class,
                   requestDto.getRestaurantId(),
                    requestDto.getName(),
                    requestDto.getWareHouseInfo(),
                    requestDto.getPurchaseOrder(),
                    requestDto.getReceivedDate(),
                    requestDto.getBatchNo(),
                    restaurant).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());
            responseDTO.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            responseDTO.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


    /*Farm states fetching api*/

    @GetMapping(value = "/farmstates/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FarmBatchCreateDtoResponse>> getAllFarmState(@PathVariable("status") String status) {

        try {
            Vault.StateStatus stateStatus;
            switch(status.toLowerCase()) {
                case "unconsumed":
                    stateStatus = Vault.StateStatus.UNCONSUMED;
                    break;
                case "consumed":
                    stateStatus = Vault.StateStatus.CONSUMED;
                    break;
                case "all":
                    stateStatus=Vault.StateStatus.ALL;
                    break;
                default:
                    throw new IllegalArgumentException("Status enetered is wrong");
            }
            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(stateStatus);
           // FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
           // CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
            //QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria);

            //QueryCriteria criteria = generalCriteria.and(customCriteria);
            Vault.Page<TomatoFarmState> results = proxy.vaultQueryByCriteria(generalCriteria, TomatoFarmState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            List<FarmBatchCreateDtoResponse> farmStates=results.getStates().stream().map(i->i.getState().getData()).collect(Collectors.toList()).stream().map(j->new FarmBatchCreateDtoResponse(
                    j.getFarmId(),
                    j.getBatchNo(),j.getLocation(),j.getHarvestDate(),j.getMeanTemp(),j.getMeanNitrogen(),j.getCategory(),Optional.ofNullable(j.getSupplier()).isPresent()?j.getSupplier().toString():null,
                    Optional.ofNullable(j.getSuppliedTo()).isPresent()?j.getSuppliedTo().toString():null
            )).collect(Collectors.toList());
            TomatoFarmState farmState = results.getStates().get(0).getState().getData();


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(farmStates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList());
        }
    }

    @GetMapping(value = "/farmbatchdetailstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FarmBatchDetailsState> getFarmBatchDetailsState(@PathVariable("batchNo") String batchNo) {

        try {

            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
//             FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
//             CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
//            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL, ImmutableSet.of(FarmBatchDetailsState.class));

            //QueryCriteria criteria = generalCriteria.and(customCriteria);
            Vault.Page<FarmBatchDetailsState> tempResults=proxy.vaultQueryByCriteria(generalCriteria,FarmBatchDetailsState.class);
            // Vault.Page<FarmBatchDetailsState> results = proxy.vaultQueryByCriteria(customCriteria, FarmBatchDetailsState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());

            //FarmBatchDetailsState farmState = results.getStates().get(0).getState().getData();

            List<StateAndRef<FarmBatchDetailsState>> farmBatchDetailsStates=tempResults.getStates();
//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
            StateAndRef<FarmBatchDetailsState> inputFarmStateAndRef = farmBatchDetailsStates
                    .stream().filter(farmStateAndRef-> {
                        FarmBatchDetailsState farmState = farmStateAndRef.getState().getData();

                        return (farmState.getBatchNo().equals(batchNo));
                    }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
            FarmBatchDetailsState farmBatchState=inputFarmStateAndRef.getState().getData();
            FarmBatchDetailsState convertedFarmState=converter.getObjectMapper().convertValue(farmBatchState,FarmBatchDetailsState.class);
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedFarmState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("No filed batchNo found");
        }
    }

    @GetMapping(value = "/farmstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TomatoFarmState> getFarmState(@PathVariable("batchNo") String batchNo) {

        try {

            //QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);
            //QueryCriteria criteria = generalCriteria.and(customCriteria);
            Vault.Page<TomatoFarmState> results = proxy.vaultQueryByCriteria(customCriteria, TomatoFarmState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            TomatoFarmState farmState = results.getStates().get(0).getState().getData();
            TomatoFarmState convertedFarmState=converter.getObjectMapper().convertValue(farmState,TomatoFarmState.class);



//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedFarmState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Bad request");
        }
    }


    /*Logistic states fetching api */

    @GetMapping(value = "/logisticstates/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LogisticStateResponseDto>> getAllLogisticState(@PathVariable("status") String status) {

        try {
            Vault.StateStatus stateStatus;
            switch(status.toLowerCase()) {
                case "unconsumed":
                    stateStatus = Vault.StateStatus.UNCONSUMED;
                    break;
                case "consumed":
                    stateStatus = Vault.StateStatus.CONSUMED;
                    break;
                case "all":
                    stateStatus=Vault.StateStatus.ALL;
                    break;
                default:
                    throw new IllegalArgumentException("Status provided is incorrect");
            }
            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(stateStatus);
//            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
//            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
//            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria);
//
//            QueryCriteria criteria = generalCriteria.and(customCriteria);
            Vault.Page<TomatoLogisticState> results = proxy.vaultQueryByCriteria(generalCriteria, TomatoLogisticState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            List<LogisticStateResponseDto> logisticStates=results.getStates().stream().map(i->i.getState().getData()).collect(Collectors.toList()).stream().map(j->new LogisticStateResponseDto(
                    j.getLogisiticId(),j.getWayBillNo(),j.getPickupDate(),j.getPickupLocation(),j.getTempInTransit(),j.getDropDate(),
                    j.getDropLocation(),j.getBatchNo(),Optional.ofNullable(j.getCarrier()).isPresent()?j.getCarrier().toString():null
            )).collect(Collectors.toList());
            //TomatoLogisticState logisticState = results.getStates().get(0).getState().getData();


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
//            LogisticStateResponseDto response = new LogisticStateResponseDto(logisticState.getLogisiticId(),logisticState.getWayBillNo(),
//                    logisticState.getPickupDate(),
//                    logisticState.getPickupLocation(),
//                    logisticState.getTempInTransit(),
//                    logisticState.getDropDate(),
//                    logisticState.getDropLocation(),
//                    logisticState.getBatchNo(),
//                    Optional.ofNullable(logisticState.getCarrier()).isPresent() ? logisticState.getCarrier().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(logisticStates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList());
        }
    }

    //For fetching batch details Logistic side

    @GetMapping(value = "/logisticbatchdetailstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LogisticBatchDetailsState> getLogisticBatchDetailsState(@PathVariable("batchNo") String batchNo) {

        try {

            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
//             FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
//             CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
//            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);

            //QueryCriteria criteria = generalCriteria.and(customCriteria);
            //Vault.Page<LogisticBatchDetailsState> results = proxy.vaultQueryByCriteria(customCriteria, LogisticBatchDetailsState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
//            List<LogisticBatchDetailsResponse> logisticStates=results.getStates().stream().map(i->i.getState().getData()).collect(Collectors.toList()).
//                    stream().map(j->converter.getObjectMapper().convertValue(j,LogisticBatchDetailsResponse.class)).collect(Collectors.toList());

            Vault.Page<LogisticBatchDetailsState> tempResults=proxy.vaultQueryByCriteria(generalCriteria,LogisticBatchDetailsState.class);
            List<StateAndRef<LogisticBatchDetailsState>>  logisticBatchDetailsStates=tempResults.getStates();
            //LogisticBatchDetailsState logisticState = results.getStates().get(0).getState().getData();


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
            StateAndRef<LogisticBatchDetailsState> inputFarmStateAndRef = logisticBatchDetailsStates
                    .stream().filter(farmStateAndRef-> {
                        LogisticBatchDetailsState logisticBatchState = farmStateAndRef.getState().getData();

                        return (logisticBatchState.getBatchNo().equals(batchNo));
                    }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
            LogisticBatchDetailsState logisticBatchState=inputFarmStateAndRef.getState().getData();
            LogisticBatchDetailsState convertedLogisticState=converter.getObjectMapper().convertValue(logisticBatchState,LogisticBatchDetailsState.class);

//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedLogisticState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("No filed for batchNo found");
        }
    }

    @GetMapping(value = "/logisticstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TomatoLogisticState> getLogisticState(@PathVariable("batchNo") String batchNo) {

        try {

            //QueryCriteria generalCriteria=new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoLogisticStateSchemaV1.PersistentToken.class);
            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);
            //QueryCriteria criteria=generalCriteria.and(customCriteria);
            Vault.Page<TomatoLogisticState> results = proxy.vaultQueryByCriteria(customCriteria, TomatoLogisticState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            TomatoLogisticState logisticState = results.getStates().get(0).getState().getData();
            TomatoLogisticState convertedlogisticState=converter.getObjectMapper().convertValue(logisticState,TomatoLogisticState.class);


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedlogisticState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Bad request");
        }
    }


    /*Distributor states fetching api */

    @GetMapping(value = "/distributorstates/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Logistic_DistributorFlowDto>> getAllDistributorState(@PathVariable("status") String status) {

        try {
            Vault.StateStatus stateStatus;
            switch(status.toLowerCase()) {
                case "unconsumed":
                    stateStatus = Vault.StateStatus.UNCONSUMED;
                    break;
                case "consumed":
                    stateStatus = Vault.StateStatus.CONSUMED;
                    break;
                case "all":
                    stateStatus = Vault.StateStatus.ALL;
                    break;
                default:
                    throw new IllegalArgumentException("Status enetered is wrong");
            }
            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(stateStatus);
//            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
//            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
//            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria);
//
//            QueryCriteria criteria = generalCriteria.and(customCriteria);
            Vault.Page<TomatoDistributorState> results = proxy.vaultQueryByCriteria(generalCriteria, TomatoDistributorState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            List<Logistic_DistributorFlowDto> distributorStates=results.getStates().stream().map(i->i.getState().getData()).collect(Collectors.toList()).stream().map(j->new Logistic_DistributorFlowDto(
                    Optional.ofNullable(j.getDistributor()).isPresent()?j.getDistributor().toString():null,
                    j.getDistributorId(),
                    j.getWayBillNo(),
                    j.getReceivedDate(),
                    j.getStorageTemp(),
                    j.getBatchNo())).collect(Collectors.toList());
            TomatoDistributorState distributorState = results.getStates().get(0).getState().getData();


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
//            Logistic_DistributorFlowDto response=new Logistic_DistributorFlowDto(Optional.ofNullable(distributorState.getDistributor()).isPresent()?distributorState.getDistributor().toString():null,
//                                                                                distributorState.getDistributorId(),
//                                                                                distributorState.getWayBillNo(),
//                                                                                distributorState.getReceivedDate(),
//                                                                                distributorState.getStorageTemp(),
//                                                                                distributorState.getBatchNo());
          return ResponseEntity.status(HttpStatus.OK).body(distributorStates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList());
        }
    }

    @GetMapping(value = "/distributorbatchdetailstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DistributorBatchDetailsState> getDistributorBatchDetailsState(@PathVariable("batchNo") String batchNo) {

        try {

            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
//             FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
//             CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
//            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);

            //QueryCriteria criteria = generalCriteria.and(customCriteria);
            // Vault.Page<DistributorBatchDetailsState> results = proxy.vaultQueryByCriteria(customCriteria, DistributorBatchDetailsState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            // List<DistributorBatchDetailsState> distributorStates=results.getStates().stream().map(i->i.getState().getData()).map(k->converter.getObjectMapper().convertValue(k,DistributorBatchDetailsState.class)).collect(Collectors.toList());
            // DistributorBatchDetailsState distributorState = results.getStates().get(0).getState().getData();
            Vault.Page<DistributorBatchDetailsState> results=proxy.vaultQueryByCriteria(generalCriteria,DistributorBatchDetailsState.class);
            List<StateAndRef<DistributorBatchDetailsState>> distributorBatchDetailsStates=results.getStates();


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
            StateAndRef<DistributorBatchDetailsState> inputFarmStateAndRef = distributorBatchDetailsStates
                    .stream().filter(farmStateAndRef-> {
                        DistributorBatchDetailsState distributorBatchDetailsState = farmStateAndRef.getState().getData();

                        return (distributorBatchDetailsState.getBatchNo().equals(batchNo));
                    }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
            DistributorBatchDetailsState distributorBatchDetailsState=inputFarmStateAndRef.getState().getData();
            DistributorBatchDetailsState convertedDistributorState=converter.getObjectMapper().convertValue(distributorBatchDetailsState,DistributorBatchDetailsState.class);
            //        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedDistributorState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("No field for batchNo found");
        }
    }

    @GetMapping(value = "/distributorstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TomatoDistributorState> getDistributorState(@PathVariable("batchNo") String batchNo) {

        try {
            //QueryCriteria generalCriteria=new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoDistributorStateSchemaV1.PersistentToken.class);
            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);
            //QueryCriteria criteria=generalCriteria.and(customCriteria);
            Vault.Page<TomatoDistributorState> results = proxy.vaultQueryByCriteria(customCriteria, TomatoDistributorState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            TomatoDistributorState distributorState = results.getStates().get(0).getState().getData();
            TomatoDistributorState converteddistributorState=converter.getObjectMapper().convertValue(distributorState,TomatoDistributorState.class);


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(converteddistributorState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Bad request");
        }
    }

    //Restaurant states fetching API

    @GetMapping(value = "/restaurantstates/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Distributor_RestaurantFlowDto>> getAllRestaurantStates(@PathVariable("status") String status) {

        try {
            Vault.StateStatus stateStatus;
            switch(status.toLowerCase()) {
                case "unconsumed":
                    stateStatus = Vault.StateStatus.UNCONSUMED;
                    break;
                case "consumed":
                    stateStatus = Vault.StateStatus.CONSUMED;
                    break;
                case "all":
                    stateStatus = Vault.StateStatus.ALL;
                    break;
                default:
                    throw new IllegalArgumentException("Status enetered is wrong");
            }
            QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(stateStatus);
//            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
//            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
//            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria);
//
//            QueryCriteria criteria = generalCriteria.and(customCriteria);
            Vault.Page<TomatoRestaurantState> results = proxy.vaultQueryByCriteria(generalCriteria, TomatoRestaurantState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            List<Distributor_RestaurantFlowDto> restaurantStates=results.getStates().stream().map(i->i.getState().getData()).collect(Collectors.toList()).stream().map(j->new Distributor_RestaurantFlowDto(
                              Optional.ofNullable(j.getRestaurant()).isPresent()?j.getRestaurant().toString():null,
                    j.getRestaurantId(),
                    j.getName(),
                    j.getWareHouseInfo(),
                    j.getPurchaseOrder(),
                    j.getReceivedDate(),
                    j.getBatchNo())).collect(Collectors.toList());
           // TomatoRestaurantState restaurantState = results.getStates().get(0).getState().getData();


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
//           Distributor_RestaurantFlowDto response=new Distributor_RestaurantFlowDto(Optional.ofNullable(restaurantState.getRestaurant()).isPresent()?restaurantState.getRestaurant().toString():null,
//                   restaurantState.getRestaurantId(),
//                   restaurantState.getName(),
//                   restaurantState.getWareHouseInfo(),
//                   restaurantState.getPurchaseOrder(),
//                   restaurantState.getReceivedDate(),
//                   restaurantState.getBatchNo()
                   //);
            return ResponseEntity.status(HttpStatus.OK).body(restaurantStates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(new ArrayList());
        }
    }


    ///To fetch BatchFarmDetails state






    @GetMapping(value = "/restaurantbatchdetailstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantBatchDetailsState> getRestaurantBatchDetailsState(@PathVariable("batchNo") String batchNo) {

        try {

           QueryCriteria generalCriteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
//            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
//             CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
//            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);

            //QueryCriteria criteria = generalCriteria.and(customCriteria);
           // Vault.Page<RestaurantBatchDetailsState> results = proxy.vaultQueryByCriteria(customCriteria, RestaurantBatchDetailsState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            //List<RestaurantBatchDetailsState> restaurantStates=results.getStates().stream().map(i->i.getState().getData()).map(k->converter.getObjectMapper().convertValue(k,RestaurantBatchDetailsState.class)).collect(Collectors.toList());
            //  RestaurantBatchDetailsState restaurantState = results.getStates().get(0).getState().getData();
           Vault.Page<RestaurantBatchDetailsState> results=proxy.vaultQueryByCriteria(generalCriteria,RestaurantBatchDetailsState.class);


            List<StateAndRef<RestaurantBatchDetailsState>> restaurantBatchDetailsStates=results.getStates();
//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
        StateAndRef<RestaurantBatchDetailsState> inputFarmStateAndRef = restaurantBatchDetailsStates
                .stream().filter(farmStateAndRef-> {
                    RestaurantBatchDetailsState restaurantBatchDetailsState = farmStateAndRef.getState().getData();

                    return (restaurantBatchDetailsState.getBatchNo().equals(batchNo));
                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
        RestaurantBatchDetailsState restaurantBatchDetailsState=inputFarmStateAndRef.getState().getData();
            RestaurantBatchDetailsState convertedRestaurantState=converter.getObjectMapper().convertValue(restaurantBatchDetailsState,RestaurantBatchDetailsState.class);
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedRestaurantState);
        } catch (Exception e) {
            e.printStackTrace();
           throw new IllegalArgumentException("No field for batchNo found");
        }
    }









    @GetMapping(value = "/restaurantstate/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TomatoRestaurantState> getRestaurantState(@PathVariable("batchNo") String batchNo) {

        try {
           // QueryCriteria generalCriteria=new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL);
            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoRestaurantStateSchemaV1.PersistentToken.class);
            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);
            //QueryCriteria criteria=generalCriteria.and(customCriteria);
            Vault.Page<TomatoRestaurantState> results = proxy.vaultQueryByCriteria(customCriteria, TomatoRestaurantState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            TomatoRestaurantState restaurantState = results.getStates().get(0).getState().getData();
            TomatoRestaurantState convertedRestaurantState=converter.getObjectMapper().convertValue(restaurantState,TomatoRestaurantState.class);


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedRestaurantState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Bad request");
        }
    }


    //Flow for Creating an account in a node

    @PostMapping(value = "/accountgenerate/{acctName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<MainResponseDTO<String>> accountGenerate(@PathVariable("acctName") String accountName) {

        MainResponseDTO<String> response=new MainResponseDTO();

        try {
            String accountString = proxy.startTrackedFlowDynamic(CreateNewAccount.class,
                    accountName
                   ).getReturnValue().get();
            System.out.println("Transaction Done" + accountString);
            logger.info(accountString);

            response.setResponse("Transaction id "+accountString+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            response.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }


    //Flow for sharing the account info to all the counterparties which is required for this node to do a transaction

    @PostMapping(value = "/shareaccount/{acctName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<MainResponseDTO<String>> shareAccount(@PathVariable("acctName") String acctName,@RequestBody MainRequestDTO<List<String>> request) {

        MainResponseDTO<String> response=new MainResponseDTO();



        try {
            List<Party> allParties=request.getRequest().stream().map(it->proxy.wellKnownPartyFromX500Name(CordaX500Name.parse(it))).collect(Collectors.toList());

            String accountString = proxy.startTrackedFlowDynamic(ShareAccount.class,acctName
                    ,allParties
            ).getReturnValue().get();
            System.out.println("Transaction Done" + accountString);
            logger.info(accountString);

            response.setResponse("Transaction id "+accountString+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            response.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }



    //Flow call for making tomato batch for account in farmer node


    @PostMapping(value = "/tomatoBatchFarmAccountGenerate", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    private ResponseEntity<MainResponseDTO<String>> tomatoBatchFarmAccountGenerate(@RequestBody FarmBatchCreateDto requestDto) {

        MainResponseDTO<String> response=new MainResponseDTO();
        String batchNo = requestDto.getBatchNo();
        if (batchNo == null) {
            response.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(FarmAccountTomatoBatchIssueFlow.class,
                    "RameshFarmer",
                    requestDto.getFarmId(),
                    requestDto.getBatchNo(),
                    requestDto.getLocation(),
                    requestDto.getHarvestDate(),
                    requestDto.getMeanTemp(),
                    requestDto.getMeanNitrogen(),
                    requestDto.getCategory()).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());

            response.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            response.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping(value = "/farmAccountLogisticFlow/{farmerAcctName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MainResponseDTO<String>> farmAccountLogisticFlow(@PathVariable("farmerAcctName") String farmerAcctName,@RequestBody Farm_LogisticFlowDto requestDto) {

        MainResponseDTO<String> responseDTO=new MainResponseDTO<>();
        String batchNo = requestDto.getBatchNo();
        String carrierName = requestDto.getCarrierName();


        if (carrierName == null) {
            responseDTO.setResponse("Query parameter 'supplierName' must not be null.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        if (batchNo == null) {
            responseDTO.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }
        CordaX500Name partyX500Name = CordaX500Name.parse(carrierName);
        Party carrier = proxy.wellKnownPartyFromX500Name(partyX500Name);

        if (!(carrier instanceof Party)) {
            responseDTO.setResponse("Party named " + carrierName +" cannot be found.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }


        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(FarmerAccount_LogisticNodeBatchFlow.class,
                    requestDto.getLogisticId(),
                    requestDto.getWaybillNo(),
                    requestDto.getPickupDate(),
                    requestDto.getPickupLocation(),
                    requestDto.getTempInTransit(),
                    requestDto.getDropDate(),
                    requestDto.getDropLocation(),
                    requestDto.getBatchNo(),
                    carrier,farmerAcctName).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());
            responseDTO.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            responseDTO.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    //FarmerAccount to LogisticAccountFlow

    @PostMapping(value = "/farmAccountLogisticAccountFlow/{farmerAcctName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MainResponseDTO<String>> farmAccountLogisticAccountFlow(@PathVariable("farmerAcctName") String farmerAcctName,@RequestBody Farm_LogisticFlowDto requestDto) {

        MainResponseDTO<String> responseDTO=new MainResponseDTO<>();
        String batchNo = requestDto.getBatchNo();
        String carrierName = requestDto.getCarrierName();


        if (carrierName == null) {
            responseDTO.setResponse("Query parameter 'supplierName' must not be null.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        if (batchNo == null) {
            responseDTO.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }
//        CordaX500Name partyX500Name = CordaX500Name.parse(carrierName);
//        Party carrier = proxy.wellKnownPartyFromX500Name(partyX500Name);
//
//        if (!(carrier instanceof Party)) {
//            responseDTO.setResponse("Party named " + carrierName +" cannot be found.\n");
//            return ResponseEntity.badRequest().body(responseDTO);
//        }


        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(FarmAccount_LogisticAccountBatchFlow.class,
                    farmerAcctName,requestDto.getCarrierName(),
                    requestDto.getLogisticId(),
                    requestDto.getWaybillNo(),
                    requestDto.getPickupDate(),
                    requestDto.getPickupLocation(),
                    requestDto.getTempInTransit(),
                    requestDto.getDropDate(),
                    requestDto.getDropLocation(),
                    requestDto.getBatchNo()
                    ).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());
            responseDTO.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            responseDTO.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    @GetMapping(value = "/accountState/{acctName}/{batchNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TomatoFarmState> getFarmAccountState(@PathVariable("acctName") String acctName,@PathVariable("batchNo") String batchNo) {

        try {
            AccountInfo accountInfo=proxy.startTrackedFlowDynamic(AccountInfoByName.class,acctName).getReturnValue().get().get(0).getState().getData();
            QueryCriteria generalCriteria=new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL).withExternalIds(ImmutableList.of(accountInfo.getIdentifier().getId())).withStatus(Vault.StateStatus.ALL);
            FieldInfo info1BatchNo = QueryCriteriaUtils.getField("batchNo", TomatoFarmStateSchemaV1.PersistentToken.class);
            CriteriaExpression infoBatchCriteria = Builder.equal(info1BatchNo, batchNo);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(infoBatchCriteria, Vault.StateStatus.ALL);
            QueryCriteria criteria=generalCriteria.and(customCriteria);
            Vault.Page<TomatoFarmState> results = proxy.vaultQueryByCriteria(criteria, TomatoFarmState.class);
            //System.out.println(results.getStates().get(0).getState().getData().getBatchNo());
            TomatoFarmState farmState = results.getStates().get(0).getState().getData();
            TomatoFarmState convertedFarmState=converter.getObjectMapper().convertValue(farmState,TomatoFarmState.class);


//        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=proxy.vaultQuery(TomatoFarmState.class).getStates();
//        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
//                .stream().filter(farmStateAndRef-> {
//                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
//
//                    return (farmState.getBatchNo().equals(batchNo));
//                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));
//        TomatoFarmState farmState=inputFarmStateAndRef.getState().getData();
//        System.out.println(inputFarmStateAndRef.getState().getData().getBatchNo());
            //FarmBatchCreateDtoResponse response = new FarmBatchCreateDtoResponse(farmState.getFarmId(), farmState.getBatchNo(), farmState.getLocation(), farmState.getHarvestDate(), farmState.getMeanTemp(), farmState.getMeanNitrogen(), farmState.getCategory(), farmState.getSupplier().toString(), Optional.ofNullable(farmState.getSuppliedTo()).isPresent() ? farmState.getSuppliedTo().toString() : null);
            return ResponseEntity.status(HttpStatus.OK).body(convertedFarmState);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Bad request");
        }
    }

    //Flow from logistic account to distributor account


    @PostMapping(value = "/logisticAccountDistributorAccountFlow/{logisticAcctName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MainResponseDTO<String>> logisticAccountDistributorAccountFlow(@PathVariable("logisticAcctName") String logisticAcctName,@RequestBody Logistic_DistributorFlowDto requestDto) {

        MainResponseDTO<String> responseDTO=new MainResponseDTO<>();
        String batchNo = requestDto.getBatchNo();
        String carrierName = requestDto.getDistributorName();


        if (carrierName == null) {
            responseDTO.setResponse("Query parameter 'supplierName' must not be null.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        if (batchNo == null) {
            responseDTO.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }
//        CordaX500Name partyX500Name = CordaX500Name.parse(carrierName);
//        Party carrier = proxy.wellKnownPartyFromX500Name(partyX500Name);
//
//        if (!(carrier instanceof Party)) {
//            responseDTO.setResponse("Party named " + carrierName +" cannot be found.\n");
//            return ResponseEntity.badRequest().body(responseDTO);
//        }


        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(LogisticAccount_DistributorAccountBatchFlow.class,
                   logisticAcctName,
                    requestDto.getDistributorName(),
                    requestDto.getDistributorId(),
                    requestDto.getWayBillNo(),
                    requestDto.getReceivedDate(),
                    requestDto.getStorageTemp(),
                    requestDto.getBatchNo()

            ).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());
            responseDTO.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            responseDTO.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    //Flow from distributor Account to restaurant account


    @PostMapping(value = "/distributorAccountRestaurantAccountFlow/{distributorAcctName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MainResponseDTO<String>> distributorAccountRestaurantAccountFlow(@PathVariable("distributorAcctName") String distributorAcctName,@RequestBody Distributor_RestaurantFlowDto requestDto) {

        MainResponseDTO<String> responseDTO=new MainResponseDTO<>();
        String batchNo = requestDto.getBatchNo();
        String carrierName = requestDto.getRestaurantPartyName();


        if (carrierName == null) {
            responseDTO.setResponse("Query parameter 'supplierName' must not be null.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }

        if (batchNo == null) {
            responseDTO.setResponse("Query parameter 'iouValue' must be non-negative.\n");
            return ResponseEntity.badRequest().body(responseDTO);
        }
//        CordaX500Name partyX500Name = CordaX500Name.parse(carrierName);
//        Party carrier = proxy.wellKnownPartyFromX500Name(partyX500Name);
//
//        if (!(carrier instanceof Party)) {
//            responseDTO.setResponse("Party named " + carrierName +" cannot be found.\n");
//            return ResponseEntity.badRequest().body(responseDTO);
//        }


        try {
            SignedTransaction signedTx = proxy.startTrackedFlowDynamic(DistributorAccount_RestaurantAccountFlow.class,
                   distributorAcctName,
                    requestDto.getRestaurantPartyName(),
                    requestDto.getRestaurantId(),
                    requestDto.getName(),
                    requestDto.getWareHouseInfo(),
                    requestDto.getPurchaseOrder(),
                    requestDto.getReceivedDate(),
                    requestDto.getBatchNo()

            ).getReturnValue().get();
            System.out.println("Transaction Done" + batchNo);
            logger.info(signedTx.getTx().getCommands().get(0).toString());
            responseDTO.setResponse("Transaction id "+signedTx.getId()+"committed to ledger.\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            responseDTO.setResponse(ex.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

}