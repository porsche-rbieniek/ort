/*
 * Copyright (C) 2017-2019 HERE Europe B.V.
 * Copyright (C) 2022 Bosch.IO GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.cli.utils

import org.ossreviewtoolkit.model.utils.DirectoryPackageConfigurationProvider
import org.ossreviewtoolkit.model.utils.FilePackageConfigurationProvider
import org.ossreviewtoolkit.model.utils.PackageConfigurationProvider
import org.ossreviewtoolkit.utils.core.ORT_PACKAGE_CONFIGURATIONS_DIRNAME
import org.ossreviewtoolkit.utils.core.ortConfigDirectory

internal sealed class PackageConfigurationOption {
    data class Dir(val value: java.io.File) : PackageConfigurationOption()
    data class File(val value: java.io.File) : PackageConfigurationOption()
}

internal fun PackageConfigurationOption?.createProvider(): PackageConfigurationProvider =
    when (this) {
        is PackageConfigurationOption.Dir -> DirectoryPackageConfigurationProvider(value)
        is PackageConfigurationOption.File -> FilePackageConfigurationProvider(value)
        null -> {
            val globalPackageConfigurations = ortConfigDirectory.resolve(ORT_PACKAGE_CONFIGURATIONS_DIRNAME)
            DirectoryPackageConfigurationProvider(globalPackageConfigurations)
        }
    }
