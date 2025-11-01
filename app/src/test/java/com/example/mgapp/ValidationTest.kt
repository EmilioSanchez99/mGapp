package com.example.mgapp

import com.example.mgapp.data.model.FieldSchema
import com.example.mgapp.data.model.FieldType
import com.example.mgapp.data.model.ValidationRule
import com.example.mgapp.data.model.HotspotSchema
import com.example.mgapp.domain.validateForm
import org.junit.Assert.*
import org.junit.Test

class ValidationTest {

    private val schema = HotspotSchema(
        hotspotId = "test",
        fields = listOf(
            FieldSchema("name", 0, FieldType.TEXT, ValidationRule(required = true, min = 3.0)),
            FieldSchema("height", 0, FieldType.NUMBER, ValidationRule(min = 1.0, max = 100.0))
        )
    )

    @Test
    fun requiredFieldFailsWhenEmpty() {
        val result = validateForm(mapOf("name" to ""), schema)
        assertTrue(result["name"]!!.isNotEmpty())
    }

    @Test
    fun passesWithValidData() {
        val result = validateForm(mapOf("name" to "Tower", "height" to "12"), schema)
        assertTrue(result.all { it.value.isEmpty() })
    }

    @Test
    fun failsWhenBelowMin() {
        val result = validateForm(mapOf("height" to "0"), schema)
        assertTrue(result["height"]!!.isNotEmpty())
    }
}
