package com.elyric.plugin.nav_plugin

import org.objectweb.asm.ClassVisitor

class NavClassVisitor(
    api: Int,
    nextClassVisitor: ClassVisitor
) : ClassVisitor(api, nextClassVisitor)
