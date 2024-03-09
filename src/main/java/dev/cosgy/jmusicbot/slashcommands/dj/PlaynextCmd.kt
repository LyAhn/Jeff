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
import com.jagrosh.jmusicbot.audio.AudioHandler
import com.jagrosh.jmusicbot.audio.QueuedTrack
import com.jagrosh.jmusicbot.utils.FormatUtil
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.cosgy.jmusicbot.slashcommands.DJCommand
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
class PlaynextCmd(bot: Bot) : DJCommand(bot) {
    private val loadingEmoji: String = bot.config.loading
    override var log: Logger = LoggerFactory.getLogger("Playnext")

    init {
        this.name = "playnext"
        this.arguments = "<title|URL>"
        this.help = "次に再生する曲を指定します"
        this.aliases = bot.config.getAliases(this.name)
        this.beListening = true
        this.bePlaying = false
        val options: MutableList<OptionData> = ArrayList()
        options.add(OptionData(OptionType.STRING, "title", "タイトルまたはURL", true))
        this.options = options
    }

    override fun doCommand(event: CommandEvent?) {
        if (event!!.args.isEmpty() && event.message.attachments.isEmpty()) {
            event.replyWarning("曲のタイトルまたはURLを入力してください。")
            return
        }
        val args = if (event.args.startsWith("<") && event.args.endsWith(">")
        ) event.args.substring(1, event.args.length - 1)
        else if (event.args.isEmpty()) event.message.attachments[0].url else event.args
        log.info(event.guild.name + "で[" + args + "]の読み込みを開始しました。")
        event.reply("$loadingEmoji`[$args]`を読み込み中です...") { m: Message? ->
            bot!!.playerManager.loadItemOrdered(
                event.guild, args, m?.let { ResultHandler(it, event, false) }
            )
        }
    }

    override fun doCommand(event: SlashCommandEvent?) {
        if (!checkDJPermission(event!!.client, event)) {
            event.reply(event.client.warning + "権限がないため実行できません。").queue()
            return
        }
        val args = event.getOption("title")!!.asString
        log.info(event.guild!!.name + "で[" + args + "]の読み込みを開始しました。")
        event.reply("$loadingEmoji`[$args]`を読み込み中です...").queue { m: InteractionHook? ->
            bot!!.playerManager.loadItemOrdered(
                event.guild, args, m?.let { SlashResultHandler(it, event, false) }
            )
        }
    }

    private inner class SlashResultHandler(
        private val m: InteractionHook,
        private val event: SlashCommandEvent,
        private val ytsearch: Boolean
    ) : AudioLoadResultHandler {
        private fun loadSingle(track: AudioTrack) {
            if (bot!!.config.isTooLong(track)) {
                m.editOriginal(
                    FormatUtil.filter(
                        event.client.warning + "(**" + (if (track.info.uri.contains("https://stream.gensokyoradio.net/")) "幻想郷ラジオ" else track.info.title) + "**) このトラックは許可されている最大長よりも長いです: `"
                                + FormatUtil.formatTime(track.duration) + "` > `" + FormatUtil.formatTime(bot.config.maxSeconds * 1000) + "`"
                    )
                ).queue()
                return
            }
            val handler = event.guild!!.audioManager.sendingHandler as AudioHandler?
            val pos = handler!!.addTrackToFront(QueuedTrack(track, event.user)) + 1
            val addMsg = FormatUtil.filter(
                event.client.success + "**" + (if (track.info.uri.contains("https://stream.gensokyoradio.net/")) "幻想郷ラジオ" else track.info.title)
                        + "** (`" + FormatUtil.formatTime(track.duration) + "`) " + (if (pos == 0) "を再生待ちに追加しました。" else "を" + pos + "番目の再生待ちに追加しました。")
            )
            m.editOriginal(addMsg).queue()

            //log.info(event.getGuild().getName() + track.getInfo().title
            //        + "(" + FormatUtil.formatTime(track.getDuration()) + ") " + (pos == 0 ? "を再生待ちに追加しました。" : "を" + pos + "番目の再生待ちに追加しました。"));
        }

        override fun trackLoaded(track: AudioTrack) {
            loadSingle(track)
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            val single =
                if (playlist.tracks.size == 1 || playlist.isSearchResult) if (playlist.selectedTrack == null) playlist.tracks[0] else playlist.selectedTrack
                else if (playlist.selectedTrack != null) playlist.selectedTrack
                else playlist.tracks[0]
            loadSingle(single)
        }

        override fun noMatches() {
            if (ytsearch) m.editOriginal(FormatUtil.filter(event.client.warning + " この検索結果はありません `" + event.user + "`."))
                .queue()
            else bot!!.playerManager.loadItemOrdered(
                event.guild,
                "ytsearch:" + event.user,
                SlashResultHandler(m, event, true)
            )
        }

        override fun loadFailed(throwable: FriendlyException) {
            if (throwable.severity == FriendlyException.Severity.COMMON) m.editOriginal(event.client.error + " 読み込みエラー: " + throwable.message)
                .queue()
            else m.editOriginal(event.client.error + " 曲の読み込み中にエラーが発生しました。").queue()
            log.info(event.guild!!.name + "で読み込みエラーが発生しました。")
        }
    }


