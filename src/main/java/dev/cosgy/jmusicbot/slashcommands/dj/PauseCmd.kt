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
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.PlayStatus
import com.jagrosh.jmusicbot.audio.AudioHandler
import dev.cosgy.jmusicbot.slashcommands.DJCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
class PauseCmd(bot: Bot) : DJCommand(bot) {
    override var log: Logger = LoggerFactory.getLogger("Pause")

    init {
        this.name = "pause"
        this.help = "現在の曲を一時停止します"
        this.aliases = bot.config.getAliases(this.name)
        this.bePlaying = true
    }

    override fun doCommand(event: CommandEvent?) {
        val handler = event!!.guild.audioManager.sendingHandler as AudioHandler?
        if (handler!!.player.isPaused) {
            event.replyWarning("曲はすでに一時停止しています。 `" + event.client.prefix + " play` を使用して一時停止を解除する事ができます。")
            return
        }
        handler.player.isPaused = true
        log.info(event.guild.name + "で" + handler.player.playingTrack.info.title + "を一時停止しました。")
        event.replySuccess("**" + handler.player.playingTrack.info.title + "**を一時停止にしました。 `" + event.client.prefix + " play` を使用すると一時停止を解除できます。")

        Bot.updatePlayStatus(event.guild, event.guild.selfMember, PlayStatus.PAUSED)
    }

    override fun doCommand(event: SlashCommandEvent?) {
        if (!checkDJPermission(event!!.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val handler = event.guild!!.audioManager.sendingHandler as AudioHandler?
        if (handler!!.player.isPaused) {
            event.reply(event.client.warning + "曲はすでに一時停止しています。 `" + event.client.prefix + " play` を使用して一時停止を解除する事ができます。")
                .queue()
            return
        }
        handler.player.isPaused = true
        log.info(event.guild!!.name + "で" + handler.player.playingTrack.info.title + "を一時停止しました。")
        event.reply(event.client.success + "**" + handler.player.playingTrack.info.title + "**を一時停止にしました。 `" + event.client.prefix + " play` を使用すると一時停止を解除できます。")
            .queue()

        Bot.updatePlayStatus(event.guild, event.guild!!.selfMember, PlayStatus.PAUSED)
    }
}
