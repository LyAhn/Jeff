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
import net.dv8tion.jda.api.Permission

class InviteCommand : SlashCommand() {
    init {
        this.name = "invite"
        this.help = "Botの招待用URLを表示します。"
        this.guildOnly = false
        this.aliases = arrayOf("share")
    }

    override fun execute(event: SlashCommandEvent) {
        val botId = event.jda.selfUser.idLong
        val permissions = arrayOf(
            Permission.MANAGE_CHANNEL,
            Permission.MANAGE_ROLES,
            Permission.MESSAGE_MANAGE,
            Permission.NICKNAME_CHANGE,
            Permission.MESSAGE_SEND,
            Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK,
            Permission.VIEW_CHANNEL,
            Permission.MESSAGE_EXT_EMOJI
        )

        event.reply(
            String.format(
                "https://discord.com/oauth2/authorize?client_id=%s&scope=bot%20applications.commands&permissions=%s",
                botId,
                Permission.getRaw(*permissions)
            )
        ).queue()
    }

    override fun execute(event: CommandEvent) {
        val botId = event.selfUser.idLong
        val permissions = arrayOf(
            Permission.MANAGE_CHANNEL,
            Permission.MANAGE_ROLES,
            Permission.MESSAGE_MANAGE,
            Permission.NICKNAME_CHANGE,
            Permission.MESSAGE_SEND,
            Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK,
            Permission.VIEW_CHANNEL,
            Permission.MESSAGE_EXT_EMOJI
        )

        event.replyFormatted(
            "https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s",
            botId,
            Permission.getRaw(*permissions)
        )
    }
}
