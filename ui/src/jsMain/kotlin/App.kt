import dev.fritz2.core.RenderContext
import dev.fritz2.core.render
import v6.v6AddTranslations


fun main() {
    render("#target") {

//        helloWorld()
//        v1Prototype()
//        v2ComponentsAndStores()
//        v3TalkToOurServer()
//        v4UseLibForModels()
//        v5UseKtSearch()
        v6AddTranslations()
    }
}

// a component
private fun RenderContext.helloWorld() {
    h1("text-5xl") {
        +"OHAI!"
    }
}