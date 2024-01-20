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

package dev.cosgy.jmusicbot.slashcommands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import dev.cosgy.jmusicbot.playlist.MylistLoader;
import dev.cosgy.jmusicbot.slashcommands.DJCommand;
import dev.cosgy.jmusicbot.slashcommands.MusicCommand;
import dev.cosgy.jmusicbot.util.StackTraceUtil;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kosugikun
 */
public class MylistCmd extends MusicCommand {

    public MylistCmd(Bot bot) {
        super(bot);
        this.guildOnly = false;
        this.name = "mylist";
        this.arguments = "<append|delete|make|all>";
        this.help = "自分専用の再生リストを管理";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new MusicCommand[]{
                new MakelistCmd(bot),
                new DeletelistCmd(bot),
                new AppendlistCmd(bot),
                new ListCmd(bot)
        };
    }

    @Override
    public void doCommand(CommandEvent event) {

        StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " マイリスト管理コマンド:\n");
        for (Command cmd : this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments() == null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }

    @Override
    public void doCommand(SlashCommandEvent slashCommandEvent) {
    }

    public static class MakelistCmd extends DJCommand {
        public MakelistCmd(Bot bot) {
            super(bot);
            this.name = "make";
            this.aliases = new String[]{"create"};
            this.help = "再生リストを新規作成";
            this.arguments = "<name>";
            this.guildOnly = true;
            this.ownerCommand = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "プレイリスト名", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String pName = event.getArgs().replaceAll("\\s+", "_");
            String userId = event.getAuthor().getId();

            if (pName.isEmpty()) {
                event.replyError("プレイリスト名を指定してください。");
                return;
            }

            if (bot.getMylistLoader().getPlaylist(userId, pName) == null) {
                try {
                    bot.getMylistLoader().createPlaylist(userId, pName);
                    event.reply(event.getClient().getSuccess() + "マイリスト `" + pName + "` を作成しました");
                } catch (IOException e) {
                    if (event.isOwner() || event.getMember().isOwner()) {
                        event.replyError("曲の読み込み中にエラーが発生しました。\n" +
                                "**エラーの内容: " + e.getLocalizedMessage() + "**");
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " マイリストを作成できませんでした。:" + e.getLocalizedMessage());
                }
            } else {
                event.reply(event.getClient().getError() + " マイリスト `" + pName + "` は既に存在します");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String pName = event.getOption("name").getAsString().replaceAll("\\s+", "_");
            String userId = event.getUser().getId();

            if (pName.isEmpty()) {
                event.reply(event.getClient().getError() + "プレイリスト名を指定してください。").queue();
                return;
            }

            if (bot.getMylistLoader().getPlaylist(userId, pName) == null) {
                try {
                    bot.getMylistLoader().createPlaylist(userId, pName);
                    event.reply(event.getClient().getSuccess() + "マイリスト `" + pName + "` を作成しました").queue();
                } catch (IOException e) {
                    if (event.getClient().getOwnerId() == event.getMember().getId() || event.getMember().isOwner()) {
                        event.reply(event.getClient().getError() + "曲の読み込み中にエラーが発生しました。\n" +
                                "**エラーの内容: " + e.getLocalizedMessage() + "**").queue();
                        StackTraceUtil.sendStackTrace(event.getTextChannel(), e);
                        return;
                    }

                    event.reply(event.getClient().getError() + " マイリストを作成できませんでした。:" + e.getLocalizedMessage()).queue();
                }
            } else {
                event.reply(event.getClient().getError() + " マイリスト `" + pName + "` は既に存在します").queue();
            }
        }
    }

