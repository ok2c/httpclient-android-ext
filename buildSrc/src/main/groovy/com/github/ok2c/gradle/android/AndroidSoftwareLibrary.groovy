/*
 * Copyright 2019, OK2 Consulting Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ok2c.gradle.android

import com.android.build.gradle.tasks.BundleAar
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ExcludeRule
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Usage
import org.gradle.api.capabilities.Capability
import org.gradle.api.internal.artifacts.configurations.Configurations
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.attributes.ImmutableAttributes
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory

class AndroidSoftwareLibrary implements SoftwareComponentInternal {

    private final ObjectFactory objectFactory
    private final ConfigurationContainer configurations
    private final ImmutableAttributesFactory attributesFactory
    private final BundleAar bundleAar
    private final String name

    AndroidSoftwareLibrary(ObjectFactory objectFactory,
                           ConfigurationContainer configurations,
                           ImmutableAttributesFactory attributesFactory,
                           BundleAar bundleAar) {
        this.objectFactory = objectFactory
        this.configurations = configurations
        this.attributesFactory = attributesFactory
        this.bundleAar = bundleAar
        this.name = bundleAar.variantName + 'AndroidLibrary'
    }

    @Override
    Set<? extends UsageContext> getUsages() {
        return [new InternalUsageContext('api'), new InternalUsageContext('runtime')]
    }

    @Override
    String getName() {
        return name
    }

    private final class InternalUsageContext implements UsageContext {

        private final String name;
        private final String configurationName;
        private final Usage usage;
        private final ImmutableAttributes attributes;

        InternalUsageContext(String name) {
            this.name = name
            this.configurationName = bundleAar.variantName + name.capitalize() + 'Elements'
            this.usage = objectFactory.named(Usage.class, 'java-' + name)
            this.attributes = attributesFactory.of(Usage.USAGE_ATTRIBUTE, this.usage)
        }

        @Override
        String getName() {
            return name
        }

        @Override
        AttributeContainer getAttributes() {
            return this.attributes
        }

        @Override
        Usage getUsage() {
            return this.usage
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            return [new ArchivePublishArtifact(bundleAar)]
        }

        @Override
        Set<? extends ModuleDependency> getDependencies() {
            Configuration configuration = configurations.getByName(configurationName)
            return configuration.getIncoming().getDependencies().withType(ModuleDependency.class)
        }

        @Override
        Set<? extends DependencyConstraint> getDependencyConstraints() {
            Configuration configuration = configurations.getByName(configurationName)
            return configuration.getIncoming().getDependencyConstraints()
        }

        @Override
        Set<? extends Capability> getCapabilities() {
            Configuration configuration = configurations.getByName(configurationName)
            return Configurations.collectCapabilities(configuration, new HashSet<>(), new HashSet<>())
        }

        @Override
        Set<ExcludeRule> getGlobalExcludes() {
            Configuration configuration = configurations.getByName(configurationName)
            return configuration.getExcludeRules()
        }

    }

}
