import dev.fritz2.core.RenderContext
import dev.fritz2.core.render
import v4.v4UseLibForModels
import v5.v5UseKtSearch

fun main() {
    render("#target") {

//        helloWorld()
//        v1.v1Prototype()
//        v2ComponentsAndStores()
//        v3TalkToOurServer()
//        v4UseLibForModels()
        v5UseKtSearch()
    }
}

// a component
private fun RenderContext.helloWorld() {
    h1("text-5xl") {
        +"OHAI!"
    }
}