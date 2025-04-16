/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package com.intellij.lang.properties.refactoring;

import com.intellij.lang.properties.IProperty;
import com.intellij.lang.properties.psi.PropertiesFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.refactoring.RefactoringSettings;
import consulo.language.editor.refactoring.safeDelete.NonCodeUsageSearchInfo;
import consulo.language.editor.refactoring.safeDelete.SafeDeleteProcessor;
import consulo.language.editor.refactoring.safeDelete.SafeDeleteProcessorDelegate;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import consulo.usage.UsageInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yole
 */
@ExtensionImpl
public class PropertiesSafeDeleteProcessor implements SafeDeleteProcessorDelegate {
    public boolean handlesElement(final PsiElement element) {
        return element instanceof PropertiesFile;
    }

    public NonCodeUsageSearchInfo findUsages(
        final PsiElement element,
        final PsiElement[] allElementsToDelete,
        final List<UsageInfo> result
    ) {
        PropertiesFile file = (PropertiesFile)element;
        List<PsiElement> elements = new ArrayList<PsiElement>();
        elements.add(file.getContainingFile());
        for (IProperty property : file.getProperties()) {
            elements.add(property.getPsiElement());
        }
        for (PsiElement psiElement : elements) {
            SafeDeleteProcessor.findGenericElementUsages(psiElement, result, allElementsToDelete);
        }
        return new NonCodeUsageSearchInfo(SafeDeleteProcessor.getDefaultInsideDeletedCondition(allElementsToDelete), elements);
    }

    public Collection<PsiElement> getElementsToSearch(final PsiElement element, final Collection<PsiElement> allElementsToDelete) {
        return Collections.singletonList(element);
    }

    public Collection<PsiElement> getAdditionalElementsToDelete(
        final PsiElement element,
        final Collection<PsiElement> allElementsToDelete,
        final boolean askUser
    ) {
        return null;
    }

    public Collection<String> findConflicts(final PsiElement element, final PsiElement[] allElementsToDelete) {
        return null;
    }

    public UsageInfo[] preprocessUsages(final Project project, final UsageInfo[] usages) {
        return usages;
    }

    public void prepareForDeletion(final PsiElement element) throws IncorrectOperationException {
    }

    @Override
    public boolean isToSearchInComments(PsiElement element) {
        return RefactoringSettings.getInstance().SAFE_DELETE_SEARCH_IN_COMMENTS;
    }

    @Override
    public boolean isToSearchForTextOccurrences(PsiElement element) {
        return RefactoringSettings.getInstance().SAFE_DELETE_SEARCH_IN_NON_JAVA;
    }

    @Override
    public void setToSearchInComments(PsiElement element, boolean enabled) {
        RefactoringSettings.getInstance().SAFE_DELETE_SEARCH_IN_COMMENTS = enabled;
    }

    @Override
    public void setToSearchForTextOccurrences(PsiElement element, boolean enabled) {
        RefactoringSettings.getInstance().SAFE_DELETE_SEARCH_IN_NON_JAVA = enabled;
    }
}
