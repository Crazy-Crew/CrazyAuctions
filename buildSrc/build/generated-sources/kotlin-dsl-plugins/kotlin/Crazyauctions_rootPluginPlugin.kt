/**
 * Precompiled [crazyauctions.root-plugin.gradle.kts][Crazyauctions_root_plugin_gradle] script plugin.
 *
 * @see Crazyauctions_root_plugin_gradle
 */
public
class Crazyauctions_rootPluginPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Crazyauctions_root_plugin_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
