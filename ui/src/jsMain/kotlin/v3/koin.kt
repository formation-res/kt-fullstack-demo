package v3

import org.koin.dsl.module

val v3Module = module {
    single { QueryTextStore() }
    single { SearchResultStore() }
}