package com.ks.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ks.customize.CustomIotaApi;
import com.ks.util.CommonConstants;

import jota.dto.response.GetNewAddressResponse;
import jota.dto.response.SendTransferResponse;
import jota.error.ArgumentException;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.pow.pearldiver.PearlDiverLocalPoW;
import jota.utils.TrytesConverter;

@Controller
@RequestMapping
public class IotaController {

    CustomIotaApi api = new CustomIotaApi.Builder().protocol("https")
        .host("nodes.thetangle.org")
        .port("443")
        .localPoW(new PearlDiverLocalPoW())
        .build();

    @RequestMapping(value = "/iota", method = RequestMethod.GET)
    public ModelAndView app() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("titleHome", "ks - IOTA");
        modelAndView.setViewName("landing");
        return modelAndView;
    }
    
    @RequestMapping(value = "/message", method = RequestMethod.POST)
    public ModelAndView sign(@RequestParam("seed") String seed, @RequestParam("tag") String tag, @RequestParam("msg") String msg) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("titleHome", "ks - IOTA");
        modelAndView.setViewName("landing");
        try {
            GetNewAddressResponse resp = api.generateNewAddresses(seed, 3, true, 1);
            String address = resp.first();
           
            String messageTrytes = TrytesConverter.asciiToTrytes(msg);
            String tagTrytes = TrytesConverter.asciiToTrytes(tag);
            Transfer transfer = new Transfer(address, 0, messageTrytes, tagTrytes);

            SendTransferResponse respo = api.sendTransfer(seed, 2, 5, 14, Collections.singletonList(transfer), null, null, false, true, null);
            System.err.println(respo.getTransactions().get(0).getHash());
            modelAndView.addObject("txn", respo.getTransactions().get(0).getHash());
            return modelAndView;

        } catch (ArgumentException e) {
            
            e.printStackTrace();
            
            return modelAndView;
        }
       
    }

    @RequestMapping(value = "/retrieveMessage", method = RequestMethod.POST)
    public ModelAndView message(@RequestParam("txn") String txn) throws ArgumentException {

        ModelAndView modelAndView = new ModelAndView();
        String [] hashes = new String[1];
        hashes[0] = txn;
        List<Transaction> txns = api.findTransactionsObjectsByHashes(hashes);
        String frag = txns.get(0).getSignatureFragments();        
        String tag = txns.get(0).getTag();
        String msg = TrytesConverter.trytesToAscii(frag);
        String tagStr = TrytesConverter.trytesToAscii(tag);
        modelAndView.addObject("titleHome", "ks - IOTA");
        modelAndView.addObject("tag", tagStr);
        modelAndView.addObject("msg", msg);
        modelAndView.addObject("txn", txn);
        modelAndView.setViewName("landing");
        return modelAndView;
    }
}
