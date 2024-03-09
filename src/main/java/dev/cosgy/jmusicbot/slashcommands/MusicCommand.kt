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
package dev.cosgy.jmusicbot.slashcommands

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.audio.AudioHandler
import com.jagrosh.jmusicbot.settings.Settings
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion
import net.dv8tion.jda.api.exceptions.PermissionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
abstract class MusicCommand(@JvmField protected val bot: Bot?) : SlashCommand() {
    @JvmField
    protected var bePlaying: Boolean = false
    @JvmField
    protected var beListening: Boolean = false
    open var log: Logger = LoggerFactory.getLogger("MusicCommand")

    init {
        this.guildOnly = true
        this.category = Category("Music")
    }

    override fun execute(event: SlashCommandEvent) {
        val settings = event.client.getSettingsFor<Settings>(event.guild)
        val channel = settings.getTextChannel(event.guild)

        bot?.playerManager?.setUpHandler(event.guild)
        if (bePlaying && !(event.guild!!.audioManager.sendingHandler as AudioHandler?)!!.isMusicPlaying(event.jda)) {
            event.reply(event.client.error + "コマンドを使用するには、再生中である必要があります。").queue()
            return
        }
        if (beListening) {
            var current = event.guild!!.selfMember.voiceState!!.channel

            if (current == null) current = settings.getVoiceChannel(event.guild) as AudioChannelUnion
            val userState = event.member!!.voiceState

            if (!userState!!.inAudioChannel() || userState.isDeafened || (current != null && userState.channel != current)) {
                event.reply(
                    event.client.error + String.format(
                        "このコマンドを使用するには、%sに参加している必要があります！",
                        (if (current == null) "音声チャンネル" else "**" + current.asMention + "**")
                    )
                ).queue()
                return
            }
            if (!event.guild!!.selfMember.voiceState!!.inAudioChannel()) {
                try {
                    event.guild!!.audioManager.openAudioConnection(userState.channel)
                } catch (ex: PermissionException) {
                    event.reply(
                        event.client.error + String.format(
                            "**%s**に接続できません!", userState.channel!!
                                .asMention
                        )
                    ).queue()
                    return
                }
                if (userState.channel!!.type == ChannelType.STAGE) {
                    event.textChannel.sendMessage(
                        event.client.warning + String.format(
                            "ステージチャンネルに参加しました。ステージチャンネルで%sを使用するには手動でスピーカーに招待する必要があります。",
                            event.guild!!
                                .selfMember.nickname
                        )
                    ).queue()
                }
            }
        }

        doCommand(event)
    }

    override fun execute(event: CommandEvent) {
        val settings = event.client.getSettingsFor<Settings>(event.guild)
        val channel = settings.getTextChannel(event.guild)

        if (channel != null && event.textChannel != channel) {
            try {
                event.message.delete().queue()
            } catch (ignore: PermissionException) {
            }
            event.replyInDm(event.client.error + String.format("コマンドは%sでのみ実行できます", channel.asMention))
            return
        }
        bot?.playerManager?.setUpHandler(event.guild) // no point constantly checking for this later

        if (bePlaying && !(event.guild.audioManager.sendingHandler as AudioHandler?)!!.isMusicPlaying(event.jda)) {
            event.reply(event.client.error + "コマンドを使用するには、再生中である必要があります。")
            return
        }
        if (beListening) {
            var current = event.guild.selfMember.voiceState!!.channel

            if (current == null) current = settings.getVoiceChannel(event.guild) as AudioChannelUnion
            val userState = event.member.voiceState
            if (!userState!!.inAudioChannel() || userState.isDeafened || (current != null && userState.channel != current)) {
                event.replyError(
                    String.format(
                        "このコマンドを使用するには、%sに参加している必要があります！",
                        (if (current == null) "音声チャンネル" else "**" + current.name + "**")
                    )
                )
                return
            }
            if (!event.guild.selfMember.voiceState!!.inAudioChannel()) {
                try {
                    event.guild.audioManager.openAudioConnection(userState.channel)
                } catch (ex: PermissionException) {
                    event.reply(
                        event.client.error + String.format(
                            "**%s**に接続できません!", userState.channel!!
                                .name
                        )
                    )
                    return
                }
                if (userState.channel!!.type == ChannelType.STAGE) {
                    event.textChannel.sendMessage(
                        event.client.warning + String.format(
                            "ステージチャンネルに参加しました。ステージチャンネルで%sを使用するには手動でスピーカーに招待する必要があります。",
                            event.guild.selfMember.nickname
                        )
                    ).queue()
                }
            }
        }

        doCommand(event)
    }

    abstract fun doCommand(event: CommandEvent?)

    abstract fun doCommand(event: SlashCommandEvent?)
}
