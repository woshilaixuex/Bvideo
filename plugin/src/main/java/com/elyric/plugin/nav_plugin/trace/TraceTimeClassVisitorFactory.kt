package com.elyric.plugin.nav_plugin.trace

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

abstract class TraceTimeClassVisitorFactory :
    AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return TraceTimeClassVisitor(Opcodes.ASM9, nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return classData.className.startsWith("com.elyric")
    }
}
