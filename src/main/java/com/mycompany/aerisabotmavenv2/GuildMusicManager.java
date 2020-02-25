/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aerisabotmavenv2;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {

    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;
    /**
     * Current playlist for the player.
     */
    public String currentPlaylist;
    /**
     * RadioMode for the player.
     */
    private static boolean radioMode;

    /**
     * Creates a player and a track scheduler.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        radioMode = false;
    }

    public String getCurrentPlaylist() {
        return currentPlaylist;
    }

    public static boolean isRadioMode() {
        return radioMode;
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public void setCurrentPlaylist(String currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public static void setRadioMode(boolean radioMode) {
        GuildMusicManager.radioMode = radioMode;
    }
    
    public boolean getRadioMode() {
        return radioMode;
    }

    public void toggleRadioMode(TextChannel channel, boolean selection) {
        if (this.radioMode != selection) {
            this.radioMode = selection;
            if (selection) {
                channel.sendMessage("Radio now set to **ON**").queue();
            } else {
                channel.sendMessage("Radio now set to **OFF**").queue();
            }
        }
    }
}
