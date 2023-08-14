package codeemoji.inlay.showingmodifiers;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@ToString
@EqualsAndHashCode
@State(name = "ShowingModifiersSettings", storages = @Storage("showing-modifiers-settings.xml"))
public class ShowingModifiersSettings implements PersistentStateComponent<ShowingModifiersSettings> {

    @MapAnnotation
    private final HashMap<ShowingModifiers.ScopeModifier, Boolean> basicModifiersMap = new HashMap<>();

    public ShowingModifiersSettings() {
        basicModifiersMap.put(ShowingModifiers.ScopeModifier.VOLATILE_FIELD, true);
        basicModifiersMap.put(ShowingModifiers.ScopeModifier.TRANSIENT_FIELD, true);
        basicModifiersMap.put(ShowingModifiers.ScopeModifier.SYNCHRONIZED_METHOD, true);
        basicModifiersMap.put(ShowingModifiers.ScopeModifier.NATIVE_METHOD, true);
    }

    @Override
    public @NotNull ShowingModifiersSettings getState() {
        return this;
    }

    public void loadState(@NotNull ShowingModifiersSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public synchronized boolean query(@NotNull ShowingModifiers.ScopeModifier scopeModifier) {
        basicModifiersMap.putIfAbsent(scopeModifier, false);
        return basicModifiersMap.get(scopeModifier);
    }

    public synchronized void update(@NotNull ShowingModifiers.ScopeModifier scopeModifier, boolean value) {
        basicModifiersMap.put(scopeModifier, value);
    }

}