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
import com.jagrosh.jmusicbot.audio.QueuedTrack
import com.jagrosh.jmusicbot.queue.FairQueue
import dev.cosgy.jmusicbot.slashcommands.DJCommand
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.LoggerFactory


/**
 * ユーザーが再生リスト内のトラックを移動できるようにするコマンドです。
 */
class MoveTrackCmd(bot: Bot) : DJCommand(bot) {
    init {
        this.name = "movetrack"
        this.help = "再生待ちの曲の再生順を変更します"
        this.arguments = "<from> <to>"
        this.aliases = bot.config.getAliases(this.name)
        this.bePlaying = true

        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.INTEGER, "from", "from", true))
        options.add(OptionData(OptionType.INTEGER, "to", "to", true))
        this.options = options
    }

    override fun doCommand(event: CommandEvent?) {
        val log = LoggerFactory.getLogger("MoveTrack")
        val from: Int
        val to: Int

        val parts = event!!.args.split("\\s+".toRegex(), limit = 2).toTypedArray()
        if (parts.size < 2) {
            event.replyError("2つの有効な数字を含んでください。")
            return
        }

        try {
            // Validate the args
            from = parts[0].toInt()
            to = parts[1].toInt()
        } catch (e: NumberFormatException) {
            event.replyError("2つの有効な数字を含んでください。")
            return
        }

        if (from == to) {
            event.replyError("同じ位置に移動することはできません。")
            return
        }

        // Validate that from and to are available
        val handler = event.guild.audioManager.sendingHandler as AudioHandler?
        val queue = handler!!.queue
        if (isUnavailablePosition(queue, from)) {
            val reply = String.format("`%d` は再生待ちに存在しない位置です。", from)
            event.replyError(reply)
            return
        }
        if (isUnavailablePosition(queue, to)) {
            val reply = String.format("`%d` 再生待ちに存在しない位置です。", to)
            event.replyError(reply)
            return
        }

        // Move the track
        val track = queue.moveItem(from - 1, to - 1)
        val trackTitle = track.track.info.title
        val reply = String.format("**%s** を `%d` から `%d`に移動しました。", trackTitle, from, to)
        log.info(event.guild.name + "で %s を %d から %d に移動しました。", trackTitle, from, to)
        event.replySuccess(reply)
    }

    override fun doCommand(event: SlashCommandEvent?) {
        if (!checkDJPermission(event!!.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }

        val from = event.getOption("from")!!.asString.toInt()
        val to = event.getOption("to")!!.asString.toInt()

        if (from == to) {
            event.reply(event.client.error + "同じ位置に移動することはできません。").queue()
            return
        }

        // Validate that from and to are available
        val handler = event.guild!!.audioManager.sendingHandler as AudioHandler?
        val queue = handler!!.queue
        if (isUnavailablePosition(queue, from)) {
            val reply = String.format("`%d` は再生待ちに存在しない位置です。", from)
            event.reply(event.client.error + reply).queue()
            return
        }
        if (isUnavailablePosition(queue, to)) {
            val reply = String.format("`%d` 再生待ちに存在しない位置です。", to)
            event.reply(event.client.error + reply).queue()
            return
        }

        // Move the track
        val track = queue.moveItem(from - 1, to - 1)
        val trackTitle = track.track.info.title
        val reply = String.format("**%s** を `%d` から `%d`に移動しました。", trackTitle, from, to)
        event.reply(event.client.success + reply).queue()
    }

    companion object {
        private fun isUnavailablePosition(queue: FairQueue<QueuedTrack>, position: Int): Boolean {
            return (position < 1 || position > queue.size())
        }
    }
}
