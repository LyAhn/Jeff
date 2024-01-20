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
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.LoggerFactory
import java.util.*

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
class SetvcCmd(bot: Bot) : AdminCommand() {
    init {
        this.name = "setvc"
        this.help = "再生に使用する音声チャンネルを固定します。"
        this.arguments = "<チャンネル名|NONE|なし>"
        this.aliases = bot.config.getAliases(this.name)

        this.children = arrayOf<SlashCommand>(Set(), None())
    }

    override fun execute(slashCommandEvent: SlashCommandEvent) {
    }

    override fun execute(event: CommandEvent) {
        val log = LoggerFactory.getLogger("SetVcCmd")
        if (event.args.isEmpty()) {
            event.reply(event.client.error + "音声チャンネルまたはNONEを含めてください。")
            return
        }
        val s = event.client.getSettingsFor<Settings>(event.guild)
        if (event.args.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            s.setVoiceChannel(null)
            event.reply(event.client.success + "音楽はどの音声チャンネルでも再生できます。")
        } else {
            val list = FinderUtil.findVoiceChannels(event.args, event.guild)
            if (list.isEmpty()) event.reply(event.client.warning + "一致する音声チャンネルが見つかりませんでした \"" + event.args + "\"")
            else if (list.size > 1) event.reply(event.client.warning + FormatUtil.listOfVChannels(list, event.args))
            else {
                s.setVoiceChannel(list[0])
                log.info("音楽チャンネルを設定しました。")
                event.reply(event.client.success + "音楽は**" + list[0].asMention + "**でのみ再生できるようになりました。")
            }
        }
    }

    private class Set : AdminCommand() {
        init {
            this.name = "set"
            this.help = "再生に使用する音声チャンネルを設定"

            val options: MutableList<OptionData> = ArrayList()
            options.add(OptionData(OptionType.CHANNEL, "channel", "音声チャンネル", true))

            this.options = options
        }

        override fun execute(event: SlashCommandEvent) {
            if (checkAdminPermission(event.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val s = event.client.getSettingsFor<Settings>(event.guild)
            val channel = event.getOption("channel")!!.asLong

            if (event.getOption("channel")!!.channelType != ChannelType.VOICE) {
                event.reply(event.client.error + "音声チャンネルを設定して下さい").queue()
            }

            val vc = event.guild!!.getVoiceChannelById(channel)
            s.setVoiceChannel(vc)
            event.reply(event.client.success + "音楽は**" + vc!!.asMention + "**でのみ再生できるようになりました。")
                .queue()
        }
    }

    private class None : AdminCommand() {
        init {
            this.name = "none"
            this.help = "再生に使用する音声チャンネルの設定をリセットします。"
        }

        override fun execute(event: SlashCommandEvent) {
            if (checkAdminPermission(event.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.setVoiceChannel(null)
            event.reply(event.client.success + "音楽はどの音声チャンネルでも再生できます。").queue()
        }

        override fun execute(event: CommandEvent) {
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.setVoiceChannel(null)
            event.replySuccess("音楽はどの音声チャンネルでも再生できます。")
        }
    }
}
