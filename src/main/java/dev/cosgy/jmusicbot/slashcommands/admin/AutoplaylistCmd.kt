/*
 *  Copyright 2024 Cosgy Dev (info@cosgy.dev).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dev.cosgy.jmusicbot.slashcommands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.settings.Settings
import dev.cosgy.jmusicbot.slashcommands.AdminCommand
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.util.*

/**
 * @author kosugi_kun
 */
open class AutoplaylistCmd(private val bot: Bot) : AdminCommand() {
    init {
        this.guildOnly = true
        this.name = "autoplaylist"
        this.arguments = "<name|NONE|なし>"
        this.aliases = bot.config.getAliases(this.name)
        this.help = "サーバーの自動再生リストを設定"
        this.ownerCommand = false

        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.STRING, "name", "プレイリストの名前", true))

        this.options = options
    }

    override fun execute(event: SlashCommandEvent) {
        if (checkAdminPermission(event.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val pName = event.getOption("name")!!.asString
        if (pName.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            val settings = event.client.getSettingsFor<Settings>(event.guild)
            settings.defaultPlaylist = null
            event.reply(event.client.success + "**" + event.guild!!.name + "** での自動再生リストを、なしに設定しました。")
                .queue()
            return
        }
        if (bot.playlistLoader.getPlaylist(event.guild!!.id, pName) == null) {
            event.reply(event.client.error + "`" + pName + "`を見つけることができませんでした!").queue()
        } else {
            val settings = event.client.getSettingsFor<Settings>(event.guild)
            settings.defaultPlaylist = pName
            event.reply(
                """
    ${event.client.success}**${event.guild!!.name}** での自動再生リストを、`$pName`に設定しました。
    再生待ちに曲がないときは、自動再生リストの曲が再生されます。
    """.trimIndent()
            ).queue()
        }
    }

    public override fun execute(event: CommandEvent) {
        if (!event.isOwner || !event.member.isOwner) return
        val guildId = event.guild.id

        if (event.args.isEmpty()) {
            event.reply(event.client.error + " 再生リスト名、またはNONEを含めてください。")
            return
        }
        if (event.args.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            val settings = event.client.getSettingsFor<Settings>(event.guild)
            settings.defaultPlaylist = null
            event.reply(event.client.success + "**" + event.guild.name + "** での自動再生リストを、なしに設定しました。")
            return
        }
        val pName = event.args.replace("\\s+".toRegex(), "_")
        if (bot.playlistLoader.getPlaylist(guildId, pName) == null) {
            event.reply(event.client.error + "`" + pName + "`を見つけることができませんでした!")
        } else {
            val settings = event.client.getSettingsFor<Settings>(event.guild)
            settings.defaultPlaylist = pName
            event.reply(
                """
    ${event.client.success}**${event.guild.name}** での自動再生リストを、`$pName`に設定しました。
    再生待ちに曲がないときは、自動再生リストの曲が再生されます。
    """.trimIndent()
            )
        }
    }
}