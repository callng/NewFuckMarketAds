package com.owo233.fuckmarketads.util

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.util.concurrent.ConcurrentHashMap

private val fieldCache = ConcurrentHashMap<Pair<Class<*>, Boolean>, Array<Field>>()

private fun allowAccess(member: Member) {
    if (member !is AccessibleObject) return
    member.runCatching { isAccessible = true }
}

private fun Class<*>.allFields(withSuper: Boolean): Array<Field> {
    return fieldCache.getOrPut(this to withSuper) {
        val fields = mutableListOf<Field>()
        var current: Class<*>? = this

        while (current != null && current != Any::class.java) {
            fields += current.declaredFields
            current = if (withSuper) current.superclass else null
        }

        fields.toTypedArray()
    }
}

fun Any.getFields(withSuper: Boolean = true): Array<Field> {
    val clazz = (this as? Class<*>) ?: this::class.java
    return clazz.allFields(withSuper)
}

fun Any.getField(fieldType: Class<*>, withSuper: Boolean = true): Field? {
    return this.getFields(withSuper).firstOrNull { it.type == fieldType }.also {
        if (it != null) allowAccess(it)
    }
}

fun Any.getField(fieldName: String, withSuper: Boolean = true): Field? {
    return this.getFields(withSuper).firstOrNull { it.name == fieldName }.also {
        if (it != null) allowAccess(it)
    }
}

fun Any.getFieldValue(fieldType: Class<*>, withSuper: Boolean = true): Any? {
    return this.getField(fieldType, withSuper)?.get(this)
}

fun Any.getFieldValue(name: String, withSuper: Boolean = true): Any? {
    return this.getField(name, withSuper)?.get(this)
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.getFieldValueAs(fieldType: Class<*>, withSuper: Boolean = true): T? =
    this.getFieldValue(fieldType, withSuper) as T?

@Suppress("UNCHECKED_CAST")
fun <T> Any.getFieldValueAs(name: String, withSuper: Boolean = true): T? =
    this.getFieldValue(name, withSuper) as T?

fun Any.setFieldValue(name: String, value: Any?): Boolean {
    val field = this.getField(name) ?: return false
    field.set(this, value)
    return true
}
