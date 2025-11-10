// Minimal root build file. Module build scripts (app/) will apply plugins.
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
