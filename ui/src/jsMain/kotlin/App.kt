import dev.fritz2.core.RenderContext
import dev.fritz2.core.render
import dev.fritz2.routing.routerOf
import v1.v1Prototype
import v2.v2ComponentsAndStores
import v3.v3TalkToOurServer
import v4.v4UseLibForModels
import v5.v5UseKtSearch
import v6.v6AddTranslations

val router = routerOf("hi")

// a component
private fun RenderContext.helloWorld() {
    h1("text-5xl") {
        +"OHAI!"
    }
}

fun main() {
    render("#target") {
        div("flex flex-col h-screen justify-between") {
            div("mb-auto") {
                router.data.render { page ->
                    when(page) {
                        "hi" -> helloWorld()
                        "1" -> v1Prototype()
                        "2" -> v2ComponentsAndStores()
                        "3" -> v3TalkToOurServer()
                        "4" -> v4UseLibForModels()
                        "5" -> v5UseKtSearch()
                        "6" -> v6AddTranslations()
                    }
                }
            }
            div("h-10") {
                navigation()
            }
        }
    }
}

