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
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo
import com.jagrosh.jdautilities.doc.standard.CommandInfo
import com.jagrosh.jdautilities.examples.doc.Author
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.ChannelType
import org.slf4j.LoggerFactory
import java.awt.Color
import java.util.*

/**
 * @author Cosgy Dev
 */
@CommandInfo(name = ["About"], description = "ボットに関する情報を表示します")
@Author("Cosgy Dev")
class AboutCommand(
    private val color: Color,
    private val description: String,
    private val features: Array<String>,
    vararg perms: Permission
) : SlashCommand() {
    private val perms: Array<out Permission>
    private var IS_AUTHOR = true
    private var REPLACEMENT_ICON = "+"
    private var oauthLink: String? = null

    init {
        this.name = "about"
        this.help = "ボットに関する情報を表示します"
        this.aliases = arrayOf("botinfo", "info")
        this.guildOnly = false
        this.perms = perms
        this.botPermissions = arrayOf(Permission.MESSAGE_EMBED_LINKS)
    }

    fun setIsAuthor(value: Boolean) {
        this.IS_AUTHOR = value
    }

    fun setReplacementCharacter(value: String) {
        this.REPLACEMENT_ICON = value
    }

    override fun execute(event: SlashCommandEvent) {
        if (oauthLink == null) {
            try {
                val info = event.jda.retrieveApplicationInfo().complete()
                oauthLink = if (info.isBotPublic) info.getInviteUrl(0L, *perms) else ""
            } catch (e: Exception) {
                val log = LoggerFactory.getLogger("OAuth2")
                log.error("招待リンクを生成できませんでした ", e)
                oauthLink = ""
            }
        }
        val builder = EmbedBuilder()
        builder.setColor(if (event.guild == null) color else event.guild!!.selfMember.color)
        builder.setAuthor("" + event.jda.selfUser.name + "についての情報", null, event.jda.selfUser.avatarUrl)
        val CosgyOwner = "Cosgy Devが運営、開発をしています。"
        val author = if (event.jda.getUserById(event.client.ownerId) == null) "<@" + event.client.ownerId + ">"
        else event.jda.getUserById(event.client.ownerId)!!.name
        val descr = StringBuilder().append("こんにちは！ **").append(event.jda.selfUser.name).append("**です。 ")
            .append(description).append("は、")
            .append("[" + JDAUtilitiesInfo.AUTHOR + "](https://github.com/JDA-Applications)の[Commands Extension](" + JDAUtilitiesInfo.GITHUB + ") (")
            .append(JDAUtilitiesInfo.VERSION).append(")と[JDA library](https://github.com/DV8FromTheWorld/JDA) (")
            .append(JDAInfo.VERSION).append(")を使用しており、")
            .append((if (IS_AUTHOR) CosgyOwner else author + "が所有しています。"))
            .append(event.jda.selfUser.name)
            .append("についての質問などは[Cosgy Dev公式チャンネル](https://discord.gg/RBpkHxf)へお願いします。")
            .append("\nこのボットの使用方法は`").append("/help")
            .append("`で確認することができます。").append("\n\n機能の特徴： ```css")
        for (feature in features) descr.append("\n")
            .append(if (event.client.success.startsWith("<")) REPLACEMENT_ICON else event.client.success).append(" ")
            .append(feature)
        descr.append(" ```")
        builder.setDescription(descr)

        if (event.jda.shardInfo.shardTotal == 1) {
            builder.addField(
                "ステータス", """${event.jda.guilds.size} サーバー
1 シャード""", true
            )
            builder.addField("ユーザー", """${event.jda.users.size} ユニーク
${event.jda.guilds.stream().mapToInt { g: Guild -> g.members.size }.sum()} 合計""", true
            )
            builder.addField(
                "チャンネル", """${event.jda.textChannels.size} テキスト
${event.jda.voiceChannels.size} ボイス""", true
            )
        } else {
            builder.addField(
                "ステータス", """${event.client.totalGuilds} サーバー
シャード ${event.jda.shardInfo.shardId + 1}/${event.jda.shardInfo.shardTotal}""", true
            )
            builder.addField(
                "", """${event.jda.users.size} ユーザーのシャード
${event.jda.guilds.size} サーバー""", true
            )
            builder.addField(
                "", """${event.jda.textChannels.size} テキストチャンネル
${event.jda.voiceChannels.size} ボイスチャンネル""", true
            )
        }
        builder.setFooter("再起動が行われた時間", "https://www.cosgy.dev/wp-content/uploads/2020/03/restart.jpg")
        builder.setTimestamp(event.client.startTime)
        event.replyEmbeds(builder.build()).queue()
    }

    override fun execute(event: CommandEvent) {
        if (oauthLink == null) {
            try {
                val info = event.jda.retrieveApplicationInfo().complete()
                oauthLink = if (info.isBotPublic) info.getInviteUrl(0L, *perms) else ""
            } catch (e: Exception) {
                val log = LoggerFactory.getLogger("OAuth2")
                log.error("招待リンクを生成できませんでした ", e)
                oauthLink = ""
            }
        }
        val builder = EmbedBuilder()
        builder.setColor(if (event.isFromType(ChannelType.TEXT)) event.guild.selfMember.color else color)
        builder.setAuthor("" + event.selfUser.name + "について!", null, event.selfUser.avatarUrl)
        val CosgyOwner = "Cosgy Devが運営、開発をしています。"
        val author = if (event.jda.getUserById(event.client.ownerId) == null) "<@" + event.client.ownerId + ">"
        else event.jda.getUserById(event.client.ownerId)!!.name
        val descr = StringBuilder().append("こんにちは！ **").append(event.selfUser.name).append("**です。 ")
            .append(description).append("は、")
            .append(JDAUtilitiesInfo.AUTHOR + "の[コマンド拡張](" + JDAUtilitiesInfo.GITHUB + ") (")
            .append(JDAUtilitiesInfo.VERSION).append(")と[JDAライブラリ](https://github.com/DV8FromTheWorld/JDA) (")
            .append(JDAInfo.VERSION).append(")を使用しており、")
            .append((if (IS_AUTHOR) CosgyOwner else author + "が所有しています。"))
            .append(event.selfUser.name)
            .append("についての質問などは[Cosgy Dev公式チャンネル](https://discord.gg/RBpkHxf)へお願いします。")
            .append("\nこのボットの使用方法は`").append(event.client.textualPrefix).append(event.client.helpWord)
            .append("`で確認することができます。").append("\n\n機能の特徴： ```css")
        for (feature in features) descr.append("\n")
            .append(if (event.client.success.startsWith("<")) REPLACEMENT_ICON else event.client.success).append(" ")
            .append(feature)
        descr.append(" ```")
        builder.setDescription(descr)

        if (event.jda.shardInfo.shardTotal == 1) {
            builder.addField(
                "ステータス", """${event.jda.guilds.size} サーバー
1 シャード""", true
            )
            builder.addField("ユーザー", """${event.jda.users.size} ユニーク
${event.jda.guilds.stream().mapToInt { g: Guild -> g.members.size }.sum()} 合計""", true
            )
            builder.addField(
                "チャンネル", """${event.jda.textChannels.size} テキスト
${event.jda.voiceChannels.size} ボイス""", true
            )
        } else {
            builder.addField(
                "ステータス", """${event.client.totalGuilds} サーバー
シャード ${event.jda.shardInfo.shardId + 1}/${event.jda.shardInfo.shardTotal}""", true
            )
            builder.addField(
                "", """${event.jda.users.size} ユーザーのシャード
${event.jda.guilds.size} サーバー""", true
            )
            builder.addField(
                "", """${event.jda.textChannels.size} テキストチャンネル
${event.jda.voiceChannels.size} ボイスチャンネル""", true
            )
        }
        builder.setFooter("再起動が行われた時間", "https://www.cosgy.dev/wp-content/uploads/2020/03/restart.jpg")
        builder.setTimestamp(event.client.startTime)
        event.reply(builder.build())
    }
}
