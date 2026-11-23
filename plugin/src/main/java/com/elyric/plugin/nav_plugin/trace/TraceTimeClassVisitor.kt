package com.elyric.plugin.nav_plugin.trace

import com.elyric.plugin.nav_plugin.model.PluginInfo.TRACE_TIME_DESCRIPTOR
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.commons.Method

class TraceTimeClassVisitor(
    api: Int,
    nextClassVisitor: ClassVisitor
) : ClassVisitor(api, nextClassVisitor) {
    private var className: String = ""

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        className = name.orEmpty().replace('/', '.')
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        val methodName = name.orEmpty()
        val methodDescriptor = descriptor.orEmpty()
        val skip = access and (Opcodes.ACC_ABSTRACT or Opcodes.ACC_NATIVE) != 0
        if (skip) {
            return methodVisitor
        }
        return TraceTimeMethodVisitor(
            api = api,
            methodVisitor = methodVisitor,
            access = access,
            methodName = methodName,
            methodDescriptor = methodDescriptor,
            className = className
        )
    }
}

private class TraceTimeMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    access: Int,
    private val methodName: String,
    methodDescriptor: String,
    private val className: String
) : AdviceAdapter(api, methodVisitor, access, methodName, methodDescriptor) {
    private var enabled = false
    private var tag = DEFAULT_TAG
    private var traceName = ""
    private var thresholdMs = 0L
    private var startTimeLocalIndex = -1

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor? {
        val annotationVisitor = super.visitAnnotation(descriptor, visible)
        if (descriptor != TRACE_TIME_DESCRIPTOR) {
            return annotationVisitor
        }
        enabled = true
        return object : AnnotationVisitor(api, annotationVisitor) {
            override fun visit(name: String?, value: Any?) {
                when (name) {
                    KEY_TAG -> tag = value as? String ?: DEFAULT_TAG
                    KEY_NAME -> traceName = value as? String ?: ""
                    KEY_THRESHOLD_MS -> thresholdMs = (value as? Number)?.toLong() ?: 0L
                }
                super.visit(name, value)
            }
        }
    }

    override fun onMethodEnter() {
        if (!enabled) {
            return
        }
        startTimeLocalIndex = newLocal(Type.LONG_TYPE)
        invokeStatic(SYSTEM_CLOCK_TYPE, ELAPSED_REALTIME_METHOD)
        storeLocal(startTimeLocalIndex, Type.LONG_TYPE)
    }

    override fun onMethodExit(opcode: Int) {
        if (!enabled || startTimeLocalIndex < 0) {
            return
        }
        val costLocalIndex = newLocal(Type.LONG_TYPE)
        invokeStatic(SYSTEM_CLOCK_TYPE, ELAPSED_REALTIME_METHOD)
        loadLocal(startTimeLocalIndex, Type.LONG_TYPE)
        math(SUB, Type.LONG_TYPE)
        storeLocal(costLocalIndex, Type.LONG_TYPE)

        if (thresholdMs > 0L) {
            loadLocal(costLocalIndex, Type.LONG_TYPE)
            push(thresholdMs)
            val skipLog = newLabel()
            ifCmp(Type.LONG_TYPE, LT, skipLog)
            emitLog(costLocalIndex, opcode)
            mark(skipLog)
        } else {
            emitLog(costLocalIndex, opcode)
        }
    }

    private fun emitLog(costLocalIndex: Int, opcode: Int) {
        push(tag)
        newInstance(STRING_BUILDER_TYPE)
        dup()
        invokeConstructor(STRING_BUILDER_TYPE, STRING_BUILDER_CONSTRUCTOR)
        push(traceName.ifBlank { "$className#$methodName" })
        invokeVirtual(STRING_BUILDER_TYPE, STRING_APPEND_STRING_METHOD)
        push(" cost=")
        invokeVirtual(STRING_BUILDER_TYPE, STRING_APPEND_STRING_METHOD)
        loadLocal(costLocalIndex, Type.LONG_TYPE)
        invokeVirtual(STRING_BUILDER_TYPE, STRING_APPEND_LONG_METHOD)
        push("ms")
        invokeVirtual(STRING_BUILDER_TYPE, STRING_APPEND_STRING_METHOD)
        if (opcode == ATHROW) {
            push(" exit=throw")
            invokeVirtual(STRING_BUILDER_TYPE, STRING_APPEND_STRING_METHOD)
        }
        invokeVirtual(STRING_BUILDER_TYPE, STRING_TO_STRING_METHOD)
        invokeStatic(LOG_TYPE, LOG_D_METHOD)
        pop()
    }

    private companion object {
        const val DEFAULT_TAG = "BTraceTime"
        const val KEY_TAG = "tag"
        const val KEY_NAME = "name"
        const val KEY_THRESHOLD_MS = "thresholdMs"

        val SYSTEM_CLOCK_TYPE: Type = Type.getType("Landroid/os/SystemClock;")
        val LOG_TYPE: Type = Type.getType("Landroid/util/Log;")
        val STRING_BUILDER_TYPE: Type = Type.getType(StringBuilder::class.java)
        val ELAPSED_REALTIME_METHOD = Method("elapsedRealtime", "()J")
        val LOG_D_METHOD = Method("d", "(Ljava/lang/String;Ljava/lang/String;)I")
        val STRING_BUILDER_CONSTRUCTOR = Method("<init>", "()V")
        val STRING_APPEND_STRING_METHOD = Method(
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
        )
        val STRING_APPEND_LONG_METHOD = Method("append", "(J)Ljava/lang/StringBuilder;")
        val STRING_TO_STRING_METHOD = Method("toString", "()Ljava/lang/String;")
    }
}
