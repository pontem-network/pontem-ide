package org.jetbrains.kotlin.idea.highlighting

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.idea.base.highlighting.AbstractKotlinHighlightExitPointsHandlerFactory
import org.jetbrains.kotlin.idea.codeinsight.utils.isInlinedArgument
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtReturnExpression

class KotlinHighlightExitPointsHandlerFactory: AbstractKotlinHighlightExitPointsHandlerFactory() {

    override fun getRelevantReturnDeclaration(returnExpression: KtReturnExpression): KtDeclarationWithBody? {
        val psi = analyze(returnExpression) {
            returnExpression.getReturnTargetSymbol()?.psi
        }
        return psi as? KtDeclarationWithBody
    }

    override fun isInlinedArgument(declaration: KtDeclarationWithBody): Boolean {
        return declaration is KtFunction && analyze(declaration) {
            isInlinedArgument(declaration, false)
        }
    }

}