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
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter

class UserInfo : SlashCommand() {
    var log: Logger = LoggerFactory.getLogger("UserInfo")

    init {
        this.name = "userinfo"
        this.help = "指定したユーザーに関する情報を表示します"
        this.arguments = "<ユーザー>"
        this.guildOnly = true
        this.category = Category("General")

        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.USER, "user", "ユーザー", true))
        this.options = options
    }

    override fun execute(event: SlashCommandEvent) {
        val memb = event.getOption("user")!!.asMember

        val eb = EmbedBuilder().setColor(memb!!.color)
        val name = memb.effectiveName
        val guildJoinData = memb.timeJoined.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        val discordJoinedData = memb.user.timeCreated.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        val id = memb.user.id
        val status =
            memb.onlineStatus.key.replace("offline", ":x: オフライン").replace("dnd", ":red_circle: 起こさないで")
                .replace("idle", "退席中").replace("online", ":white_check_mark: オンライン")
        var roles: String
        var avatar = memb.user.avatarUrl

        log.debug(
            """
    
    ユーザー名:${memb.effectiveName}
    ギルド参加日時:
    """.trimIndent()
                    + memb.user.timeCreated
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) + "\n" +
                    "ユーザーID:" + memb.user.id + "\n" +
                    "オンライン状態:" + memb.onlineStatus
        )

        val game = try {
            memb.activities.toString()
        } catch (e: Exception) {
            "-/-"
        }

        val rolesBuilder = StringBuilder()
        for (r in memb.roles) {
            rolesBuilder.append(r.name).append(", ")
        }
        roles = rolesBuilder.toString()
        roles = if (roles.isNotEmpty()) roles.substring(0, roles.length - 2)
        else "このサーバーには役職が存在しません"

        if (avatar == null) {
            avatar = "アイコンなし"
        }

        eb.setAuthor(memb.user.name + " のユーザー情報", null, null)
            .addField(":pencil2: 名前/ニックネーム", "**$name**", true)
            .addField(":1234: ID", "**$id**", true)
            .addBlankField(false)
            .addField(":signal_strength: 現在のステータス", "**$status**", true)
            .addField(":video_game: プレイ中のゲーム", "**$game**", true)
            .addField(":tools: 役職", "**$roles**", true)
            .addBlankField(false)
            .addField(":inbox_tray: サーバー参加日", "**$guildJoinData**", true)
            .addField(":beginner: アカウント作成日", "**$discordJoinedData**", true)
            .addBlankField(false)
            .addField(":frame_photo: アイコンURL", avatar, false)

        if (avatar != "アイコンなし") {
            eb.setAuthor(memb.user.name + " のユーザー情報", null, avatar)
        }

        event.replyEmbeds(eb.build()).queue()
    }

    public override fun execute(event: CommandEvent) {
        val memb = if (event.args.length > 0) {
            try {
                if (event.message.referencedMessage!!.mentions.members.size != 0) {
                    event.message.referencedMessage!!.mentions.members[0]
                } else {
                    FinderUtil.findMembers(event.args, event.guild)[0]
                }
            } catch (e: Exception) {
                event.reply("ユーザー \"" + event.args + "\" は見つかりませんでした。")
                return
            }
        } else {
            event.member
        }

        val eb = EmbedBuilder().setColor(memb.color)
        val name = memb.effectiveName
        val guildJoinData = memb.timeJoined.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        val discordJoinedData = memb.user.timeCreated.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        val id = memb.user.id
        val status =
            memb.onlineStatus.key.replace("offline", ":x: オフライン").replace("dnd", ":red_circle: 起こさないで")
                .replace("idle", "退席中").replace("online", ":white_check_mark: オンライン")
        var roles: String
        var avatar = memb.user.avatarUrl

        log.debug(
            """
    
    ユーザー名:${memb.effectiveName}
    ギルド参加日時:
    """.trimIndent()
                    + memb.user.timeCreated
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")) + "\n" +
                    "ユーザーID:" + memb.user.id + "\n" +
                    "オンライン状態:" + memb.onlineStatus
        )

        val game = try {
            memb.activities.toString()
        } catch (e: Exception) {
            "-/-"
        }

        val ROLESBuilder = StringBuilder()
        for (r in memb.roles) {
            ROLESBuilder.append(r.name).append(", ")
        }
        roles = ROLESBuilder.toString()
        roles = if (roles.isNotEmpty()) roles.substring(0, roles.length - 2)
        else "このサーバーには役職が存在しません"

        if (avatar == null) {
            avatar = "アイコンなし"
        }

        eb.setAuthor(memb.user.name + " のユーザー情報", null, null)
            .addField(":pencil2: 名前/ニックネーム", "**$name**", true)
            .addField(":1234: ID", "**$id**", true)
            .addBlankField(false)
            .addField(":signal_strength: 現在のステータス", "**$status**", true)
            .addField(":video_game: プレイ中のゲーム", "**$game**", true)
            .addField(":tools: 役職", "**$roles**", true)
            .addBlankField(false)
            .addField(":inbox_tray: サーバー参加日", "**$guildJoinData**", true)
            .addField(":beginner: アカウント作成日", "**$discordJoinedData**", true)
            .addBlankField(false)
            .addField(":frame_photo: アイコンURL", avatar, false)

        if (avatar != "アイコンなし") {
            eb.setAuthor(memb.user.name +  " のユーザー情報", null, avatar)
        }

        event.channel.sendMessageEmbeds(eb.build()).queue()
    }
}
