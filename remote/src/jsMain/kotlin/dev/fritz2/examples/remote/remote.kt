package dev.fritz2.examples.remote

import dev.fritz2.binding.RootStore
import dev.fritz2.binding.const
import dev.fritz2.binding.handledBy
import dev.fritz2.dom.html.render
import dev.fritz2.dom.mount
import dev.fritz2.dom.values
import dev.fritz2.remote.getBody
import dev.fritz2.remote.remote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
fun main() {

    val userStore = object : RootStore<String>("") {

        val users = remote("https://reqres.in/api/users")

        val loadAllUsers = handle {
            users.get().getBody()
        }

        val loadUserById = handle { _, s: String ->
            users.acceptJson().get(s).getBody()
        }

        val saveUserWithName = handle { _, s: String ->
            users.body("""
                    {
                        "name": "$s",
                        "job": "programmer"
                    }
                """.trimIndent())
                .contentType("application/json; charset=utf-8")
                .acceptJson().post().getBody()
        }
    }

    render {
        div {
            div("form-group") {
                label("load-user") {
                    text("Load user by id")
                }
                input("form-control", id = "load-user") {
                    placeholder = const("Enter user id")
                    changes.values() handledBy userStore.loadUserById
                }
            }

            hr("my-4") { }

            div("form-group") {
                label("save-user") {
                    text("Save user")
                }
                input("form-control", id = "save-user") {
                    placeholder = const("Enter new user name")
                    changes.values() handledBy userStore.saveUserWithName
                }
            }

            hr("my-4") { }

            div("form-group") {
                button("btn btn-primary") {
                    text("Load all users")
                    clicks handledBy userStore.loadAllUsers
                }
            }
            div("card card-body") {
                h6("card-title") {
                    text("User store data")
                }
                pre("text-wrap") {
                    code {
                        userStore.data.bind()
                    }
                }
            }
        }
    }.mount("target")
}
