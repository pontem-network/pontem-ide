// "Optimize imports" "true"

import A as B
<caret>import A as T

class A

fun foo() {
    B()
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.inspections.KotlinOptimizeImportsQuickFix