import dev.fritz2.core.RenderContext

object TWClasses {
    // just some reusable defaults that I don't want to copy/paste all over
    const val submitButton = "rounded-full bg-sky-500/75 hover:bg-sky-500/100 text-white font-bold px-5 hover:shadow-xl"
    const val defaultSpaceX = "space-x-2"
    const val defaultSpaceY = "space-x-2"
}

fun RenderContext.lineUp(classes: String = TWClasses.defaultSpaceX, block: RenderContext.()->Unit) {
    div("flex flex-row $classes") {
        block.invoke(this)
    }
}

fun RenderContext.stackUp(classes: String = TWClasses.defaultSpaceY, block: RenderContext.()->Unit) {
    div("flex flex-col $classes") {
        block.invoke(this)
    }
}