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

class SetvcStatusCmd(bot: Bot) : AdminCommand() {
    init {
        this.name = "setvcstatus"
        this.help = "VCステータスに再生中を表示するかを設定します。"
        this.arguments = "<true|false>"
        this.aliases = bot.config.getAliases(this.name)

        this.options = listOf(
            OptionData(OptionType.BOOLEAN, "status", "有効:true 無効:false", true)
        )
    }

    override fun execute(event: SlashCommandEvent) {
        if (checkAdminPermission(event.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }

        val status = event.getOption("status")!!.asBoolean
        val s = event.client.getSettingsFor<Settings>(event.guild)
        s.vcStatus = status

        event.reply(event.client.success + "VCステータスに再生中を表示するかを`" + status + "`に設定しました。").queue()
    }

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.reply(event.client.error + "trueかfalseを含めてください。")
            return
        }

        val s = event.client.getSettingsFor<Settings>(event.guild)

        if (event.args.lowercase(Locale.getDefault()).matches("(false|無効)".toRegex())) {
            s.vcStatus = false
            event.reply(event.client.success + "VCステータスに再生中を表示しないように設定しました。")
        } else if (event.args.lowercase(Locale.getDefault()).matches("(true|有効)".toRegex())) {
            s.vcStatus = true
            event.reply(event.client.success + "VCステータスに再生中を表示するように設定しました。")
        } else {
            event.reply(event.client.error + "trueかfalseを含めてください。")
        }
    }
}
