package com.cinect.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class AdminContentSmokeTest {

    @Autowired
    private NewsService newsService;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private BannerService bannerService;

    @Test
    @Transactional(readOnly = true)
    void newsFindAllForAdminDoesNotThrow() {
        assertDoesNotThrow(() -> newsService.findAllForAdmin(0, 10));
    }

    @Test
    @Transactional(readOnly = true)
    void campaignsFindAllForAdminDoesNotThrow() {
        assertDoesNotThrow(() -> campaignService.findAllForAdmin());
    }

    @Test
    @Transactional(readOnly = true)
    void bannersFindAllForAdminDoesNotThrow() {
        assertDoesNotThrow(() -> bannerService.findAllForAdmin());
    }
}
