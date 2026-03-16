package com.owo233.fuckmarketads.util

import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

private val fieldCache = ConcurrentHashMap<Pair<Class<*>, Boolean>, Array<Field>>()
private val methodCache = ConcurrentHashMap<Pair<Class<*>, Boolean>, Array<Method>>()

private fun allowAccess(member: Member) {
    if (member !is AccessibleObject) return
    member.runCatching { isAccessible = true }
}

private fun parametersMatch(paramTypes: Array<Class<*>>, args: Array<out Any?>): Boolean {
    if (paramTypes.size != args.size) return false
    for (i in paramTypes.indices) {
        val arg = args[i]
        val param = paramTypes[i]
        if (arg == null) {
            if (param.isPrimitive) return false
        } else {
            val argClass = arg.javaClass
            if (!isAssignable(param, argClass)) return false
        }
    }
    return true
}

private fun wrap(clazz: Class<*>): Class<*> = when (clazz) {
    Boolean::class.javaPrimitiveType -> Boolean::class.javaObjectType
    Byte::class.javaPrimitiveType -> Byte::class.javaObjectType
    Char::class.javaPrimitiveType -> Char::class.javaObjectType
    Short::class.javaPrimitiveType -> Short::class.javaObjectType
    Int::class.javaPrimitiveType -> Int::class.javaObjectType
    Long::class.javaPrimitiveType -> Long::class.javaObjectType
    Float::class.javaPrimitiveType -> Float::class.javaObjectType
    Double::class.javaPrimitiveType -> Double::class.javaObjectType
    else -> clazz
}

private fun isAssignable(param: Class<*>, argClass: Class<*>): Boolean {
    return when {
        param == argClass -> true
        wrap(param).isAssignableFrom(wrap(argClass)) -> true
        isWideningPrimitive(param, argClass) -> true
        else -> false
    }
}

private fun isWideningPrimitive(param: Class<*>, argClass: Class<*>): Boolean {
    return when (argClass) {
        java.lang.Byte.TYPE -> param in arrayOf(
            java.lang.Short.TYPE, Integer.TYPE, java.lang.Long.TYPE,
            java.lang.Float.TYPE, java.lang.Double.TYPE
        )
        java.lang.Short.TYPE, Character.TYPE -> param in arrayOf(
            Integer.TYPE, java.lang.Long.TYPE, java.lang.Float.TYPE, java.lang.Double.TYPE
        )
        Integer.TYPE -> param in arrayOf(java.lang.Long.TYPE, java.lang.Float.TYPE, java.lang.Double.TYPE)
        java.lang.Long.TYPE -> param in arrayOf(java.lang.Float.TYPE, java.lang.Double.TYPE)
        java.lang.Float.TYPE -> param == java.lang.Double.TYPE
        else -> false
    }
}

// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

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

// = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

private fun Class<*>.allMethods(withSuper: Boolean): Array<Method> {
    return methodCache.getOrPut(this to withSuper) {
        val methods = mutableListOf<Method>()
        var current: Class<*>? = this
        while (current != null && current != Any::class.java) {
            methods += current.declaredMethods
            current = if (withSuper) current.superclass else null
        }
        methods.toTypedArray()
    }
}

fun Any.getMethods(withSuper: Boolean = true): Array<Method> {
    val clazz = (this as? Class<*>) ?: this::class.java
    return clazz.allMethods(withSuper)
}

fun <T> Any.invoke(
    name: String,
    returnType: Class<T>,
    vararg args: Any?,
    withSuper: Boolean = true
): T? {
    val clazz = (this as? Class<*>) ?: this::class.java
    clazz.allMethods(withSuper).forEach {
        if (it.name == name
            && it.returnType == returnType
            && parametersMatch(it.parameterTypes, args)
        ) {
            allowAccess(it)
            @Suppress("UNCHECKED_CAST")
            return it.invoke(this, *args) as T?
        }
    }
    return null
}

fun Any.invoke(
    name: String,
    vararg args: Any?,
    withSuper: Boolean = true
): Any? {
    val clazz = (this as? Class<*>) ?: this::class.java
    clazz.allMethods(withSuper).forEach {
        if (it.name == name && parametersMatch(it.parameterTypes, args)) {
            allowAccess(it)
            return try {
                it.invoke(this, *args)
            } catch (e: InvocationTargetException) {
                throw e.cause ?: e
            }
        }
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.invokeAs(
    name: String,
    vararg args: Any?,
    withSuper: Boolean = true
): T? = this.invoke(name, *args, withSuper = withSuper) as T?

fun Any.invokeMethod(
    withSuper: Boolean = false,
    vararg args: Any?,
    predicate: Method.() -> Boolean
): Any? {
    val clazz = (this as? Class<*>) ?: this::class.java
    val receiver = if (this is Class<*>) null else this

    clazz.allMethods(withSuper).forEach { method ->
        if (!method.predicate()) return@forEach
        if (!parametersMatch(method.parameterTypes, args)) return@forEach

        allowAccess(method)
        return try {
            method.invoke(receiver, *args)
        } catch (e: InvocationTargetException) {
            throw (e.cause ?: e)
        }
    }

    throw NoSuchMethodException(
        buildString {
            append("No matching method found in ")
            append(clazz.name)
            append(", args=")
            append(args.joinToString(prefix = "[", postfix = "]") {
                it?.javaClass?.name ?: "null"
            })
        }
    )
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.invokeMethodAs(
    withSuper: Boolean = false,
    vararg args: Any?,
    predicate: Method.() -> Boolean
): T? {
    return invokeMethod(withSuper, *args, predicate = predicate) as T?
}
