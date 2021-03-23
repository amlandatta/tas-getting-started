package com.vmware.training.courseservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final Logger log = LoggerFactory.getLogger(HealthController.class);

    @GetMapping
    public String getHealth() {
        return "OK";
    }

    @GetMapping("/kill")
    public void killApp() {
        log.info("Manual kill triggered for demo purpose");
        System.exit(1);
    }

    @Value("${CF_INSTANCE_GUID:}")
    private String instanceId;

    @Value("${CF_INSTANCE_INDEX:}")
    private String instanceIndex;

    @Value("${CF_INSTANCE_IP:}")
    private String instanceIp;

    @Value("${APP_VERSION:}")
    private String appVersion;

    @GetMapping("/whereami")
    public String getInstanceId() {
        log.info("App instance Index: {} Id: {} Diego Cell IP: {}",
                instanceIndex, instanceId, instanceIp);
        return instanceIndex + ":" + instanceId + ":" + instanceIp;
    }

    @GetMapping("/version")
    public String getAppVersion() {
        log.info("App version: {}", appVersion);
        return appVersion;
    }
}
