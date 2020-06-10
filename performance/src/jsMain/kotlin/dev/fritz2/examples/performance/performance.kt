package dev.fritz2.examples.performance

import dev.fritz2.binding.RootStore
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.html.render
import dev.fritz2.dom.mount
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.browser.document

@ExperimentalCoroutinesApi
@FlowPreview
fun main() {

    val store = object : RootStore<Int>(0) {
        val dummyHandler = handle { model ->
            model
        }
    }

    val counter = store.data.map { number ->
        render {
            p(id = "value") {
                text(number.toString())
                clicks handledBy store.dummyHandler
            }
        }
    }.conflate()

    document.write(
        """
            <body id="target">
                Loading...
            </body>
        """.trimIndent()
    )

    render {
        div {
            counter.bind()
            button {
                text("start updates")
                domNode.addEventListener("click", {
                    val values = flow {
                        for (i in 1..500000) {
                            emit(i)
                        }
                    }

                    values handledBy store.update
                })
            }
        }
    }.mount("target")

}