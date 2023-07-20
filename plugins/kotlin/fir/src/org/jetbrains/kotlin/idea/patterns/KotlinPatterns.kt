// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.patterns

import com.intellij.patterns.*
import com.intellij.psi.PsiElement
import com.intellij.util.PairProcessor
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.analysis.api.KtAllowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.KtAnalysisSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.annotations.annotations
import org.jetbrains.kotlin.analysis.api.lifetime.allowAnalysisOnEdt
import org.jetbrains.kotlin.analysis.api.renderer.types.impl.KtTypeRendererForSource
import org.jetbrains.kotlin.analysis.api.symbols.KtFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KtValueParameterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.receiverType
import org.jetbrains.kotlin.analysis.api.types.KtType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.types.Variance

// Methods in this class are used through reflection
@Suppress("unused")
object KotlinPatterns : StandardPatterns() {
    @JvmStatic
    fun kotlinParameter() = KtParameterPattern()

    @JvmStatic
    fun kotlinFunction() = KotlinFunctionPattern()

    @JvmStatic
    fun receiver() = KotlinReceiverPattern()
}

context(KtAnalysisSession)
private fun KtType.renderFullyQualifiedName() = render(KtTypeRendererForSource.WITH_QUALIFIED_NAMES, Variance.INVARIANT)

// Methods in this class are used through reflection during pattern construction
@Suppress("unused")
open class KotlinFunctionPattern : PsiElementPattern<KtFunction, KotlinFunctionPattern>(KtFunction::class.java) {
    @OptIn(KtAllowAnalysisOnEdt::class)
    fun withParameters(vararg parameterTypes: String): KotlinFunctionPattern {
        return withPatternCondition("kotlinFunctionPattern-withParameters") { function, _ ->
            if (function.valueParameters.size != parameterTypes.size) return@withPatternCondition false

            allowAnalysisOnEdt {
                analyze(function) {
                    val symbol = function.getSymbol() as? KtFunctionSymbol ?: return@withPatternCondition false
                    val valueParameterSymbols = symbol.valueParameters

                    if (valueParameterSymbols.size != parameterTypes.size) return@withPatternCondition false
                    for (i in 0..valueParameterSymbols.size - 1) {
                        val expectedTypeString = parameterTypes[i]
                        val valueParamSymbol = valueParameterSymbols[i]

                        if (valueParamSymbol.returnType.renderFullyQualifiedName() != expectedTypeString) {
                            return@withPatternCondition false
                        }
                    }
                }
            }
            true
        }
    }

    @OptIn(KtAllowAnalysisOnEdt::class)
    fun withReceiver(receiverFqName: String): KotlinFunctionPattern {
        return withPatternCondition("kotlinFunctionPattern-withReceiver") { function, _ ->
            if (function.receiverTypeReference == null) return@withPatternCondition false
            if (receiverFqName == "?") return@withPatternCondition true

            allowAnalysisOnEdt {
                analyze(function) {
                    val symbol = function.getSymbol() as? KtFunctionSymbol ?: return@withPatternCondition false
                    val receiverType = symbol.receiverType ?: return@withPatternCondition false

                    receiverType.renderFullyQualifiedName() == receiverFqName
                }
            }
        }
    }

    class DefinedInClassCondition(val fqName: String) : PatternCondition<KtFunction>("kotlinFunctionPattern-definedInClass") {
        override fun accepts(element: KtFunction, context: ProcessingContext?): Boolean {
            if (element.parent is KtFile) return false
            return element.containingClassOrObject?.fqName?.asString() == fqName
        }
    }

    fun definedInClass(fqName: String): KotlinFunctionPattern = with(DefinedInClassCondition(fqName))

    fun definedInPackage(packageFqName: String): KotlinFunctionPattern {
        return withPatternCondition("kotlinFunctionPattern-definedInPackage") { function, _ ->
            if (function.parent !is KtFile) return@withPatternCondition false

            function.containingKtFile.packageFqName.asString() == packageFqName
        }
    }
}

// Methods in this class are used through reflection during pattern construction
@Suppress("unused")
class KtParameterPattern : PsiElementPattern<KtParameter, KtParameterPattern>(KtParameter::class.java) {
    fun ofFunction(index: Int, pattern: ElementPattern<Any>): KtParameterPattern {
        return with(object : PatternConditionPlus<KtParameter, KtFunction>("KtParameterPattern-ofMethod", pattern) {
            override fun processValues(
                ktParameter: KtParameter,
                context: ProcessingContext,
                processor: PairProcessor<in KtFunction, in ProcessingContext>
            ): Boolean {
                val function = ktParameter.ownerFunction as? KtFunction ?: return true
                return processor.process(function, context)
            }

            override fun accepts(ktParameter: KtParameter, context: ProcessingContext): Boolean {
                val ktFunction = ktParameter.ownerFunction ?: return false

                val parameters = ktFunction.valueParameters
                if (index < 0 || index >= parameters.size || ktParameter != parameters[index]) return false

                return super.accepts(ktParameter, context)
            }
        })
    }

    fun withAnnotation(fqName: String): KtParameterPattern {
        return withPatternCondition("KtParameterPattern-withAnnotation") { ktParameter, _ ->
            if (ktParameter.annotationEntries.isEmpty()) return@withPatternCondition false

            analyze(ktParameter) {
                val paramSymbol = ktParameter.getParameterSymbol()
                paramSymbol is KtValueParameterSymbol && paramSymbol.annotations.any { annotation ->
                    annotation.classId?.asFqNameString() == fqName
                }
            }
        }
    }
}

@Suppress("unused")
class KotlinReceiverPattern : PsiElementPattern<KtTypeReference, KotlinReceiverPattern>(KtTypeReference::class.java) {
    fun ofFunction(pattern: ElementPattern<Any>): KotlinReceiverPattern {
        return with(object : PatternConditionPlus<KtTypeReference, KtFunction>("KtReceiverPattern-ofMethod", pattern) {
            override fun processValues(
                typeReference: KtTypeReference,
                context: ProcessingContext?,
                processor: PairProcessor<in KtFunction, in ProcessingContext>
            ): Boolean = processor.process(typeReference.parent as? KtFunction, context)

            override fun accepts(typeReference: KtTypeReference, context: ProcessingContext?): Boolean {
                val ktFunction = typeReference.parent as? KtFunction ?: return false
                if (ktFunction.receiverTypeReference != typeReference) return false

                return super.accepts(typeReference, context)
            }
        })
    }
}

private fun <T : PsiElement, Self : PsiElementPattern<T, Self>> PsiElementPattern<T, Self>.withPatternCondition(
    debugName: String, condition: (T, ProcessingContext?) -> Boolean
): Self = with(object : PatternCondition<T>(debugName) {
    override fun accepts(element: T, context: ProcessingContext?): Boolean {
        return condition(element, context)
    }
})