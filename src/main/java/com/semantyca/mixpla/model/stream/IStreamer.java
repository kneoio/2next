package com.semantyca.mixpla.model.stream;


public interface IStreamer {

    void initialize();

    void shutdown();

    IPlaylistManager getPlaylistManager();


}