    private inner class ResultHandler(
        private val m: Message,
        private val event: CommandEvent,
        private val ytsearch: Boolean
    ) : AudioLoadResultHandler {
        private fun loadSingle(track: AudioTrack) {
            if (bot!!.config.isTooLong(track)) {
                m.editMessage(
                    FormatUtil.filter(
                        event.client.warning + "(**" + (if (track.info.uri.contains("https://stream.gensokyoradio.net/")) "幻想郷ラジオ" else track.info.title) + "**) このトラックは許可されている最大長よりも長いです: `"
                                + FormatUtil.formatTime(track.duration) + "` > `" + FormatUtil.formatTime(bot.config.maxSeconds * 1000) + "`"
                    )
                ).queue()
                return
            }
            val handler = event.guild.audioManager.sendingHandler as AudioHandler?
            val pos = handler!!.addTrackToFront(QueuedTrack(track, event.author)) + 1
            val addMsg = FormatUtil.filter(
                event.client.success + "**" + (if (track.info.uri.contains("https://stream.gensokyoradio.net/")) "幻想郷ラジオ" else track.info.title)
                        + "** (`" + FormatUtil.formatTime(track.duration) + "`) " + (if (pos == 0) "を再生待ちに追加しました。" else "を" + pos + "番目の再生待ちに追加しました。")
            )
            m.editMessage(addMsg).queue()

            //log.info(event.getGuild().getName() + track.getInfo().title
            //        + "(" + FormatUtil.formatTime(track.getDuration()) + ") " + (pos == 0 ? "を再生待ちに追加しました。" : "を" + pos + "番目の再生待ちに追加しました。"));
        }

        override fun trackLoaded(track: AudioTrack) {
            loadSingle(track)
        }

        override fun playlistLoaded(playlist: AudioPlaylist) {
            val single =
                if (playlist.tracks.size == 1 || playlist.isSearchResult) if (playlist.selectedTrack == null) playlist.tracks[0] else playlist.selectedTrack
                else if (playlist.selectedTrack != null) playlist.selectedTrack
                else playlist.tracks[0]
            loadSingle(single)
        }

        override fun noMatches() {
            if (ytsearch) m.editMessage(FormatUtil.filter(event.client.warning + " この検索結果はありません `" + event.args + "`."))
                .queue()
            else bot!!.playerManager.loadItemOrdered(event.guild, "ytsearch:" + event.args, ResultHandler(m, event, true))
        }

        override fun loadFailed(throwable: FriendlyException) {
            if (throwable.severity == FriendlyException.Severity.COMMON) m.editMessage(event.client.error + " 読み込みエラー: " + throwable.message)
                .queue()
            else m.editMessage(event.client.error + " 曲の読み込み中にエラーが発生しました。").queue()
            log.info(event.guild.name + "で読み込みエラーが発生しました。")
        }
    }
}
