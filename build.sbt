scalaVersion := "2.12.1"

javaOptions += "-XstartOnFirstThread"
fork in run := true

val lwjglVersion = "3.1.1"
val jomlVersion = "1.9.2"

libraryDependencies += "org.lwjgl" % "lwjgl" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-assimp" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-assimp" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-bgfx" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-bgfx" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-egl" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-jawt" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-lmdb" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-lmdb" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-nanovg" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-nanovg" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-nfd" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-nfd" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-nuklear" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-nuklear" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-openal" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-openal" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-opencl" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-opengles" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-opengles" % lwjglVersion classifier "natives-macos"

// libraryDependencies += "org.lwjgl" % "lwjgl-ovr" % lwjglVersion
// libraryDependencies += "org.lwjgl" % "lwjgl-ovr" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-par" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-par" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-sse" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-sse" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-stb" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-stb" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-tinyfd" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-tinyfd" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-vulkan" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-xxhash" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-xxhash" % lwjglVersion classifier "natives-macos"

libraryDependencies += "org.joml" % "joml" % jomlVersion
