package com.chinaunicom.torn.mcloud.controller;

import com.chinaunicom.torn.mcloud.service.InstanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/kickstart")
public class KickstartController {

    @Autowired
    private InstanceService instanceService;


    @GetMapping(path = "/init")
    public ResponseEntity<String> getInitShell(@RequestParam("sn") String sn) {
        return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "text/plain").body(this.instanceService.initShell(sn));
    }
}
