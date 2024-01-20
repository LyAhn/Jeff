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
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
class StopCmd(bot: Bot) : DJCommand(bot) {
    override var log: Logger = LoggerFactory.getLogger("Stop")

    init {
        this.name = "stop"
        this.help = "現在の曲を停止して再生待ちを削除します。"
        this.aliases = bot.config.getAliases(this.name)
        this.bePlaying = false

        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.STRING, "option", "再生リストを保存する場合は`save`を入力", false))

        this.options = options
    }

    override fun doCommand(event: CommandEvent?) {
        val handler = event!!.guild.audioManager.sendingHandler as AudioHandler?
        val cache = bot.cacheLoader
        val queue = handler!!.queue

        if (queue.size() > 0 && event.args.matches("save".toRegex())) {
            cache.Save(event.guild.id, handler.queue)
            event.reply(event.client.success + " 再生待ちの" + queue.size() + "曲を保存して再生を停止しました。")
            log.info(event.guild.name + "で再生待ちを保存して,ボイスチャンネルから切断しました。")
        } else {
            event.reply(event.client.success + " 再生待ちを削除して、再生を停止しました。")
        }
        handler.stopAndClear()
        event.guild.audioManager.closeAudioConnection()
    }

    override fun doCommand(event: SlashCommandEvent?) {
        if (!checkDJPermission(event!!.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val handler = event.guild!!.audioManager.sendingHandler as AudioHandler?
        val cache = bot.cacheLoader
        val queue = handler!!.queue

        log.debug("再生待ちのサイズ：" + queue.size())

        if (event.getOption("option") == null) {
            event.reply(event.client.success + " 再生待ちを削除して、再生を停止しました。").queue()
            log.info(event.guild!!.name + "で再生待ちを削除して,ボイスチャンネルから切断しました。")
            handler.stopAndClear()
            event.guild!!.audioManager.closeAudioConnection()
            return
        }

        if (queue.size() > 0 && event.getOption("option")!!.asString == "save") {
            cache.Save(event.guild!!.id, handler.queue)
            event.reply(event.client.success + " 再生待ちの" + queue.size() + "曲を保存して再生を停止しました。").queue()
            log.info(event.guild!!.name + "で再生待ちを保存して,ボイスチャンネルから切断しました。")
        } else {
            event.reply(event.client.success + " 再生待ちを削除して、再生を停止しました。").queue()
            log.info(event.guild!!.name + "で再生待ちを削除して,ボイスチャンネルから切断しました。")
        }
        handler.stopAndClear()
        event.guild!!.audioManager.closeAudioConnection()
    }

    override fun onAutoComplete(event: CommandAutoCompleteInteractionEvent) {
        val cmdOptions = arrayOf("save")
        if (event.name == "stop" && event.focusedOption.name == "option") {
            val options = Stream.of(*cmdOptions)
                .filter { word: String -> word.startsWith(event.focusedOption.value) } // only display words that start with the user's current input
                .map { word: String? ->
                    Command.Choice(
                        word!!, word
                    )
                } // map the words to choices
                .collect(Collectors.toList())
            event.replyChoices(options).queue()
        }
    }
}
