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
import com.jagrosh.jdautilities.doc.standard.CommandInfo
import com.jagrosh.jdautilities.examples.doc.Author
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import java.time.temporal.ChronoUnit

/**
 * @author John Grosh (jagrosh)
 */
@CommandInfo(name = ["Ping", "Pong"], description = "ボットのレイテンシを確認します")
@Author("John Grosh (jagrosh)")
class PingCommand : SlashCommand() {
    init {
        this.name = "ping"
        this.help = "ボットのレイテンシをチェックします"
        this.guildOnly = false
        this.aliases = arrayOf("pong")
    }

    override fun execute(event: SlashCommandEvent) {
        event.reply("Ping: ...").queue { m: InteractionHook ->
            m.editOriginal("Websocket: " + event.jda.gatewayPing + "ms").queue()
        }
    }

    override fun execute(event: CommandEvent) {
        event.reply("Ping: ...") { m: Message ->
            val ping = event.message.timeCreated.until(m.timeCreated, ChronoUnit.MILLIS)
            m.editMessage("Ping: " + ping + "ms | Websocket: " + event.jda.gatewayPing + "ms").queue()
        }
    }
}
