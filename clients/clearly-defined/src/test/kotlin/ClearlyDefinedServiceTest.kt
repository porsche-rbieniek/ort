/*
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

package org.ossreviewtoolkit.clients.clearlydefined

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

import kotlinx.serialization.encodeToString

class ClearlyDefinedServiceTest : StringSpec({
    val json = ClearlyDefinedService.JSON

    "Enums should be serialized correctly" {
        enumValues<ComponentType>().forAll {
            json.encodeToString(it) shouldBe "\"$it\""
        }

        enumValues<Provider>().forAll {
            json.encodeToString(it) shouldBe "\"$it\""
        }

        enumValues<Nature>().forAll {
            json.encodeToString(it) shouldBe "\"$it\""
        }

        enumValues<ContributionType>().forAll {
            json.encodeToString(it) shouldBe "\"$it\""
        }
    }
})
