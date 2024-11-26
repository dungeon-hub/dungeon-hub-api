package net.dungeonhub.service

import dev.kordex.core.builders.ExtensibleBotBuilder
import dev.kordex.core.builders.PluginBuilder
import dev.kordex.core.i18n.ResourceBundleTranslations
import dev.kordex.core.i18n.TranslationsProvider
import dev.kordex.core.koin.KordExContext
import dev.kordex.core.plugins.PluginManager
import dev.kordex.core.utils.loadModule
import org.koin.dsl.bind
import org.koin.environmentProperties
import org.koin.fileProperties
import java.io.File
import java.util.*

object TestHelper {
    fun buildKoinContext() {
        val botBuilder = ExtensibleBotBuilder()
        val pluginBuilder = PluginBuilder(botBuilder)

        KordExContext.startKoin {
            environmentProperties()

            if (File("koin.properties").exists()) {
                fileProperties("koin.properties")
            }
        }

        loadModule { single { botBuilder } bind ExtensibleBotBuilder::class }
        loadModule { single { ResourceBundleTranslations { Locale.ENGLISH } } bind TranslationsProvider::class }

        val manager = pluginBuilder.manager(pluginBuilder.pluginPaths, pluginBuilder.enabled)

        loadModule { single { manager } bind PluginManager::class }
    }
}