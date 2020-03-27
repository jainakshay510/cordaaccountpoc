package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AllAccounts;
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.ShareAccountInfoFlow;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;

import java.util.List;
import java.util.UUID;

@StartableByRPC
@StartableByService
@InitiatingFlow
public class ShareAccount extends FlowLogic<String> {

    private String acctNameShared;
    private List<Party> sharedTo;

    public ShareAccount(String acctNameShared, List<Party> sharedTo) {
        this.acctNameShared = acctNameShared;
        this.sharedTo = sharedTo;
    }

    @Suspendable
    public String call() throws FlowException {

        List<StateAndRef<AccountInfo>> allMyAccounts= (List<StateAndRef<AccountInfo>>) subFlow(new AllAccounts());
        System.out.println("Akshay+++++"+allMyAccounts.get(0).getState().getData().getName()+"......"+acctNameShared);
        StateAndRef<AccountInfo> sharedAccount=allMyAccounts.stream().filter(it->it.getState().getData().getName().equals(acctNameShared)).findFirst().get();
        subFlow(new ShareAccountInfo(sharedAccount, sharedTo));
        return "Shared " + acctNameShared + " with all the given parties" ;
    }
}
