package com.ll.medium.global.app;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class AppConfig {
    private static String resourcesStaticDirPath;

    @Getter
    public static String tempDirPath;

    @Getter
    public static String genFileDirPath;

    @Getter
    public static String siteName;

    @Getter
    public static String siteBaseUrl;

    @Getter
    private static String tossPaymentsWidgetSecretKey;

    @Value("${custom.tempDirPath}")
    public void setTempDirPath(String tempDirPath) {
        AppConfig.tempDirPath = tempDirPath;
    }

    @Value("${custom.genFile.dirPath}")
    public void setGenFileDirPath(String genFileDirPath) {
        AppConfig.genFileDirPath = genFileDirPath;
    }

    @Value("${custom.site.name}")
    public void setSiteName(String siteName) {
        AppConfig.siteName = siteName;
    }

    @Value("${custom.site.baseUrl}")
    public void setSiteBaseUrl(String siteBaseUrl) {
        AppConfig.siteBaseUrl = siteBaseUrl;
    }

    public static String getResourcesStaticDirPath() {
        if (resourcesStaticDirPath == null) {
            ClassPathResource resource = new ClassPathResource("static/");
            try {
                resourcesStaticDirPath = resource.getFile().getAbsolutePath();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return resourcesStaticDirPath;
    }

    @Value("${custom.tossPayments.widget.secretKey}")
    public void setTossPaymentsWidgetSecretKey(String tossPaymentsWidgetSecretKey) {
        AppConfig.tossPaymentsWidgetSecretKey = tossPaymentsWidgetSecretKey;
    }
}