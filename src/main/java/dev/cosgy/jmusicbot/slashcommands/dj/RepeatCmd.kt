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
package dev.cosgy.jmusicbot.slashcommands.dj

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.settings.Settings
import dev.cosgy.jmusicbot.settings.RepeatMode
import dev.cosgy.jmusicbot.slashcommands.DJCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com> | edit: ryuuta0217
 */
class RepeatCmd(bot: Bot) : DJCommand(bot) {
    override var log: Logger = LoggerFactory.getLogger("Repeat")

    init {
        this.name = "repeat"
        this.help = "再生待ち楽曲の再生が終了したら曲を再追加します"
        this.arguments = "[all|on|single|one|off]"
        this.aliases = bot.config.getAliases(this.name)
        this.guildOnly = true

        this.children = arrayOf<SlashCommand>(SingleCmd(bot), AllCmd(bot), OffCmd(bot))
    }

    // override musiccommand's execute because we don't actually care where this is used
    override fun execute(event: CommandEvent) {
        val value: RepeatMode
        val settings = event.client.getSettingsFor<Settings>(event.guild)

        val args = event.args

        if (args.isEmpty()) {
            log.info("変更前の再生モード:" + settings.repeatMode)
            value =
                (if (settings.repeatMode == RepeatMode.OFF) RepeatMode.ALL else (if (settings.repeatMode == RepeatMode.ALL) RepeatMode.SINGLE else (if (settings.repeatMode == RepeatMode.SINGLE) RepeatMode.OFF else settings.repeatMode)))
        } else if (args.equals("true", ignoreCase = true) || args.equals("all", ignoreCase = true) || args.equals(
                "on",
                ignoreCase = true
            )
        ) {
            value = RepeatMode.ALL
        } else if (args.equals("false", ignoreCase = true) || args.equals("off", ignoreCase = true)) {
            value = RepeatMode.OFF
        } else if (args.equals("one", ignoreCase = true) || args.equals("single", ignoreCase = true)) {
            value = RepeatMode.SINGLE
        } else {
            event.replyError(
                """
    有効なオプションは
    ```
    全曲リピート: true, all, on
    1曲リピート: one, single
    リピートオフ: false, off```
    です
    (または、オプション無しで切り替えが可能です)
    """.trimIndent()
            )
            return
        }

        settings.repeatMode = value
        log.info(event.guild.name + "でリピートコマンドを実行し、設定を" + value + "に設定しました。")
        event.replySuccess("リピートを `" + (if (value == RepeatMode.ALL) "有効(全曲リピート)" else (if (value == RepeatMode.SINGLE) "有効(1曲リピート)" else "無効")) + "` にしました。")
    }

    override fun doCommand(event: CommandEvent?) { /* Intentionally Empty */
    }

    override fun doCommand(event: SlashCommandEvent?) {
    }

    private inner class SingleCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "single"
            this.help = "１曲リピートモードに変更します。"
            this.guildOnly = true
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!checkDJPermission(event!!.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val settings = event.client.getSettingsFor<Settings>(
                event.guild
            )
            settings.repeatMode = RepeatMode.SINGLE
            event.reply("リピートを `有効(1曲リピート)` にしました。").queue()
        }

        override fun doCommand(event: CommandEvent?) {
        }
    }

    private inner class AllCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "all"
            this.help = "全曲リピートモードに変更します。"
            this.guildOnly = true
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!checkDJPermission(event!!.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val settings = event.client.getSettingsFor<Settings>(
                event.guild
            )
            settings.repeatMode = RepeatMode.ALL
            event.reply("リピートを `有効(全曲リピート)` にしました。").queue()
        }

        override fun doCommand(event: CommandEvent?) {
        }
    }

    private inner class OffCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "off"
            this.help = "リピートを無効に変更します。"
            this.guildOnly = true
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!checkDJPermission(event!!.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val settings = event.client.getSettingsFor<Settings>(
                event.guild
            )
            settings.repeatMode = RepeatMode.OFF
            event.reply("リピートを `無効` にしました。").queue()
        }

        override fun doCommand(event: CommandEvent?) {
        }
    }
}
