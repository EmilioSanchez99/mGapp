package com.example.mgapp.domain

import com.example.mgapp.R
import com.example.mgapp.data.model.FieldSchema
import com.example.mgapp.data.model.FieldType
import com.example.mgapp.data.model.HotspotSchema

data class ValidationError(
    val fieldId: String,
    val messageRes: Int
)

fun validateField(value: String?, schema: FieldSchema): List<ValidationError> {
    val rule = schema.validation
    val errors = mutableListOf<ValidationError>()

    // --- Requerido ---
    if (rule.required && (value == null || value.isBlank())) {
        errors += ValidationError(schema.id, R.string.err_required)
        return errors // si es requerido y está vacío, no seguimos
    }

    // --- Según tipo ---
    when (schema.type) {
        FieldType.TEXT -> {
            val len = value?.length ?: 0
            if (rule.min != null && len < rule.min!!) errors += ValidationError(schema.id, R.string.err_min_length)
            if (rule.max != null && len > rule.max!!) errors += ValidationError(schema.id, R.string.err_max_length)
            if (!rule.regex.isNullOrBlank() && !(value?.matches(rule.regex!!.toRegex()) ?: false))
                errors += ValidationError(schema.id, R.string.err_invalid_format)
        }
        FieldType.NUMBER -> {
            if (!value.isNullOrBlank()) {
                val num = value.toDoubleOrNull()
                if (num == null) errors += ValidationError(schema.id, R.string.err_invalid_number)
                else {
                    if (rule.min != null && num < rule.min!!) errors += ValidationError(schema.id, R.string.err_too_low)
                    if (rule.max != null && num > rule.max!!) errors += ValidationError(schema.id, R.string.err_too_high)
                }
            }
        }
        FieldType.DATE -> { /* puedes validar formato dd/MM/yyyy si lo necesitas */ }
        FieldType.DROPDOWN -> {
            if (!rule.options.isNullOrEmpty() && value != null && value !in rule.options)
                errors += ValidationError(schema.id, R.string.err_invalid_option)
        }
        FieldType.CHECKBOX -> { /* nada que validar aquí normalmente */ }
    }
    return errors
}

fun validateForm(values: Map<String, String?>, schema: HotspotSchema): Map<String, List<ValidationError>> {
    return schema.fields.associate { f -> f.id to validateField(values[f.id], f) }
}
