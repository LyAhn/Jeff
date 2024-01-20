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
 * @author John Grosh (john.a.grosh@gmail.com)
 */
class PrefixCmd(bot: Bot) : AdminCommand() {
    init {
        this.name = "prefix"
        this.help = "サーバー固有のプレフィックスを設定します"
        this.arguments = "<プレフィックス|NONE>"
        this.aliases = bot.config.getAliases(this.name)

        //this.children = new SlashCommand[]{new None()};
        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.STRING, "prefix", "設定するプレフィックス", true))

        this.options = options
    }

    override fun execute(event: SlashCommandEvent) {
        if (checkAdminPermission(event.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val s = event.client.getSettingsFor<Settings>(event.guild)
        val prefix = event.getOption("prefix")!!.asString
        if (prefix.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            s.prefix = null
            event.reply(event.client.success + "プレフィックスがクリアされました。").queue()
        } else {
            s.prefix = prefix
            event.reply(event.client.success + "*" + event.guild!!.name + "* でのプレフィックスを、 `" + prefix + "`に設定しました。")
                .queue()
        }
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.replyError("プレフィックスまたはNONEを含めてください。")
            return
        }

        val s = event.client.getSettingsFor<Settings>(event.guild)
        if (event.args.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            s.prefix = null
            event.replySuccess("プレフィックスがクリアされました。")
        } else {
            s.prefix = event.args
            event.replySuccess("*" + event.guild.name + "* でのプレフィックスを、 `" + event.args + "`に設定しました。")
        }
    }
}
