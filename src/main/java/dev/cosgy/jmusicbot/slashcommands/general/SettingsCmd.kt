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
package dev.cosgy.jmusicbot.slashcommands.general

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.settings.Settings
import com.jagrosh.jmusicbot.utils.FormatUtil
import dev.cosgy.jmusicbot.settings.RepeatMode
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import java.util.*

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
class SettingsCmd(bot: Bot) : SlashCommand() {
    init {
        this.name = "settings"
        this.help = "Botの設定を表示します"
        this.aliases = bot.config.getAliases(this.name)
        this.guildOnly = true
    }

    override fun execute(event: SlashCommandEvent) {
        val s = event.client.getSettingsFor<Settings>(event.guild)
        val builder = MessageCreateBuilder()
            .addContent("$EMOJI **")
            .addContent(FormatUtil.filter(event.jda.selfUser.name))
            .addContent("** の設定:")
        val tChan = s.getTextChannel(event.guild)
        val vChan = s.getVoiceChannel(event.guild)
        val role = s.getRole(event.guild)
        val ebuilder = EmbedBuilder()
            .setDescription(
                """
    コマンド実行用チャンネル: ${if (tChan == null) "なし" else "**#" + tChan.name + "**"}
    専用ボイスチャンネル: ${if (vChan == null) "なし" else "**" + vChan.asMention + "**"}
    DJ 権限: ${if (role == null) "未設定" else "**" + role.name + "**"}
    リピート: **${if (s.repeatMode == RepeatMode.ALL) "有効(全曲リピート)" else (if (s.repeatMode == RepeatMode.SINGLE) "有効(1曲リピート)" else "無効")}**
    音量:**${s.volume}**
    デフォルトプレイリスト: ${if (s.defaultPlaylist == null) "なし" else "**" + s.defaultPlaylist + "**"}
    """.trimIndent()
            )
            .setFooter(
                String.format(
                    "%s 個のサーバーに参加 | %s 個のボイスチャンネルに接続",
                    event.jda.guilds.size,
                    event.jda.guilds.stream()
                        .filter { g: Guild -> Objects.requireNonNull(g.selfMember.voiceState)!!.inAudioChannel() }
                        .count()),
                null)
        event.reply(builder.addEmbeds(ebuilder.build()).build()).queue()
    }

    override fun execute(event: CommandEvent) {
        val s = event.client.getSettingsFor<Settings>(event.guild)
        val builder = MessageCreateBuilder()
            .addContent("$EMOJI **")
            .addContent(FormatUtil.filter(event.selfUser.name))
            .addContent("** の設定:")
        val tChan = s.getTextChannel(event.guild)
        val vChan = s.getVoiceChannel(event.guild)
        val role = s.getRole(event.guild)
        val ebuilder = EmbedBuilder()
            .setColor(event.selfMember.color)
            .setDescription(
                """
    コマンド実行用チャンネル: ${if (tChan == null) "なし" else "**#" + tChan.name + "**"}
    専用ボイスチャンネル: ${if (vChan == null) "なし" else "**" + vChan.name + "**"}
    DJ 権限: ${if (role == null) "未設定" else "**" + role.name + "**"}
    リピート: **${if (s.repeatMode == RepeatMode.ALL) "有効(全曲リピート)" else (if (s.repeatMode == RepeatMode.SINGLE) "有効(1曲リピート)" else "無効")}**
    デフォルトプレイリスト: ${if (s.defaultPlaylist == null) "なし" else "**" + s.defaultPlaylist + "**"}
    """.trimIndent()
            )
            .setFooter(
                String.format(
                    "%s 個のサーバーに参加 | %s 個のボイスチャンネルに接続",
                    event.jda.guilds.size,
                    event.jda.guilds.stream()
                        .filter { g: Guild -> Objects.requireNonNull(g.selfMember.voiceState)!!.inAudioChannel() }
                        .count()),
                null)
        event.channel.sendMessage(builder.addEmbeds(ebuilder.build()).build()).queue()
    }

    companion object {
        private const val EMOJI = "\uD83C\uDFA7" // 🎧
    }
}
