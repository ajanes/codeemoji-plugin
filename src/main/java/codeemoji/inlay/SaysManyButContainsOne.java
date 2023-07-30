package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codeemoji.core.CESymbol.ONE;

public class SaysManyButContainsOne extends CEProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                  private String names;
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEFieldCollector(editor, getKey().getId()) {
            @Override
            public void processInlay(PsiField field, InlayHintsSink sink) {
                PsiTypeElement typeElement = field.getTypeElement();
                if (typeElement != null &&
                        CEUtil.isPluralForm(field.getName()) &&
                        CEUtil.isNotGenericType(typeElement) &&
                        !CEUtil.isArrayType(typeElement) &&
                        !CEUtil.isIterableType(typeElement) &&
                        !CEUtil.containsOnlySpecialCharacters(typeElement.getText())) {
                    addInlay(field.getNameIdentifier(), sink, ONE);
                }
            }
        };
    }
}