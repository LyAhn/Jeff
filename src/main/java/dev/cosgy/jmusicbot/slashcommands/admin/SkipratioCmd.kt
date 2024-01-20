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

class SkipratioCmd(bot: Bot) : AdminCommand() {
    init {
        this.name = "setskip"
        this.help = "サーバー固有のスキップ率を設定"
        this.arguments = "<0 - 100>"
        this.aliases = bot.config.getAliases(this.name)

        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.INTEGER, "percent", "スキップ率", true))

        this.options = options
    }

    override fun execute(event: SlashCommandEvent) {
        try {
            val `val` = event.getOption("percent")!!.asString.toInt()
            if (`val` < 0 || `val` > 100) {
                event.reply(event.client.error + "値は、0から100の間でなければなりません。").queue()
                return
            }
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.skipRatio = `val` / 100.0

            event.reply(event.client.success + "*" + event.guild!!.name + "*のリスナーのスキップ率を" + `val` + "%に設定しました。")
                .queue()
        } catch (ex: NumberFormatException) {
            event.reply(event.client.error + "0～100の整数を入れてください（デフォルトは55）。この数値は、曲をスキップするために投票しなければならないリスニングユーザーの割合です。")
                .queue()
        }
    }

    override fun execute(event: CommandEvent) {
        try {
            val `val` =
                (if (event.args.endsWith("%")) event.args.substring(0, event.args.length - 1) else event.args).toInt()
            if (`val` < 0 || `val` > 100) {
                event.replyError("値は、0から100の間でなければなりません。")
                return
            }
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.skipRatio = `val` / 100.0

            event.replySuccess("*" + event.guild.name + "*のリスナーのスキップ率を" + `val` + "%に設定しました。")
        } catch (ex: NumberFormatException) {
            event.replyError("0～100の整数を入れてください（デフォルトは55）。この数値は、曲をスキップするために投票しなければならないリスニングユーザーの割合です。")
        }
    }
}
