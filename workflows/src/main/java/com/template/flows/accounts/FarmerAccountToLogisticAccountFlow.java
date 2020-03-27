package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;

import java.security.PublicKey;
import java.time.LocalDate;


@StartableByRPC
@StartableByService
@InitiatingFlow
public class FarmerAccountToLogisticAccountFlow extends FlowLogic<String> {

    private String farmAccountName;
    private String logisticAccountName;
    private String logisticId;
    private String wayBillNo;
    private LocalDate pickupDate;
    private String pickupLocation;
    private Double tempInTransit;
    private LocalDate dropDate;
    private String dropLocation;
    private String batchNo;

    public FarmerAccountToLogisticAccountFlow(String farmAccountName, String logisticAccountName, String logisticId, String wayBillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo) {
        this.farmAccountName = farmAccountName;
        this.logisticAccountName = logisticAccountName;
        this.logisticId = logisticId;
        this.wayBillNo = wayBillNo;
        this.pickupDate = pickupDate;
        this.pickupLocation = pickupLocation;
        this.tempInTransit = tempInTransit;
        this.dropDate = dropDate;
        this.dropLocation = dropLocation;
        this.batchNo = batchNo;
    }

    @Suspendable
    public String call() throws FlowException {

        //generating Key for Account
       AccountInfo farmAccount= getServiceHub().cordaService(AccountService.class).accountInfo(farmAccountName).get(0).getState().getData();

      PublicKey myKey=subFlow(new NewKeyForAccount(farmAccount.getIdentifier().getId())).getOwningKey();

      AccountInfo logiticAccount=getServiceHub().cordaService(AccountService.class).accountInfo(logisticAccountName).get(0).getState().getData();
      AnonymousParty logisticKey=subFlow(new RequestKeyForAccount(logiticAccount));

      //generating state for transfer



      return null;
    }
}
