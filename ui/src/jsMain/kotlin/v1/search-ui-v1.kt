package v1

import dev.fritz2.core.RenderContext
import dev.fritz2.core.placeholder
import dev.fritz2.core.type

// Our first prototype

fun RenderContext.v1Prototype() {
    // back to brutalist design ...
    h1 { +"Recipe Search Engine" }
    div {

        input {
            type("text")
            placeholder("cheese")
        }
        button {
            +"Search"
        }

        ul {
            li {
                +"Result 1"
            }
            li {
                +"Result 2"
            }
            li {
                +"Result 3"
            }
            li {
                +"Result 4"
            }
        }
    }
}