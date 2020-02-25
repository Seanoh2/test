/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aerisabotmavenv2;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 *
 * @author Sean
 */
public class AudioCommands {

    private final AudioPlayerManager playerManager;
    private static TrackHandler trackHandler;
    private GuildManager guilds;
    private Track pickedTrack;

    private AudioCommands() {
        this.playerManager = new DefaultAudioPlayerManager();
        this.trackHandler = TrackHandler.getInstance();
        this.guilds = GuildManager.getInstance();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void playAerisa(final TextChannel channel, Member user) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        AudioLoadResultHandler audioHandler = new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                play(channel.getGuild(), musicManager, track, user);
                channel.sendMessage("Playing: ").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack, user);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + pickedTrack).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        };

    }

    public void loadAndPlay(final TextChannel channel, final String trackURL, Member user) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                play(channel.getGuild(), musicManager, track, user);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack, user);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackURL).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    public void radioMode(TextChannel channel) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        musicManager.toggleRadioMode(channel, true);
    }

    public void cycleTrack(TextChannel channel) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        pickedTrack = trackHandler.newTrack(musicManager.currentPlaylist); 
    }

    public void pause(TextChannel channel, boolean state) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        musicManager.player.setPaused(state);
    }

    public void skip(TextChannel channel) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

    public void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Member user) {
        connectToVoiceChannel(guild.getAudioManager(), user);
        musicManager.scheduler.queue(track);
    }

    public void stop(TextChannel channel) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        musicManager.player.stopTrack();
    }

    public void search(TextChannel channel, String query) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        trackHandler.searchByString(channel, query, musicManager.getCurrentPlaylist());
    }

    public void playByID(TextChannel channel, int ID, Member user) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        loadAndPlay(channel, trackHandler.getTrackByID(ID, musicManager.getCurrentPlaylist()).getLocation(), user);
    }

    public void viewQueue(TextChannel channel) {
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(channel.getGuild());
        channel.sendMessage(musicManager.scheduler.getCurrentQueue(channel)).queue();
    }

    public void kickBot(Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        GuildMusicManager musicManager = guilds.getGuildAudioPlayer(guild);

        if (audioManager.isConnected() || audioManager.isAttemptingToConnect()) {
            musicManager.player.stopTrack();
            audioManager.closeAudioConnection();
        }

    }

    public void helpCommands(TextChannel channel) {
        String message;
        message = "__**Commands avaiable in the Bot:**__\n\n"
                + "**-radio**\t\tStarts radio (Default: VIP)\n\n"
                + "**-radio [playlist]**\t\tplays playlist of choice.\n\n"
                + "__**Radio playlists:**__\t*vip*\t*mellow*\t*source*\t*exiled*\t*wap*\t*cpp*\n\n"
                + "**-skip**\t\tStarts next track, will add new random track if nothing in queue and radio toggled\n\n"
                + "**-pause**\t\tPauses current track\n\n"
                + "**-play**\t\tPlays track if supplied, else unpauses player\n\n"
                + "**-unpause**\t\tUnpauses player\n\n"
                + "**-stop**\t\t\tStops current track and unloads track\n\n"
                + "**-stopRadio**\t\tStops radio, so new track wont auto-load\n\n"
                + "**-search**\t\tSearches library for string, returning list with ID, multi Params allowed (EG ff,skyrim)\n\n"
                + "**-playID**\t\tPlays song in index, mainly found by search\n\n"
                + "**-queue**\t\tDisplays current queue\n\n"
                + "**-kick**\t\tKicks bot from voice channel.";
        channel.sendMessage(message).queue();
    }

    private static void connectToVoiceChannel(AudioManager audioManager, Member user) {
        if (user == null) {
            if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
                for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                    audioManager.openAudioConnection(voiceChannel);
                    break;
                }
            }
        } else {
            if (!audioManager.isAttemptingToConnect() && !user.getVoiceState().isGuildDeafened()) {
                audioManager.openAudioConnection(user.getVoiceState().getChannel());
            }
        }
    }
}
