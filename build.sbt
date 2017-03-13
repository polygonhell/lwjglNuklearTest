scalaVersion := "2.12.1"


val lwjglVersion = "3.1.1"
val jomlVersion = "1.9.2"

val nativeClass = if (sys.props("os.name") == "Linux") {
		"natives-linux" 
	} else {
		javaOptions += "-XstartOnFirstThread"
		fork in run := true
		"natives-macos"
	}



libraryDependencies += "org.lwjgl" % "lwjgl" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-assimp" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-assimp" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-bgfx" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-bgfx" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-egl" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-jawt" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-lmdb" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-lmdb" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-nanovg" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-nanovg" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-nfd" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-nfd" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-nuklear" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-nuklear" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-openal" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-openal" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-opencl" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-opengles" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-opengles" % lwjglVersion classifier nativeClass

// libraryDependencies += "org.lwjgl" % "lwjgl-ovr" % lwjglVersion
// libraryDependencies += "org.lwjgl" % "lwjgl-ovr" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-par" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-par" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-sse" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-sse" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-stb" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-stb" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-tinyfd" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-tinyfd" % lwjglVersion classifier nativeClass

libraryDependencies += "org.lwjgl" % "lwjgl-vulkan" % lwjglVersion

libraryDependencies += "org.lwjgl" % "lwjgl-xxhash" % lwjglVersion
libraryDependencies += "org.lwjgl" % "lwjgl-xxhash" % lwjglVersion classifier nativeClass

libraryDependencies += "org.joml" % "joml" % jomlVersion
