package v3

import org.koin.dsl.module

val searchModule = module {
    single { QueryTextStore() }
    single { SearchResultStore() }
}