scalaVersion := "2.12.1"

javaOptions += "-XstartOnFirstThread"
fork in run := true

libraryDependencies += "org.lwjgl" % "lwjgl" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-assimp" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-assimp" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-bgfx" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-bgfx" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-egl" % "3.1.1"

libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-jawt" % "3.1.1"

libraryDependencies += "org.lwjgl" % "lwjgl-jemalloc" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-jemalloc" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-lmdb" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-lmdb" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-nanovg" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-nanovg" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-nfd" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-nfd" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-nuklear" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-nuklear" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-openal" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-openal" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-opencl" % "3.1.1"

libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-opengles" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-opengles" % "3.1.1" classifier "natives-macos"

// libraryDependencies += "org.lwjgl" % "lwjgl-ovr" % "3.1.1"
// libraryDependencies += "org.lwjgl" % "lwjgl-ovr" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-par" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-par" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-sse" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-sse" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-stb" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-stb" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-tinyfd" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-tinyfd" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.lwjgl" % "lwjgl-vulkan" % "3.1.1"

libraryDependencies += "org.lwjgl" % "lwjgl-xxhash" % "3.1.1"
libraryDependencies += "org.lwjgl" % "lwjgl-xxhash" % "3.1.1" classifier "natives-macos"

libraryDependencies += "org.joml" % "joml" % "1.9.2"
