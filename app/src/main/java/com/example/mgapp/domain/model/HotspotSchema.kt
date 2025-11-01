package com.example.mgapp.data.model

import androidx.annotation.StringRes

enum class FieldType {
    TEXT, NUMBER, DATE, DROPDOWN, CHECKBOX
}

// Reglas de validación por campo
data class ValidationRule(
    val required: Boolean = false,
    val min: Double? = null,        // min length o valor mínimo
    val max: Double? = null,        // max length o valor máximo
    val regex: String? = null,      // validación por patrón (ej: email)
    val options: List<String>? = null // opciones válidas (dropdown)
)

data class FieldSchema(
    val id: String,
    @StringRes val labelRes: Int,
    val type: FieldType,
    val validation: ValidationRule
)

data class HotspotSchema(
    val hotspotId: String,
    val fields: List<FieldSchema>
)
