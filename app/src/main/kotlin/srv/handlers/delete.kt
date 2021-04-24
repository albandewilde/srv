package handlers

import java.io.File

import io.javalin.http.Context

// Delete a file
fun Delete(ctx: Context) {
    val f = File(BASEDIR + rmTrailingSlash(ctx.path()))

    // Check if file exist before deletion
    if (!f.exists()) {
        ctx.status(404)
        ctx.result("File doesn't exist")
        return
    }

    f.walk().forEach {
        // If the path is the same as the file directory, we delete his content, not de directory him self
        if (it.toString() != BASEDIR) it.deleteRecursively() else null
    }
    ctx.result("Deleted")
}