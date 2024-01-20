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
package dev.cosgy.jmusicbot.slashcommands

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.command.SlashCommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.settings.Settings
import net.dv8tion.jda.api.Permission
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
abstract class DJCommand(bot: Bot?) : MusicCommand(bot) {
    init {
        this.category = Category("DJ") { event: CommandEvent -> checkDJPermission(event) }
    }

    companion object {
        var log: Logger = LoggerFactory.getLogger("DJCommand")

        @JvmStatic
        fun checkDJPermission(event: CommandEvent): Boolean {
            if (event.author.id == event.client.ownerId) {
                return true
            }
            if (event.guild == null) {
                return true
            }
            if (event.member.hasPermission(Permission.MANAGE_SERVER)) {
                return true
            }
            val settings = event.client.getSettingsFor<Settings>(event.guild)
            val dj = settings.getRole(event.guild)
            return dj != null && (event.member.roles.contains(dj) || dj.idLong == event.guild.idLong)
        }

        @JvmStatic
        fun checkDJPermission(client: CommandClient, event: SlashCommandEvent): Boolean {
            if (event.user.id == client.ownerId) {
                return true
            }
            if (event.guild == null) {
                return true
            }
            if (event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
                return true
            }
            val settings = client.getSettingsFor<Settings>(event.guild)
            val dj = settings.getRole(event.guild)
            return dj != null && (event.member!!
                .roles.contains(dj) || dj.idLong == event.guild!!.idLong)
        }
    }
}
