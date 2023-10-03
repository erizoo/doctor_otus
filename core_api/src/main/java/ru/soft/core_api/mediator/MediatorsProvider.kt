package ru.soft.core_api.mediator

import javax.inject.*

interface MediatorsProvider {

    fun mediatorsMap(): Map<Class<*>, @JvmSuppressWildcards Provider<Any>>

}