package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEFeatureRule;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEElementRule.CLASS;

public interface ICEProjectReferenceList<H extends PsiReferenceList, A extends PsiElement> extends ICEProjectConfigFile {

    default void processReferenceListFR(@NotNull CEFeatureRule featureRule, @Nullable H evaluationElement,
                                        @NotNull A hintElement, @NotNull InlayHintsSink sink,
                                        @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        addInlayReferenceListFR(hintElement, needsHintReferenceListFR(featureRule, evaluationElement), sink,
                symbol, keyTooltip);
    }

    default @NotNull List<String> needsHintReferenceListFR(@NotNull CEFeatureRule featureRule, @Nullable PsiReferenceList refList) {
        Map<CEFeatureRule, List<String>> rules = getRules(CLASS);
        List<String> featureValues = rules.get(featureRule);
        List<String> hintValues = new ArrayList<>();
        if (featureValues != null && (refList != null)) {
            PsiClassType[] refs = refList.getReferencedTypes();
            for (PsiClassType psiType : refs) {
                for (String value : featureValues) {
                    String qualifiedName = CEUtils.resolveQualifiedName(psiType);
                    if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                        hintValues.add(value);
                    }
                }
            }

        }
        return hintValues;
    }

    void addInlayReferenceListFR(@NotNull A addHintElement, @NotNull List<String> hintValues,
                                 @NotNull InlayHintsSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip);

}
