/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.cleanup

import org.junit.jupiter.api.Test
import org.openrewrite.Issue
import org.openrewrite.Recipe
import org.openrewrite.java.JavaRecipeTest

@Suppress("UnnecessarySemicolon")
interface RemoveExtraSemicolonsTest : JavaRecipeTest {
    override val recipe: Recipe
        get() = RemoveExtraSemicolons()

    @Issue("https://github.com/openrewrite/rewrite/issues/1587")
    @Test
    fun enumSemicolons() = assertChanged(
        before = """
            public enum FRUITS {
                BANANA,
                APPLE;
            }
        """,
        after = """
            public enum FRUITS {
                BANANA,
                APPLE
            }
        """
    )

    @Issue("https://github.com/openrewrite/rewrite/issues/1587")
    @Test
    fun enumSemicolonsWithOtherStatements() = assertUnchanged(
        before = """
            public enum FRUITS {
                BANANA,
                APPLE;
                
                void hiFruit() {}
            }
        """
    )

    @Test
    fun emptyBlockStatements() = assertChanged(
        before = """
            class Test {
                void test() {
                    ;
                }
            }
        """,
        after = """
            class Test {
                void test() {
                }
            }
        """
    )

    @Test
    fun tryWithResources() = assertChanged(
        before = """
            import java.io.*;
            class Test {
                void test() {
                    try (ByteArrayInputStream b = new ByteArrayInputStream(new byte[10]);
                          Reader r = new InputStreamReader(b);) {
                    }
                }
            }
        """,
        after = """
            import java.io.*;
            class Test {
                void test() {
                    try (ByteArrayInputStream b = new ByteArrayInputStream(new byte[10]);
                          Reader r = new InputStreamReader(b)) {
                    }
                }
            }
        """
    )
}
