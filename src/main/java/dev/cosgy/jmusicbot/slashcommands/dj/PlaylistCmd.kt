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
import dev.cosgy.jmusicbot.slashcommands.DJCommand
import dev.cosgy.jmusicbot.util.StackTraceUtil.sendStackTrace
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.io.IOException
import java.util.function.Consumer

/**
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
class PlaylistCmd(bot: Bot) : DJCommand(bot) {
    init {
        this.guildOnly = true
        this.name = "playlist"
        this.arguments = "<append|delete|make>"
        this.help = "再生リスト管理"
        this.aliases = bot.config.getAliases(this.name)
        this.children = arrayOf(
            ListCmd(bot),
            AppendlistCmd(bot),
            DeletelistCmd(bot),
            MakelistCmd(bot)
        )
    }

    override fun doCommand(event: CommandEvent?) {
        val builder = StringBuilder(event!!.client.warning + " 再生リスト管理コマンド:\n")
        for (cmd in this.children) builder.append("\n`").append(
            event.client.prefix
        ).append(name).append(" ").append(cmd.name)
            .append(" ").append(if (cmd.arguments == null) "" else cmd.arguments).append("` - ").append(cmd.help)
        event.reply(builder.toString())
    }

    override fun doCommand(event: SlashCommandEvent?) {
        // ここは、実行されません。
    }

    inner class MakelistCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "make"
            this.aliases = arrayOf("create")
            this.help = "再生リストを新規作成"
            this.arguments = "<name>"
            this.guildOnly = true
            this.ownerCommand = false

            val options: MutableList<OptionData> = ArrayList()
            options.add(OptionData(OptionType.STRING, "name", "プレイリスト名", true))
            this.options = options
        }

        override fun doCommand(event: CommandEvent?) {
            val pName = event!!.args.replace("\\s+".toRegex(), "_")
            val guildId = event.guild.id

            if (pName.isEmpty()) {
                event.replyError("プレイリストの名前を入力してください。")
            } else if (bot.playlistLoader.getPlaylist(guildId, pName) == null) {
                try {
                    bot.playlistLoader.createPlaylist(guildId, pName)
                    event.reply(event.client.success + "再生リスト `" + pName + "` を作成しました")
                } catch (e: IOException) {
                    if (event.isOwner || event.member.isOwner) {
                        event.replyError(
                            """
    曲の読み込み中にエラーが発生しました。
    **エラーの内容: ${e.localizedMessage}**
    """.trimIndent()
                        )
                        sendStackTrace(event.textChannel, e)
                        return
                    }

                    event.reply(event.client.error + " 再生リストを作成できませんでした。:" + e.localizedMessage)
                }
            } else {
                event.reply(event.client.error + " 再生リスト `" + pName + "` は既に存在します")
            }
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!checkDJPermission(event!!.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val pname = event.getOption("name")!!.asString
            val guildId = event.guild!!.id
            if (pname.isEmpty()) {
                event.reply(event.client.error + "プレイリストの名前を入力してください。").queue()
            } else if (bot.playlistLoader.getPlaylist(guildId, pname) == null) {
                try {
                    bot.playlistLoader.createPlaylist(guildId, pname)
                    event.reply(event.client.success + "再生リスト `" + pname + "` を作成しました").queue()
                } catch (e: IOException) {
                    if (event.client.ownerId === event.member!!.id || event.member!!.isOwner) {
                        event.reply(
                            """
    ${event.client.error}曲の読み込み中にエラーが発生しました。
    **エラーの内容: ${e.localizedMessage}**
    """.trimIndent()
                        ).queue()
                        sendStackTrace(event.textChannel, e)
                        return
                    }

                    event.reply(event.client.error + " 再生リストを作成できませんでした。:" + e.localizedMessage).queue()
                }
            } else {
                event.reply(event.client.error + " 再生リスト `" + pname + "` は既に存在します").queue()
            }
        }
    }

    inner class DeletelistCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "delete"
            this.aliases = arrayOf("remove")
            this.help = "既存の再生リストを削除"
            this.arguments = "<name>"
            this.guildOnly = true
            this.ownerCommand = false
            val options: MutableList<OptionData> = ArrayList()
            options.add(OptionData(OptionType.STRING, "name", "プレイリスト名", true))
            this.options = options
        }

        override fun doCommand(event: CommandEvent?) {
            val pname = event!!.args.replace("\\s+".toRegex(), "_")
            val guildid = event.guild.id
            if (pname != "") {
                if (bot.playlistLoader.getPlaylist(
                        guildid,
                        pname
                    ) == null
                ) event.reply(event.client.error + " 再生リストは存在しません:`" + pname + "`")
                else {
                    try {
                        bot.playlistLoader.deletePlaylist(guildid, pname)
                        event.reply(event.client.success + " 再生リストを削除しました:`" + pname + "`")
                    } catch (e: IOException) {
                        event.reply(event.client.error + " 再生リストを削除できませんでした: " + e.localizedMessage)
                    }
                }
            } else {
                event.reply(event.client.error + "再生リストの名前を含めてください")
            }
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!checkDJPermission(event!!.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val pname = event.getOption("name")!!.asString
            val guildid = event.guild!!.id
            if (bot.playlistLoader.getPlaylist(
                    guildid,
                    pname
                ) == null
            ) event.reply(event.client.error + " 再生リストは存在しません:`" + pname + "`").queue()
            else {
                try {
                    bot.playlistLoader.deletePlaylist(guildid, pname)
                    event.reply(event.client.success + " 再生リストを削除しました:`" + pname + "`").queue()
                } catch (e: IOException) {
                    event.reply(event.client.error + " 再生リストを削除できませんでした: " + e.localizedMessage).queue()
                }
            }
        }
    }

    inner class AppendlistCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "append"
            this.aliases = arrayOf("add")
            this.help = "既存の再生リストに曲を追加"
            this.arguments = "<name> <URL>| <URL> | ..."
            this.guildOnly = true
            this.ownerCommand = false
            val options: MutableList<OptionData> = ArrayList()
            options.add(OptionData(OptionType.STRING, "name", "プレイリスト名", true))
            options.add(OptionData(OptionType.STRING, "url", "URL", true))
            this.options = options
        }

        override fun doCommand(event: CommandEvent?) {
            val parts = event!!.args.split("\\s+".toRegex(), limit = 2).toTypedArray()
            val guildid = event.guild.id
            if (parts.size < 2) {
                event.reply(event.client.error + " 追加先の再生リスト名とURLを含めてください。")
                return
            }
            val pname = parts[0]
            val playlist = bot.playlistLoader.getPlaylist(guildid, pname)
            if (playlist == null) event.reply(event.client.error + " 再生リストは存在しません:`" + pname + "`")
            else {
                val builder = StringBuilder()
                playlist.items.forEach(Consumer { item: String? -> builder.append("\r\n").append(item) })
                val urls = parts[1].split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (url in urls) {
                    var u = url.trim { it <= ' ' }
                    if (u.startsWith("<") && u.endsWith(">")) u = u.substring(1, u.length - 1)
                    builder.append("\r\n").append(u)
                }
                try {
                    bot.playlistLoader.writePlaylist(guildid, pname, builder.toString())
                    event.reply(event.client.success + urls.size + " 項目を再生リストに追加しました:`" + pname + "`")
                } catch (e: IOException) {
                    event.reply(event.client.error + " 再生リストに追加できませんでした: " + e.localizedMessage)
                }
            }
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!checkDJPermission(event!!.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }

            val guildid = event.guild!!.id
            val pname = event.getOption("name")!!.asString
            val playlist = bot.playlistLoader.getPlaylist(guildid, pname)
            if (playlist == null) event.reply(event.client.error + " 再生リストは存在しません:`" + pname + "`").queue()
            else {
                val builder = StringBuilder()
                playlist.items.forEach(Consumer { item: String? -> builder.append("\r\n").append(item) })
                val urls = event.getOption("url")!!.asString.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                for (url in urls) {
                    var u = url.trim { it <= ' ' }
                    if (u.startsWith("<") && u.endsWith(">")) u = u.substring(1, u.length - 1)
                    builder.append("\r\n").append(u)
                }
                try {
                    bot.playlistLoader.writePlaylist(guildid, pname, builder.toString())
                    event.reply(event.client.success + urls.size + " 項目を再生リストに追加しました:`" + pname + "`")
                        .queue()
                } catch (e: IOException) {
                    event.reply(event.client.error + " 再生リストに追加できませんでした: " + e.localizedMessage).queue()
                }
            }
        }
    }

    inner class ListCmd(bot: Bot?) : DJCommand(bot) {
        init {
            this.name = "all"
            this.aliases = arrayOf("available", "list")
            this.help = "利用可能なすべての再生リストを表示"
            this.guildOnly = true
            this.ownerCommand = false
        }

        override fun doCommand(event: CommandEvent?) {
            val guildId = event!!.guild.id

            if (!bot.playlistLoader.folderGuildExists(guildId)) bot.playlistLoader.createGuildFolder(guildId)
            if (!bot.playlistLoader.folderGuildExists(guildId)) {
                event.reply(event.client.warning + " 再生リストフォルダが存在しないため作成できませんでした。")
                return
            }
            val list = bot.playlistLoader.getPlaylistNames(guildId)
            if (list == null) event.reply(event.client.error + " 利用可能な再生リストを読み込めませんでした。")
            else if (list.isEmpty()) event.reply(event.client.warning + " 再生リストフォルダに再生リストがありません。")
            else {
                val builder = StringBuilder(event.client.success + " 利用可能な再生リスト:\n")
                list.forEach(Consumer { str: String? -> builder.append("`").append(str).append("` ") })
                event.reply(builder.toString())
            }
        }

        override fun doCommand(event: SlashCommandEvent?) {
            if (!checkDJPermission(event!!.client, event)) {
                event.reply(event.client.warning + "権限がないため実行できません。").queue()
                return
            }
            val guildId = event.guild!!.id
            if (!bot.playlistLoader.folderGuildExists(guildId)) bot.playlistLoader.createGuildFolder(guildId)
            if (!bot.playlistLoader.folderGuildExists(guildId)) {
                event.reply(event.client.warning + " 再生リストフォルダが存在しないため作成できませんでした。").queue()
                return
            }
            val list = bot.playlistLoader.getPlaylistNames(guildId)
            if (list == null) event.reply(event.client.error + " 利用可能な再生リストを読み込めませんでした。").queue()
            else if (list.isEmpty()) event.reply(event.client.warning + " 再生リストフォルダに再生リストがありません。")
                .queue()
            else {
                val builder = StringBuilder(event.client.success + " 利用可能な再生リスト:\n")
                list.forEach(Consumer { str: String? -> builder.append("`").append(str).append("` ") })
                event.reply(builder.toString()).queue()
            }
        }
    }
}