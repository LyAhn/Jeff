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
import com.jagrosh.jdautilities.menu.Paginator
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.utils.FormatUtil
import dev.cosgy.jmusicbot.slashcommands.DJCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.PermissionException
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * @author Kosugi_kun
 */
class CashCmd(var bot: Bot) : SlashCommand() {
    private val builder: Paginator.Builder

    init {
        this.name = "cache"
        this.help = "キャッシュに保存されている曲を表示します。"
        this.guildOnly = true
        this.category = Category("General")
        this.aliases = bot.config.getAliases(this.name)
        this.children = arrayOf(DeleteCmd(bot), ShowCmd(bot))
        this.botPermissions = arrayOf(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS)
        builder = Paginator.Builder()
            .setColumns(1)
            .setFinalAction { m: Message ->
                try {
                    m.clearReactions().queue()
                } catch (ignore: PermissionException) {
                }
            }
            .setItemsPerPage(10)
            .waitOnSinglePage(false)
            .useNumberedItems(true)
            .showPageNumbers(true)
            .wrapPageEnds(true)
            .setEventWaiter(bot.waiter)
            .setTimeout(1, TimeUnit.MINUTES)
    }

    override fun execute(slashCommandEvent: SlashCommandEvent) {
    }

    override fun execute(event: CommandEvent) {
        if (!bot.cacheLoader.cacheExists(event.guild.id)) {
            event.reply("キャッシュに保存された曲がありませんでした。")
            return
        }
        var pagenum = 1
        try {
            pagenum = event.args.toInt()
        } catch (ignore: NumberFormatException) {
        }

        val cache = bot.cacheLoader.GetCache(event.guild.id)

        val songs = arrayOfNulls<String>(cache.size)
        var total: Long = 0
        for (i in cache.indices) {
            total += cache[i].length!!.toLong()
            songs[i] =
                "`[" + FormatUtil.formatTime(cache[i].length!!.toLong()) + "]` **" + cache[i].title + "** - <@" + cache[i].userId + ">"
        }
        val finTotal = total
        builder.setText { i1: Int?, i2: Int? -> getQueueTitle(event.client.success, songs.size, finTotal) }
            .setItems(*songs)
            .setUsers(event.author)
            .setColor(event.selfMember.color)

        builder.build().paginate(event.channel, pagenum)
    }

    private fun getQueueTitle(success: String, songsLength: Int, total: Long): String {
        val sb = StringBuilder()

        return FormatUtil.filter(
            sb.append(success).append(" キャッシュに保存された曲一覧 | ").append(songsLength)
                .append(" 曲 | `").append(FormatUtil.formatTime(total)).append("` ")
                .toString()
        )
    }

    class DeleteCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "delete"
            this.aliases = arrayOf("dl", "clear")
            this.help = "保存されているキャッシュを削除します。"
            this.guildOnly = true
        }

        override fun doCommand(event: CommandEvent?) {
            if (!bot.cacheLoader.cacheExists(event!!.guild.id)) {
                event.reply("キャッシュが存在しません。")
                return
            }

            try {
                bot.cacheLoader.deleteCache(event.guild.id)
            } catch (e: IOException) {
                event.reply("キャッシュを削除する際にエラーが発生しました。")
                e.printStackTrace()
                return
            }
            event.reply("キャッシュを削除しました。")
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!bot.cacheLoader.cacheExists(event!!.guild!!.id)) {
                event.reply("キャッシュが存在しません。").queue()
                return
            }

            try {
                bot.cacheLoader.deleteCache(event.guild!!.id)
            } catch (e: IOException) {
                event.reply("キャッシュを削除する際にエラーが発生しました。").queue()
                e.printStackTrace()
                return
            }
            event.reply("キャッシュを削除しました。").queue()
        }
    }

    inner class ShowCmd(bot: Bot) : SlashCommand() {
        private val builder: Paginator.Builder

        init {
            this.name = "show"
            this.help = "キャッシュされている楽曲を一覧表示します。"
            this.guildOnly = true
            this.botPermissions = arrayOf(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS)
            builder = Paginator.Builder()
                .setColumns(1)
                .setFinalAction { m: Message ->
                    try {
                        m.clearReactions().queue()
                    } catch (ignore: PermissionException) {
                    }
                }
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(bot.waiter)
                .setTimeout(1, TimeUnit.MINUTES)
        }

        override fun execute(event: SlashCommandEvent) {
            if (!bot.cacheLoader.cacheExists(event.guild!!.id)) {
                event.reply("キャッシュに保存された曲がありませんでした。").queue()
                return
            }
            val pagenum = 1
            event.reply("キャッシュを取得します。").queue()

            val cache = bot.cacheLoader.GetCache(
                event.guild!!.id
            )

            val songs = arrayOfNulls<String>(cache.size)
            var total: Long = 0
            for (i in cache.indices) {
                total += cache[i].length!!.toLong()
                songs[i] =
                    "`[" + FormatUtil.formatTime(cache[i].length!!.toLong()) + "]` **" + cache[i].title + "** - <@" + cache[i].userId + ">"
            }
            val finTotal = total
            builder.setText { i1: Int?, i2: Int? -> getQueueTitle(event.client.success, songs.size, finTotal) }
                .setItems(*songs)
                .setUsers(event.user)
                .setColor(event.member!!.color)
            builder.build().paginate(event.channel, pagenum)
        }
    }
}
