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
class SettcCmd(bot: Bot) : AdminCommand() {
    init {
        this.name = "settc"
        this.help = "ボットのコマンドチャンネルを設定します"
        this.arguments = "<チャンネル名|NONE|なし>"
        this.aliases = bot.config.getAliases(this.name)

        this.children = arrayOf<SlashCommand>(Set(), None())
    }

    override fun execute(event: SlashCommandEvent) {
    }

    // ここは普通のコマンド
    override fun execute(event: CommandEvent) {
        val log = LoggerFactory.getLogger("SettcCmd")
        if (event.args.isEmpty()) {
            event.reply(event.client.error + "チャンネルまたはNONEを含めてください。")
            return
        }
        val s = event.client.getSettingsFor<Settings>(event.guild)
        if (event.args.lowercase(Locale.getDefault()).matches("(none|なし)".toRegex())) {
            s.setTextChannel(null)
            event.reply(event.client.success + "音楽コマンドは現在どのチャンネルでも使用できます。")
        } else {
            val list = FinderUtil.findTextChannels(event.args, event.guild)
            if (list.isEmpty()) event.reply(event.client.warning + "一致するチャンネルが見つかりませんでした \"" + event.args + "\"")
            else if (list.size > 1) event.reply(event.client.warning + FormatUtil.listOfTChannels(list, event.args))
            else {
                s.setTextChannel(list[0])
                log.info("音楽コマンド用のチャンネルを設定しました。")
                event.reply(event.client.success + "音楽コマンドを<#" + list[0].id + ">のみで使用できるように設定しました。")
            }
        }
    }

    private class Set : AdminCommand() {
        init {
            this.name = "set"
            this.help = "音楽コマンド用のチャンネルを設定"

            val options: MutableList<OptionData> = ArrayList()
            options.add(OptionData(OptionType.CHANNEL, "channel", "テキストチャンネル", true))

            this.options = options
        }

        override fun execute(event: SlashCommandEvent) {
            if (checkAdminPermission(event.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val s = event.client.getSettingsFor<Settings>(event.guild)


            if (event.getOption("channel")!!.channelType != ChannelType.TEXT) {
                event.reply(event.client.error + "テキストチャンネルを設定して下さい。").queue()
                return
            }
            val channelId = event.getOption("channel")!!.asLong
            val tc = event.guild!!.getTextChannelById(channelId)

            s.setTextChannel(tc)
            event.reply(event.client.success + "音楽コマンドを<#" + tc!!.id + ">のみで使用できるように設定しました。").queue()
        }
    }

    private class None : AdminCommand() {
        init {
            this.name = "none"
            this.help = "音楽コマンド用チャンネルの設定を無効にします。"
        }

        override fun execute(event: SlashCommandEvent) {
            if (checkAdminPermission(event.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.setTextChannel(null)
            event.reply(event.client.success + "音楽コマンドは現在どのチャンネルでも使用できます。").queue()
        }

        override fun execute(event: CommandEvent) {
            val s = event.client.getSettingsFor<Settings>(event.guild)
            s.setTextChannel(null)
            event.replySuccess("音楽コマンドは現在どのチャンネルでも使用できます。")
        }
    }
}
