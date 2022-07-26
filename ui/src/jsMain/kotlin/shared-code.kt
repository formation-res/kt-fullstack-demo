import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import org.koin.core.context.GlobalContext

val koin get() = GlobalContext.get()

object TailWindClasses {
    // just some reusable defaults that I don't want to copy/paste all over
    const val submitButton = "rounded-full bg-sky-500/75 hover:bg-sky-500/100 text-white font-bold px-5 hover:shadow-xl"
    const val defaultSpaceX = "space-x-2"
    const val defaultSpaceY = "space-x-2"
}

fun RenderContext.lineUp(classes: String = TailWindClasses.defaultSpaceX, block: RenderContext.() -> Unit) {
    div("flex flex-row $classes") {
        block.invoke(this)
    }
}

fun RenderContext.stackUp(classes: String = TailWindClasses.defaultSpaceY, block: RenderContext.() -> Unit) {
    div("flex flex-col $classes") {
        block.invoke(this)
    }
}

fun RenderContext.navigation() {
    router.data.render { currentPage ->
        p {
            +"Full Stack Kotlin Demo: "
            (listOf("hi") + (1..6).map { it.toString() }).forEach { navigationItem ->
                val style = if (currentPage == navigationItem) {
                    "link underline text-red-400 decoration-red-400"
                } else {
                    "link"
                }
                a(style) {
                    +navigationItem
                    href("/#$navigationItem")
                }
                +" "
            }
        }
    }
}