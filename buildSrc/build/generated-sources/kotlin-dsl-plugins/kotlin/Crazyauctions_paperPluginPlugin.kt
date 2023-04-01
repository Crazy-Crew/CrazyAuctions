/**
 * Precompiled [crazyauctions.paper-plugin.gradle.kts][Crazyauctions_paper_plugin_gradle] script plugin.
 *
 * @see Crazyauctions_paper_plugin_gradle
 */
public
class Crazyauctions_paperPluginPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Crazyauctions_paper_plugin_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
