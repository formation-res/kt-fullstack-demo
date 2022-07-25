@file:Suppress("unused")

package v4

import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

val searchModule = module {
    single { QueryTextStore() }
    single { SearchResultStore() }
}

val  RenderContext.koin get() = GlobalContext.get()
val  RootStore<*>.koin get() = GlobalContext.get()