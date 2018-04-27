/*
 * Copyright (c) 2017-2018 HERE Europe B.V.
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

package com.here.ort.analyzer

import com.here.ort.analyzer.managers.GoDep
import com.here.ort.downloader.VersionControlSystem
import com.here.ort.model.Project
import com.here.ort.model.VcsInfo
import com.here.ort.model.yamlMapper

import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.matchers.startWith
import io.kotlintest.specs.FreeSpec

import java.io.File

class GoDepTest : FreeSpec() {
    init {
        "GoDep should" - {
            "resolve dependencies from a lockfile correctly" {
                val projectDir = File("src/funTest/assets/projects/external/qmstr")
                val manifestFile = File(projectDir, "Gopkg.toml")
                val godep = GoDep.create()

                val result = godep.resolveDependencies(listOf(manifestFile))[manifestFile]
                val expectedResult = File(projectDir.parentFile, "qmstr-expected-output.yml").readText()

                yamlMapper.writeValueAsString(result) shouldBe expectedResult
            }

            "show error if no lockfile is present" {
                val projectDir = File("src/funTest/assets/projects/synthetic/godep/no-lockfile")
                val manifestFile = File(projectDir, "Gopkg.toml")
                val godep = GoDep.create()

                val result = godep.resolveDependencies(listOf(manifestFile))[manifestFile]

                result shouldNotBe null
                result!!.project shouldBe Project.EMPTY
                result.packages.size shouldBe 0
                result.errors.size shouldBe 1
                result.errors.first() should startWith("IllegalArgumentException: No lockfile found in")
            }

            "invoke the dependency solver if no lockfile is present and allowDynamicVersions is set" {
                val projectDir = File("src/funTest/assets/projects/synthetic/godep/no-lockfile")
                val manifestFile = File(projectDir, "Gopkg.toml")
                val godep = GoDep.create()

                val allowDynamicVersionsOriginal = Main.allowDynamicVersions
                Main.allowDynamicVersions = true
                val result = godep.resolveDependencies(listOf(manifestFile))[manifestFile]
                Main.allowDynamicVersions = allowDynamicVersionsOriginal

                result shouldNotBe null
                result!!.project shouldNotBe Project.EMPTY
                result.packages.size shouldBe 4
                result.errors.size shouldBe 0
            }

            "import dependencies from Glide" {
                val projectDir = File("src/funTest/assets/projects/external/sprig")
                val manifestFile = File(projectDir, "glide.yaml")
                val godep = GoDep.create()

                val result = godep.resolveDependencies(listOf(manifestFile))[manifestFile]
                val expectedResult = File(projectDir.parentFile, "sprig-expected-output.yml").readText()

                yamlMapper.writeValueAsString(result) shouldBe expectedResult
            }

            "import dependencies from godeps" {
                val projectDir = File("src/funTest/assets/projects/external/godep")
                val manifestFile = File(projectDir, "Godeps" + File.separator + "Godeps.json")
                val godep = GoDep.create()

                val result = godep.resolveDependencies(listOf(manifestFile))[manifestFile]
                val expectedResult = File(projectDir.parentFile, "godep-expected-output.yml").readText()

                yamlMapper.writeValueAsString(result) shouldBe expectedResult
            }

            "construct an import path from VCS info" {
                val godep = GoDep.create()
                val gopath = File("/tmp/gopath")
                val projectDir = File("src/funTest/assets/projects/external/qmstr")
                val vcs = VersionControlSystem.forDirectory(projectDir)!!.getInfo()

                val expectedPath = File("/tmp/gopath/src/github.com/QMSTR/qmstr.git").absoluteFile

                godep.deduceImportPath(projectDir, vcs, gopath) shouldBe expectedPath
            }

            "construct an import path for directories that are not repositories" {
                val godep = GoDep.create()
                val gopath = File("/tmp/gopath")
                val projectDir = File("src/funTest/assets/projects/synthetic/godep/no-lockfile")
                val vcs = VcsInfo.EMPTY

                val expectedPath = File("/tmp/gopath/src/no-lockfile").absoluteFile

                godep.deduceImportPath(projectDir, vcs, gopath) shouldBe expectedPath
            }
        }
    }
}
