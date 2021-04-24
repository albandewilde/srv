package srv

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.apibuilder.ApiBuilder.*

fun main() {
    val srv = Javalin.create{cfg ->
        cfg.requestLogger{ctx: Context, _ -> println("${ctx.method()} ${ctx.path()}")}
        cfg.enableCorsForAllOrigins()
    }.start(7000)

    srv.routes{
        path("/*") {
            get(::List)
            delete(::Delete)
            put(::Add)
        }
    }
}