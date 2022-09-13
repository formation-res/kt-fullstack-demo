package v4

import org.koin.dsl.module

val v4Module = module {
    single { QueryTextStore() }
    single { SearchResultStore() }
}