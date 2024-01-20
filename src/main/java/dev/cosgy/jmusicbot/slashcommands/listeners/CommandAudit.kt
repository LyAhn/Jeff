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
package dev.cosgy.jmusicbot.slashcommands.listeners

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.CommandListener
import com.jagrosh.jmusicbot.JMusicBot
import dev.cosgy.jmusicbot.util.LastSendTextChannel.SetLastTextId
import net.dv8tion.jda.api.entities.channel.ChannelType
import org.slf4j.LoggerFactory

class CommandAudit : CommandListener {
    /**
     * Called when a [Command] is triggered
     * by a [CommandEvent].
     *
     * @param event   The CommandEvent that triggered the Command
     * @param command 実行されたコマンドオブジェクト
     */
    override fun onCommand(event: CommandEvent, command: Command) {
        if (JMusicBot.COMMAND_AUDIT_ENABLED) {
            val logger = LoggerFactory.getLogger("CommandAudit")
            val textFormat =
                if (event.isFromType(ChannelType.PRIVATE)) "%s%s で %s (%s) がコマンド %s を実行しました" else "%s の #%s で %s (%s) がコマンド %s を実行しました"

            logger.info(
                String.format(
                    textFormat,
                    if (event.isFromType(ChannelType.PRIVATE)) "DM" else event.guild.name,
                    if (event.isFromType(ChannelType.PRIVATE)) "" else event.textChannel.name,
                    event.author.name, event.author.id,
                    event.message.contentDisplay
                )
            )
        }

        SetLastTextId(event)
    }
}
