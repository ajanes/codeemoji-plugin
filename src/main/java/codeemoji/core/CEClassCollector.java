package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEClassCollector extends CECollector<PsiClass, PsiIdentifier> {

    public CEClassCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId, new CESymbol());
    }

    public CEClassCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitClass(@NotNull PsiClass clazz) {
                    if (isHintable(clazz)) {
                        addInlayOnEditor(clazz.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitClass(clazz);
                }
            });
        }
        return false;
    }
}