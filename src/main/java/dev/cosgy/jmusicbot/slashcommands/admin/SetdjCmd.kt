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
import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.settings.Settings
import com.jagrosh.jmusicbot.utils.FormatUtil
import dev.cosgy.jmusicbot.slashcommands.AdminCommand
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
class SetdjCmd(bot: Bot) : AdminCommand() {
    init {
        this.name = "setdj"
        this.help = "ボットコマンドを使用できる役割DJを設定します。"
        this.arguments = "<役割名|NONE|なし>"
        this.aliases = bot.config.getAliases(this.name)

        this.children = arrayOf<SlashCommand>(SetRole(), None())
    }

    override fun execute(event: SlashCommandEvent) {
        if (checkAdminPermission(event.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val s = event.client.getSettingsFor<Settings>(event.guild)

        if (event.getOption("role") != null) {
            s.setDJRole(event.getOption("role")!!.asRole)
            event.reply(event.client.success + "DJコマンドを役割が、**" + event.getOption("role")!!.asRole.name + "**のユーザーが使用できるように設定しました。")
                .queue()
            return
        }
        if (event.getOption("none")!!.asString.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            s.setDJRole(null)
            event.reply(event.client.success + "DJの役割はリセットされました。管理者だけがDJコマンドを使用できます。")
                .queue()
        } else {
            event.reply("コマンドが間違っています。").queue()
        }
    }

    override fun execute(event: CommandEvent) {
        val log = LoggerFactory.getLogger("SetDjCmd")
        if (event.args.isEmpty()) {
            event.reply(event.client.error + "役割の名前、またはNONEなどを付けてください。")
            return
        }
        val s = event.client.getSettingsFor<Settings>(event.guild)
        if (event.args.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            s.setDJRole(null)
            event.reply(event.client.success + "DJの役割はリセットされました。管理者だけがDJコマンドを使用できます。")
        } else {
            val list = FinderUtil.findRoles(event.args, event.guild)
            if (list.isEmpty()) event.reply(event.client.warning + "役割が見つかりませんでした \"" + event.args + "\"")
            else if (list.size > 1) event.reply(event.client.warning + FormatUtil.listOfRoles(list, event.args))
            else {
                s.setDJRole(list[0])
                log.info("DJコマンドを使える役割が追加されました。(" + list[0].name + ")")
                event.reply(event.client.success + "DJコマンドを役割が、**" + list[0].name + "**のユーザーが使用できるように設定しました。")
            }
        }
    }

    private class SetRole : AdminCommand() {
        init {
            this.name = "set"
            this.help = "DJ権限を付与する役割を設定する。"

            val options: MutableList<OptionData> = ArrayList()
            options.add(OptionData(OptionType.ROLE, "role", "権限を付与する役割", true))
            this.options = options
        }

        override fun execute(event: SlashCommandEvent) {
            val s = event.client.getSettingsFor<Settings>(event.guild)
            val role = event.getOption("role")!!.asRole

            s.setDJRole(role)
            event.reply(event.client.success + "DJコマンドを役割が、**" + role.name + "**のユーザーが使用できるように設定しました。")
                .queue()
        }
    }

    private class None : AdminCommand() {
        init {
            this.name = "none"
            this.help = "DJの役割をリセット"
        }

        override fun execute(event: SlashCommandEvent) {
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.setDJRole(null)
            event.reply(event.client.success + "DJの役割はリセットされました。管理者だけがDJコマンドを使用できます。")
                .queue()
        }

        override fun execute(event: CommandEvent) {
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.setDJRole(null)
            event.replySuccess("DJの役割はリセットされました。管理者だけがDJコマンドを使用できます。")
        }
    }
}
