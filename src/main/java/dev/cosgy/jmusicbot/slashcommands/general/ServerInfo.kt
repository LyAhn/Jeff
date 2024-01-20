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
import com.jagrosh.jmusicbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import java.time.format.DateTimeFormatter
import java.util.*

class ServerInfo(bot: Bot) : SlashCommand() {
    init {
        this.name = "serverinfo"
        this.help = "サーバーに関する情報を表示します"
        this.guildOnly = true
        this.category = Category("General")
        this.aliases = bot.config.getAliases(this.name)
    }

    override fun execute(event: SlashCommandEvent) {
        val guildName = event.guild!!.name
        val guildIconURL = event.guild!!.iconUrl
        val guildId = event.guild!!.id
        val guildOwner = event.guild!!.owner!!.user.name
        val guildCreatedDate = event.guild!!.timeCreated.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))

        val guildRolesCount = event.guild!!.roles.size.toString()
        val guildMember = event.guild!!.members.size.toString()
        val guildCategoryCount = event.guild!!.categories.size.toString()
        val guildTextChannelCount = event.guild!!.textChannels.size.toString()
        val guildVoiceChannelCount = event.guild!!.voiceChannels.size.toString()
        val guildStageChannelCount = event.guild!!.stageChannels.size.toString()
        val guildForumChannelCount = event.guild!!.forumChannels.size.toString()
        val guildLocation = event.guild!!.locale.nativeName

        /*
                .replace("japan", ":flag_jp: 日本")
                .replace("singapore", ":flag_sg: シンガポール")
                .replace("hongkong", ":flag_hk: 香港")
                .replace("Brazil", ":flag_br: ブラジル")
                .replace("us-central", ":flag_us: 中央アメリカ")
                .replace("us-west", ":flag_us: 西アメリカ")
                .replace("us-east", ":flag_us: 東アメリカ")
                .replace("us-south", ":flag_us: 南アメリカ")
                .replace("sydney", ":flag_au: シドニー")
                .replace("eu-west", ":flag_eu: 西ヨーロッパ")
                .replace("eu-central", ":flag_eu: 中央ヨーロッパ")
                .replace("russia", ":flag_ru: ロシア");
                 */
        val eb = EmbedBuilder()

        eb.setAuthor("サーバー $guildName の情報", null, guildIconURL)

        eb.addField("サーバーID", guildId, true)
        eb.addField("サーバー第一言語", guildLocation, true)
        eb.addField("サーバーオーナー", guildOwner, true)
        eb.addField("メンバー数", guildMember, true)
        eb.addField("役職数", guildRolesCount, true)
        eb.addField("カテゴリの数", guildCategoryCount, true)
        eb.addField("テキストチャンネルの数", guildTextChannelCount, true)
        eb.addField("ボイスチャンネルの数", guildVoiceChannelCount, true)
        eb.addField("ステージチャンネルの数", guildStageChannelCount, true)
        eb.addField("フォーラムチャンネルの数", guildForumChannelCount, true)

        eb.setFooter("サーバー作成日時: $guildCreatedDate", null)

        event.replyEmbeds(eb.build()).queue()
    }

    public override fun execute(event: CommandEvent) {
        val guildName = event.guild.name
        val guildIconURL = event.guild.iconUrl
        val guildId = event.guild.id
        val guildOwner = event.guild.owner!!.user.name
        val guildCreatedDate = event.guild.timeCreated.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))

        val guildRolesCount = event.guild.roles.size.toString()
        val guildMember = event.guild.members.size.toString()
        val guildCategoryCount = event.guild.categories.size.toString()
        val guildTextChannelCount = event.guild.textChannels.size.toString()
        val guildVoiceChannelCount = event.guild.voiceChannels.size.toString()
        val guildStageChannelCount = event.guild.stageChannels.size.toString()
        val guildForumChannelCount = event.guild.forumChannels.size.toString()
        val guildLocation = event.guild.locale.nativeName


        /*.replace("japan", ":flag_jp: 日本")
                .replace("singapore", ":flag_sg: シンガポール")
                .replace("hongkong", ":flag_hk: 香港")
                .replace("Brazil", ":flag_br: ブラジル")
                .replace("us-central", ":flag_us: 中央アメリカ")
                .replace("us-west", ":flag_us: 西アメリカ")
                .replace("us-east", ":flag_us: 東アメリカ")
                .replace("us-south", ":flag_us: 南アメリカ")
                .replace("sydney", ":flag_au: シドニー")
                .replace("eu-west", ":flag_eu: 西ヨーロッパ")
                .replace("eu-central", ":flag_eu: 中央ヨーロッパ")
                .replace("russia", ":flag_ru: ロシア");*/
        val eb = EmbedBuilder()

        eb.setAuthor("サーバー $guildName の情報", null, guildIconURL)

        eb.addField("サーバーID", guildId, true)
        eb.addField("サーバー第一言語", guildLocation, true)
        eb.addField("サーバーオーナー", guildOwner, true)
        eb.addField("メンバー数", guildMember, true)
        eb.addField("役職数", guildRolesCount, true)
        eb.addField("カテゴリの数", guildCategoryCount, true)
        eb.addField("テキストチャンネルの数", guildTextChannelCount, true)
        eb.addField("ボイスチャンネルの数", guildVoiceChannelCount, true)
        eb.addField("ステージチャンネルの数", guildStageChannelCount, true)
        eb.addField("フォーラムチャンネルの数", guildForumChannelCount, true)

        eb.setFooter("サーバー作成日時: $guildCreatedDate", null)

        event.channel.sendMessageEmbeds(eb.build()).queue()
    }
}
