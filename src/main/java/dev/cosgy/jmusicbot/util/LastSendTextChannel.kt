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
package dev.cosgy.jmusicbot.util

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.CommandListener
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object LastSendTextChannel : CommandListener {
    // ギルドIDでテキストチャンネルのIDを持ってきます。
    private val textChannel = HashMap<Long, Long>()
    var log: Logger = LoggerFactory.getLogger("LastSendTextChannel")

    @JvmStatic
    fun SetLastTextId(event: CommandEvent) {
        textChannel[event.guild.idLong] = event.textChannel.idLong
    }

    fun GetLastTextId(guildId: Long): Long {
        val id = if (textChannel.containsKey(guildId)) {
            textChannel[guildId]!!
        } else {
            0
        }
        return id
    }

    @JvmStatic
    fun SendMessage(guild: Guild, message: String?) {
        log.debug("メッセージを送信します。")
        val textId = GetLastTextId(guild.idLong)
        if (textId == 0L) {
            log.debug("チャンネルが保存されていなかったため、メッセージを送信できませんでした。")
            return
        }
        val channel: MessageChannel? = guild.getTextChannelById(textId)
        channel!!.sendMessage(message!!).queue()
    }
}
