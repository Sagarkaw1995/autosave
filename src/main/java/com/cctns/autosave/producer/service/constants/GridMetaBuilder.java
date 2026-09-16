package com.cctns.autosave.producer.service.constants;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Typed, module-checked replacement for hand-building gridMeta with
 * LinkedHashMap.put(String, Object).
 *
 * Two guarantees raw .put() calls don't give you:
 *  1. FIELD NAME SAFETY — only AutosaveGridField enum constants are
 *     accepted, never a raw String, so a typo'd field name is a
 *     compile error, not a silently-wrong map entry.
 *  2. MODULE <-> FIELD ENFORCEMENT — set() checks the field against
 *     THIS module's registered field set and throws IMMEDIATELY, at
 *     the exact call site, if it doesn't belong — not three files
 *     away in a downstream reader that gets a null it wasn't expecting.
 *
 * Two entry points, because "which fields are legal" is a DIFFERENT
 * question depending on the operation:
 *   forCreate(module) -> checks against module.getAllowedGridFields()
 *                        -> used by createDraft, builds a FRESH map
 *   forUpdate(module) -> checks against module.getUpdatableFields()
 *                        -> used by persistAutosaveData, MUTATES an
 *                           existing map fetched from Redis (so
 *                           create-only fields like draftId/draftNum
 *                           are never touched during an update, since
 *                           they simply aren't in updatableFields)
 *
 * Deliberately does NOT re-check "is this field required" — that's
 * already AutosaveDomainValidationService's job (Bean Validation
 * groups, checked before this builder is ever reached). This builder's
 * one job is preventing a field from landing under the WRONG module
 * or the WRONG operation — not duplicating presence checks that
 * already happened upstream.
 */
public final class GridMetaBuilder {

    private enum Mode { CREATE, UPDATE }

    private final Module module;
    private final Mode mode;
    private final EnumMap<AutosaveGridField, Object> values = new EnumMap<>(AutosaveGridField.class);

    private GridMetaBuilder(Module module, Mode mode) {
        this.module = module;
        this.mode = mode;
    }

    /** Use when creating a brand-new draft — validates against allowedGridFields. */
    public static GridMetaBuilder forCreate(Module module) {
        return new GridMetaBuilder(module, Mode.CREATE);
    }

    /** Use when updating an existing draft — validates against updatableFields. */
    public static GridMetaBuilder forUpdate(Module module) {
        return new GridMetaBuilder(module, Mode.UPDATE);
    }

    public GridMetaBuilder set(AutosaveGridField field, Object value) {
        if (value == null) {
            return this; // mirrors the original code's "if (...!=null) put(...)" pattern
        }

        Set<AutosaveGridField> permittedFields = permittedFieldsForCurrentMode();

        if (!permittedFields.contains(field)) {
            // Thrown as unchecked on purpose: this means a DEVELOPER
            // mistake (wrong field for this module/operation), not a
            // bad user request — a global handler should map this to
            // a 500, not a 400, since the caller's input wasn't the
            // problem.
            throw new IllegalArgumentException(
                    "Field '" + field.getKey() + "' is not registered for " + mode + " on module '" + module +
                            "'. If this is intentional, add " + field + " to " + module + "'s " +
                            (mode == Mode.CREATE ? "allowedGridFields" : "updatableFields") + " in Module");
        }
        values.put(field, value);
        return this;
    }

    private Set<AutosaveGridField> permittedFieldsForCurrentMode() {
        return mode == Mode.CREATE ? module.getAllowedGridFields() : module.getUpdatableFields();
    }

    /**
     * Builds a FRESH map — intended for CREATE mode (createDraft),
     * where gridMeta doesn't exist yet. Throws if called in UPDATE
     * mode, since an update should never blow away fields (draftId,
     * draftNum, etc.) that only CREATE is allowed to set — use
     * applyTo(existingMap) instead for that case.
     *
     * EnumMap iterates in enum DECLARATION order, not insertion order —
     * so the resulting field order in Redis/JSON is always consistent,
     * regardless of what order .set() calls happened in code. Reorder
     * AutosaveGridField's constants if you want a different output order.
     */
    public LinkedHashMap<String, Object> build() {
        if (mode == Mode.UPDATE) {
            throw new IllegalStateException(
                    "build() is for CREATE mode only. Use applyTo(existingGridMeta) for UPDATE mode, " +
                            "so fields outside updatableFields (e.g. draftId, draftNum) are preserved rather than dropped.");
        }
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<AutosaveGridField, Object> entry : values.entrySet()) {
            result.put(entry.getKey().getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Applies this builder's staged values ONTO an already-existing
     * gridMeta map — intended for UPDATE mode (persistAutosaveData).
     * Anything already in existingGridMeta that this module's
     * updatableFields didn't touch (draftId, draftNum, draftSrno, ...)
     * is left completely untouched. Throws if called in CREATE mode,
     * to avoid accidentally merging into a map that shouldn't exist yet.
     */
    public LinkedHashMap<String, Object> applyTo(LinkedHashMap<String, Object> existingGridMeta) {
        if (mode == Mode.CREATE) {
            throw new IllegalStateException(
                    "applyTo(existingGridMeta) is for UPDATE mode only. Use build() for CREATE mode.");
        }
        for (Map.Entry<AutosaveGridField, Object> entry : values.entrySet()) {
            existingGridMeta.put(entry.getKey().getKey(), entry.getValue());
        }
        return existingGridMeta;
    }
}