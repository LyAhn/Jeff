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
import com.jagrosh.jdautilities.commons.utils.FinderUtil
import com.jagrosh.jdautilities.menu.Menu
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.audio.AudioHandler
import dev.cosgy.jmusicbot.slashcommands.DJCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

// TODO: 選択リストはDiscord公式が提供しているものに変更する

/**
 * @author Michaili K.
 */
class ForceRemoveCmd(bot: Bot) : DJCommand(bot) {
    init {
        this.name = "forceremove"
        this.help = "指定したユーザーのエントリーを再生待ちから削除します"
        this.arguments = "<ユーザー>"
        this.aliases = bot.config.getAliases(this.name)
        this.beListening = false
        this.bePlaying = true
        this.botPermissions = arrayOf(Permission.MESSAGE_EMBED_LINKS)

        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.USER, "user", "ユーザー", true))
        this.options = options
    }

    override fun doCommand(event: CommandEvent?) {
        if (event!!.args.isEmpty()) {
            event.replyError("ユーザーに言及する必要があります！")
            return
        }

        val handler = event.guild.audioManager.sendingHandler as AudioHandler?
        if (handler!!.queue.isEmpty) {
            event.replyError("再生待ちには何もありません！")
            return
        }


        val target: User
        val found = FinderUtil.findMembers(event.args, event.guild)


        if(found.isEmpty()) {
            event.replyError("ユーザーが見つかりません！")
            return
        }
        if(found.size > 1) {
            val test = StringSelectMenu.create("user")
                .setPlaceholder("ユーザーを選択してください")

            for (user in found) {
                test.addOption("**" + user.user.name + "**", user.user.id)
            }

            val message: MessageCreateData = MessageCreateBuilder().setContent("ユーザーを選択してください")
                .addActionRow(test.build()).build()

            event.reply(message)
            return
        }
        target = found[0].user
        removeAllEntries(target, event)
    }

    override fun doCommand(event: SlashCommandEvent?) {
        if (!checkDJPermission(event!!.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val handler = event.guild!!.audioManager.sendingHandler as AudioHandler?
        if (handler!!.queue.isEmpty) {
            event.reply(event.client.error + "再生待ちには何もありません！").queue()
            return
        }

        val target = event.getOption("user")!!.asUser
        val count =
            (event.guild!!.audioManager.sendingHandler as AudioHandler?)!!.queue.removeAll(target.idLong)
        if (count == 0) {
            event.reply(event.client.warning + "**" + target.name + "** の再生待ちに曲がありません！").queue()
        } else {
            event.reply(event.client.success + "**" + target.name + "**がリクエストした`" + count + "`曲を削除しました。")
                .queue()
        }
    }

    private fun removeAllEntries(target: User, event: CommandEvent?) {
        val count =
            (event!!.guild.audioManager.sendingHandler as AudioHandler?)!!.queue.removeAll(target.idLong)
        if (count == 0) {
            event.replyWarning("**" + target.name + "** の再生待ちに曲がありません！")
        } else {
            event.replySuccess("**" + target.name + "**がリクエストした`" + count + "`曲を削除しました。")
        }
    }
}