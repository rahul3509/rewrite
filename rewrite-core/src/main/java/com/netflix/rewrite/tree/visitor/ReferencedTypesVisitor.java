/*
 * Copyright 2020 the original authors.
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
 */
package com.netflix.rewrite.tree.visitor;

import com.netflix.rewrite.tree.NameTree;
import com.netflix.rewrite.tree.Tree;
import com.netflix.rewrite.tree.Type;
import com.netflix.rewrite.tree.TypeUtils;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

public class ReferencedTypesVisitor extends AstVisitor<Set<Type.Class>> {
    @Override
    public Set<Type.Class> defaultTo(Tree t) {
        return emptySet();
    }

    @Override
    public Set<Type.Class> visitTypeName(NameTree name) {
        Set<Type.Class> referenced = new HashSet<>(super.visitTypeName(name));
        Type.Class asClass = TypeUtils.asClass(name.getType());
        if (asClass != null) {
            referenced.add(asClass);
        }
        return referenced;
    }
}