    public static class DeletelistCmd extends MusicCommand {
        public DeletelistCmd(Bot bot) {
            super(bot);
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "既存のマイリストを削除";
            this.arguments = "<name>";
            this.guildOnly = true;
            this.ownerCommand = false;

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "プレイリスト名", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String pName = event.getArgs().replaceAll("\\s+", "_");
            String userId = event.getAuthor().getId();
            if (!pName.equals("")) {
                if (bot.getMylistLoader().getPlaylist(userId, pName) == null)
                    event.reply(event.getClient().getError() + " マイリストは存在しません:`" + pName + "`");
                else {
                    try {
                        bot.getMylistLoader().deletePlaylist(userId, pName);
                        event.reply(event.getClient().getSuccess() + " マイリストを削除しました:`" + pName + "`");
                    } catch (IOException e) {
                        event.reply(event.getClient().getError() + " マイリストを削除できませんでした: " + e.getLocalizedMessage());
                    }
                }
            } else {
                event.reply(event.getClient().getError() + "マイリストの名前を含めてください");
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String pName = event.getOption("name").getAsString().replaceAll("\\s+", "_");
            String userId = event.getUser().getId();

            if (bot.getMylistLoader().getPlaylist(userId, pName) == null)
                event.reply(event.getClient().getError() + " マイリストは存在しません:`" + pName + "`").queue();
            else {
                try {
                    bot.getMylistLoader().deletePlaylist(userId, pName);
                    event.reply(event.getClient().getSuccess() + " マイリストを削除しました:`" + pName + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " マイリストを削除できませんでした: " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public static class AppendlistCmd extends MusicCommand {
        public AppendlistCmd(Bot bot) {
            super(bot);
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "既存のマイリストに曲を追加";
            this.arguments = "<name> <URL> | <URL> | ...";
            this.guildOnly = true;
            this.ownerCommand = false;
            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "プレイリスト名", true));
            options.add(new OptionData(OptionType.STRING, "url", "URL", true));
            this.options = options;
        }

        @Override
        public void doCommand(CommandEvent event) {

            String[] parts = event.getArgs().split("\\s+", 2);
            String userId = event.getAuthor().getId();
            if (parts.length < 2) {
                event.reply(event.getClient().getError() + " 追加先のマイリスト名とURLを含めてください。");
                return;
            }
            String pName = parts[0];
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, pName);
            if (playlist == null)
                event.reply(event.getClient().getError() + " マイリストは存在しません:`" + pName + "`");
            else {
                StringBuilder builder = new StringBuilder();
                playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                String[] urls = parts[1].split("\\|");
                for (String url : urls) {
                    String u = url.trim();
                    if (u.startsWith("<") && u.endsWith(">"))
                        u = u.substring(1, u.length() - 1);
                    builder.append("\r\n").append(u);
                }
                try {
                    bot.getMylistLoader().writePlaylist(userId, pName, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " 項目をマイリストに追加しました:`" + pName + "`");
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " マイリストに追加できませんでした: " + e.getLocalizedMessage());
                }
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String userId = event.getUser().getId();
            String pname = event.getOption("name").getAsString();
            MylistLoader.Playlist playlist = bot.getMylistLoader().getPlaylist(userId, pname);
            if (playlist == null)
                event.reply(event.getClient().getError() + " マイリストは存在しません:`" + pname + "`").queue();
            else {
                StringBuilder builder = new StringBuilder();
                playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                String[] urls = event.getOption("url").getAsString().split("\\|");
                for (String url : urls) {
                    String u = url.trim();
                    if (u.startsWith("<") && u.endsWith(">"))
                        u = u.substring(1, u.length() - 1);
                    builder.append("\r\n").append(u);
                }
                try {
                    bot.getMylistLoader().writePlaylist(userId, pname, builder.toString());
                    event.reply(event.getClient().getSuccess() + urls.length + " 項目をマイリストに追加しました:`" + pname + "`").queue();
                } catch (IOException e) {
                    event.reply(event.getClient().getError() + " マイリストに追加できませんでした: " + e.getLocalizedMessage()).queue();
                }
            }
        }
    }

    public static class ListCmd extends MusicCommand {
        public ListCmd(Bot bot) {
            super(bot);
            this.name = "all";
            this.aliases = new String[]{"available", "list"};
            this.help = "利用可能なすべてのマイリストを表示";
            this.guildOnly = true;
            this.ownerCommand = false;
        }

        @Override
        public void doCommand(CommandEvent event) {
            String userId = event.getAuthor().getId();

            if (!bot.getMylistLoader().folderUserExists(userId))
                bot.getMylistLoader().createUserFolder(userId);
            if (!bot.getMylistLoader().folderUserExists(userId)) {
                event.reply(event.getClient().getWarning() + " マイリストフォルダが存在しないため作成できませんでした。");
                return;
            }
            List<String> list = bot.getMylistLoader().getPlaylistNames(userId);
            if (list == null)
                event.reply(event.getClient().getError() + " 利用可能なマイリストを読み込めませんでした。");
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " マイリストフォルダに再生リストがありません。");
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " 利用可能なマイリスト:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }

        @Override
        public void doCommand(SlashCommandEvent event) {
            String userId = event.getUser().getId();

            if (!bot.getMylistLoader().folderUserExists(userId))
                bot.getMylistLoader().createUserFolder(userId);
            if (!bot.getMylistLoader().folderUserExists(userId)) {
                event.reply(event.getClient().getWarning() + " マイリストフォルダが存在しないため作成できませんでした。").queue();
                return;
            }
            List<String> list = bot.getMylistLoader().getPlaylistNames(userId);
            if (list == null)
                event.reply(event.getClient().getError() + " 利用可能なマイリストを読み込めませんでした。").queue();
            else if (list.isEmpty())
                event.reply(event.getClient().getWarning() + " マイリストフォルダに再生リストがありません。").queue();
            else {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess() + " 利用可能なマイリスト:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString()).queue();
            }
        }
    }
}
