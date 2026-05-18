package org.bravatools.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.bravatools.brava.model.spec.SupportedSpecs;
import org.bravatools.brava.model.spec.Version;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "brapi.schema")
@Getter
@Setter
/**
 * Loads schema properties for BRAVA like supported versions and where to get schemas for BrAPI modules.
 */
public class BrAPISchemaProperties {

    // Supported BrAPI versions.  Comes from brapi.schema.versions in application.properties
    private List<String> versions = new ArrayList<>();

    private Map<String, Map<String, String>> urls = new HashMap<>();

    private SupportedSpecs supportedSpecs;

    @PostConstruct
    public void init() {
        if (this.versions.isEmpty()) {
            throw new IllegalStateException("No BrAPI schema versions configured");
        }

        if (urls.isEmpty()) {
            throw new IllegalStateException("No BrAPI schema URLs configured");
        }

        supportedSpecs = new SupportedSpecs();

        List<Version> supportedVersions = new ArrayList<>();

        for (String versionProperty : this.versions) {
            var versionMatchUrlMap = "v" + versionProperty.replace('.', '_');

            Version version = new Version();

            version.setName(versionProperty);

            List<String> moduleUrls = new ArrayList<>();

            urls.get(versionMatchUrlMap).forEach((k, v) -> moduleUrls.add(v));

            version.setModuleUrls(moduleUrls);

            if (version.getModuleUrls().isEmpty()) {
                throw new IllegalStateException(String.format("No BrAPI schema module URLs configured for version [%s]", version.getName()));
            }

            supportedVersions.add(version);
        }

        supportedSpecs.setVersions(supportedVersions);

        if (supportedSpecs.getVersions().isEmpty()) {
            throw new IllegalStateException("Unable to configure SupportedSpecs with supported versions");
        }
    }
}
