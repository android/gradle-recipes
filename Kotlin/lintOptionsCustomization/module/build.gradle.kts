plugins {
        kotlin("jvm")
        id("com.android.lint")
}

lintLifecycle {
    finalizeDsl { lint -> lint.enable.plusAssign("StopShip") }
}