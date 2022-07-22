import dev.fritz2.core.RenderContext
import dev.fritz2.core.render

fun main() {
    render("#target") {
//        helloWorld()
//        v1Prototype()
        v2ComponentsAndStores()
    }
}

// a component
private fun RenderContext.helloWorld() {
    h1("text-5xl") {
        +"OHAI!"
    }
}