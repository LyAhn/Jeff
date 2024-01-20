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
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
class SkipToCmd(bot: Bot) : DJCommand(bot) {
    override var log: Logger = LoggerFactory.getLogger("Skip")

    init {
        this.name = "skipto"
        this.help = "指定された曲にスキップします"
        this.arguments = "<position>"
        this.aliases = bot.config.getAliases(this.name)
        this.bePlaying = true

        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.INTEGER, "position", "position", true))
        this.options = options
    }

    override fun doCommand(event: CommandEvent?) {
        var index = 0
        try {
            index = event!!.args.toInt()
        } catch (e: NumberFormatException) {
            event!!.reply(event.client.error + " `" + event.args + "` は有効な整数ではありません。")
            return
        }
        val handler = event.guild.audioManager.sendingHandler as AudioHandler?
        if (index < 1 || index > handler!!.queue.size()) {
            event.reply(event.client.error + " 1から" + handler!!.queue.size() + "の間の整数でないといけません!")
            return
        }
        handler!!.queue.skip(index - 1)
        event.reply(event.client.success + " **" + handler.queue[0].track.info.title + "にスキップしました。**")
        handler.player.stopTrack()
    }

    override fun doCommand(event: SlashCommandEvent?) {
        if (!checkDJPermission(event!!.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        var index = 0
        try {
            index = event.getOption("position")!!.asString.toInt()
        } catch (e: NumberFormatException) {
            event.reply(event.client.error + " `" + event.getOption("position")!!.asString + "` は有効な整数ではありません。")
                .queue()
            return
        }
        val handler = event.guild!!.audioManager.sendingHandler as AudioHandler?
        if (index < 1 || index > handler!!.queue.size()) {
            event.reply(event.client.error + " 1から" + handler!!.queue.size() + "の間の整数でないといけません!")
                .queue()
            return
        }
        handler!!.queue.skip(index - 1)
        event.reply(event.client.success + " **" + handler.queue[0].track.info.title + "にスキップしました。**").queue()
        handler.player.stopTrack()
    }
}
