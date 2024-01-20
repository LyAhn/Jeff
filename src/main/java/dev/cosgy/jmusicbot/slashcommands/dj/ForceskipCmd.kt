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
import com.jagrosh.jmusicbot.audio.AudioHandler
import dev.cosgy.jmusicbot.slashcommands.DJCommand

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
class ForceskipCmd(bot: Bot) : DJCommand(bot) {
    init {
        this.name = "forceskip"
        this.help = "現在の曲をスキップします"
        this.aliases = bot.config.getAliases(this.name)
        this.bePlaying = true
    }

    override fun doCommand(event: CommandEvent?) {
        val handler = event!!.guild.audioManager.sendingHandler as AudioHandler?
        val rm = handler!!.requestMetadata
        event.reply(
            event.client.success + "**" + handler.player.playingTrack.info.title
                    + "** " + (if (rm.owner == 0L) "(自動再生)" else "(**" + rm.user.username + "**がリクエスト)")
        )
        handler.player.stopTrack()
    }

    override fun doCommand(event: SlashCommandEvent?) {
        if (!checkDJPermission(event!!.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val handler = event.guild!!.audioManager.sendingHandler as AudioHandler?
        val rm = handler!!.requestMetadata
        event.reply(
            event.client.success + "**" + handler.player.playingTrack.info.title
                    + "** " + (if (rm.owner == 0L) "(自動再生)" else "(**" + rm.user.username + "**がリクエスト)")
        ).queue()
        handler.player.stopTrack()
    }
}
