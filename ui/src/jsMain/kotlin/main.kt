import dev.fritz2.core.RenderContext
import dev.fritz2.core.render
import v3.v3TalkToOurServer

fun main() {
    render("#target") {

//        helloWorld()
//        v1.v1Prototype()
//        v2ComponentsAndStores()
        v3TalkToOurServer()
    }
}

// a component
private fun RenderContext.helloWorld() {
    h1("text-5xl") {
        +"OHAI!"
    }
}