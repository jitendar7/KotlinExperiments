package com.experiment

// Sealed class is similar to enum except, its subclasses can have multiple constructors
// Sealed class is like an abstract class -> no instance
// We can extend Sealed class ( both object/class) , inner class cannot extend Sealed class
sealed class Post {
    class Status(var text: String) : Post()
    class Image(var url: String, var caption: String) : Post()
    class Video(var url: String, var timeDuration: Int, var encoding: String) : Post()
}

fun eval(e: Post) {
    when (e) {
        is Post.Image -> println("Image URL => ${e.url} , Caption => ${e.caption}")
        is Post.Status -> println("Status Text => ${e.text}")
        is Post.Video -> println("Video URL=>${e.url} TimeDuration=>${e.timeDuration} Encoding=>${e.encoding}")
    }
}

fun main() {

    val video = Post.Video("url1", 1200, "UTF-8")
    val image = Post.Image("url2", "Image caption")
    val status = Post.Status("Post status")

    eval(video)
    eval(image)
    eval(status)
}