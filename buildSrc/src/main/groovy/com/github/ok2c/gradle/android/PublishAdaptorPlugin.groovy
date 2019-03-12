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
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

class PublishAdaptorPlugin implements Plugin<Project> {

    private final ObjectFactory objectFactory
    private final ImmutableAttributesFactory attributesFactory

    @Inject
    PublishAdaptorPlugin(ObjectFactory objectFactory, ImmutableAttributesFactory attributesFactory) {
        this.objectFactory = objectFactory
        this.attributesFactory = attributesFactory
    }

    void apply(Project project) {
        project.afterEvaluate(new Action<Project>() {

            @Override
            void execute(Project p) {
                p.tasks.withType(BundleAar, new Action<BundleAar>() {

                    @Override
                    void execute(BundleAar bundle) {
                        p.components.add(new AndroidSoftwareLibrary(objectFactory, p.configurations, attributesFactory, bundle))
                    }

                })
            }

        })
    }

}
