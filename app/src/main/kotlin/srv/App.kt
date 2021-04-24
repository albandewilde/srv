package srv

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.apibuilder.ApiBuilder.*

import handlers.*

fun main() {
    val srv = Javalin.create{cfg ->
        cfg.requestLogger{ctx: Context, _ -> println("[${ctx.ip()}:${ctx.port()}] ${ctx.method()}:${ctx.path()}")}
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