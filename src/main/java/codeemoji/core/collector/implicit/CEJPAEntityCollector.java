package codeemoji.core.collector.implicit;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public non-sealed class CEJPAEntityCollector extends CEJPACollector {

    public final List<String> baseNames;

    public CEJPAEntityCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable Integer codePoint) {
        super(editor, keyId, codePoint);
        this.baseNames = CEJPAPersistenceUtils.buildBaseNames("Entity");
    }

    @Override
    public void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiClass clazz) {
            var implicits = new ArrayList<CEIJPAImplicit>();
            implicits.add(new CEJPAImplicitEntity());
            implicits.add(new CEJPAImplicitTable());
            processImplicits(clazz, implicits, sink);
        }
        if (member instanceof PsiField field) {
            var implicits = new ArrayList<CEIJPAImplicit>();
            implicits.add(new CEJPAImplicitColumn());
            implicits.add(new CEJPAImplicitBasic());
            processImplicits(field, implicits, sink);
        }
    }
}
