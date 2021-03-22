package com.veracore.opensourcescannerapi.controllers;

import com.veracore.opensourcescannerapi.models.ScanRequest;
import com.veracore.opensourcescannerapi.models.ScanResult;
import com.veracore.opensourcescannerapi.services.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class ScannerController {

    @Autowired
    private ScannerService scannerService;

    @PostMapping(value = "/scan")
    @ResponseStatus(value = HttpStatus.OK)
    public ScanResult submitScan(@RequestBody @Valid ScanRequest request) throws IOException, URISyntaxException {
        return scannerService.initiateScan(request);
    }
}
