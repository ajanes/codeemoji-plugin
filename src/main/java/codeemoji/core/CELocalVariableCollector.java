package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class CELocalVariableCollector extends CECollector<PsiLocalVariable, PsiElement> {

    public CELocalVariableCollector(Editor editor, String keyId) {
        super(editor, keyId);
    }

    public CELocalVariableCollector(Editor editor, String keyId, CEInlay ceInlay) {
        super(editor, keyId, ceInlay);
    }

    public CELocalVariableCollector(Editor editor, String keyId, int codePoint) {
        super(editor, keyId, codePoint);
    }

    public CELocalVariableCollector(Editor editor, String keyId, CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                    if (isHintable(variable)) {
                        addInlayOnEditor(variable.getNameIdentifier(), inlayHintsSink);
                        if (CEUtil.isNotPreviewEditor(editor)) {
                            GlobalSearchScope scope = GlobalSearchScope.fileScope(variable.getContainingFile());
                            PsiReference[] refs = ReferencesSearch.search(variable, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                            for (PsiReference ref : refs) {
                                addInlayOnEditor(ref.getElement(), inlayHintsSink);
                            }
                        } else {
                            processReferencesInPreviewEditor(variable);
                        }
                    }
                    super.visitLocalVariable(variable);
                }

                //TODO: reimplement using GlobalSearchScope
                private void processReferencesInPreviewEditor(PsiLocalVariable variable) {
                    psiElement.accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                            PsiElement qualifier = CEUtil.identifyFirstQualifier(expression);
                            if (qualifier != null && Objects.equals(qualifier.getText(), variable.getName())) {
                                addInlayOnEditor(qualifier, inlayHintsSink);
                            }
                        }
                    });
                }
            });
        }
        return false;
    }
}