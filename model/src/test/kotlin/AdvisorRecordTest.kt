/*
 * Copyright (C) 2021 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

package org.ossreviewtoolkit.model

import com.fasterxml.jackson.module.kotlin.readValue

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.haveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class AdvisorRecordTest : StringSpec({
    "AdvisorRecord with advisor results can be deserialized" {
        val yaml = """
            ---
            advisor_results:
              type:namespace:name:version:
              - vulnerabilities: []
                advisor:
                  name: "advisor"
                summary:
                  start_time: "1970-01-01T00:00:00Z"
                  end_time: "1970-01-01T00:00:00Z"
            has_issues: false
        """.trimIndent()

        val record = shouldNotThrowAny { yamlMapper.readValue<AdvisorRecord>(yaml) }
        record.advisorResults should haveSize(1)
        record.advisorResults.entries.single().let { (id, results) ->
            id shouldBe Identifier("type:namespace:name:version")
            results shouldHaveSize 1
        }
    }

    "Legacy AdvisorRecord with AdvisorResultContainers can be deserialized" {
        val yaml = """
            ---
            advisor_results:
            - id: "type:namespace:name:version"
              results:
              - vulnerabilities: []
                advisor:
                  name: "advisor"
                summary:
                  start_time: "1970-01-01T00:00:00Z"
                  end_time: "1970-01-01T00:00:00Z"
            has_issues: false
        """.trimIndent()

        val record = shouldNotThrowAny { yamlMapper.readValue<AdvisorRecord>(yaml) }
        record.advisorResults should haveSize(1)
        record.advisorResults.entries.single().let { (id, results) ->
            id shouldBe Identifier("type:namespace:name:version")
            results shouldHaveSize 1
        }
    }
})