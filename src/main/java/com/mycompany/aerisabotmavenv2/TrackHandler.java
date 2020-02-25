/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aerisabotmavenv2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.dv8tion.jda.core.entities.TextChannel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sean
 */
public class TrackHandler {

    private static final Random randomLine = new Random();
    private static TrackHandler instance = new TrackHandler();
    private static Map<String, ArrayList<Track>> playlists = new HashMap<>();

    private TrackHandler() {
        setPlaylists();
    }

    public static TrackHandler getInstance() {
        return instance;
    }

    private static Document parseXML(URL inputfile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(inputfile.openStream());
        doc.getDocumentElement().normalize();
        return doc;
    }

    public static void readXML(URL inputfile, String playlist) {
        Document document = null;
        try {
            document = parseXML(inputfile);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        if (document != null) {
            NodeList nodeList = document.getElementsByTagName("track");
            if (nodeList != null) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element trackDetails = (Element) node;
                        Element creator = (Element) trackDetails.getElementsByTagName("creator").item(0);
                        Element title = (Element) trackDetails.getElementsByTagName("title").item(0);
                        Element location = (Element) trackDetails.getElementsByTagName("location").item(0);

                        Track track = new Track(creator.getTextContent(), title.getTextContent(), location.getTextContent());
                        playlists.get(playlist).add(track);
                    }
                }
            }
        }
    }

    public static void setPlaylists() {
        try {

            if (playlists.get("vip") == null) {
                playlists.put("vip", new ArrayList<>());
                readXML(new URL("http://vip.aersia.net/roster.xml"), "vip");
            }

            if (playlists.get("mellow") == null) {
                playlists.put("mellow", new ArrayList<>());
                readXML(new URL("http://vip.aersia.net/roster-mellow.xml"), "mellow");
            }

            if (playlists.get("source") == null) {
                playlists.put("source", new ArrayList<>());
                readXML(new URL("http://vip.aersia.net/roster-source.xml"), "source");
            }

            if (playlists.get("exiled") == null) {
                playlists.put("exiled", new ArrayList<>());
                readXML(new URL("http://vip.aersia.net/roster-exiled.xml"), "exiled");
            }

            if (playlists.get("wap") == null) {
                playlists.put("wap", new ArrayList<>());
                readXML(new URL("http://wap.aersia.net/roster.xml"), "wap");
            }

            if (playlists.get("cpp") == null) {
                playlists.put("cpp", new ArrayList<>());
                readXML(new URL("http://cpp.aersia.net/roster.xml"), "cpp");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void searchByString(TextChannel channel, String query, String playlist) {
        StringBuilder result = new StringBuilder();
        int index;
        String[] command = query.split(",");
        for (String search : command) {
            index = 0;
            for (Track song : playlists.get(playlist)) {
                if (search.length() != 1) {
                    if (song.getTitle().toLowerCase().contains(search.toLowerCase()) || song.getCreator().toLowerCase().contains(search.toLowerCase())) {
                        if ((song.getLocation().length() + result.length()) > 1900) {
                            result.setLength(0);
                            result.trimToSize();
                        }
                        result.append("**ID: **").append(index).append("\t__").append(song.getTitle()).append("__\tFrom *").append(song.getCreator()).append("*\n\n");
                    }
                    index++;
                } else {
                    channel.sendMessage("Keyword **" + search + "** was too __vague__, Please retry keyword.").queue();
                    break;
                }
            }
        }
        if (result.length() != 0) {
            channel.sendMessage(result.toString()).queue();
        } else {
            channel.sendMessage("No results found for keyword(s) **" + Arrays.toString(command) + "**, please try again.").queue();
        }
    }

    public Track getTrackByID(int ID, String playlist) {
        Track track;
        track = playlists.get(playlist).get(ID);
        return track;
    }
    
    public boolean checkPlaylist(String playlist) {
        return playlists.get(playlist.toLowerCase()) != null;
    }
    
    public Track newTrack(String playlist) {
        return playlists.get(playlist.toLowerCase()).get(randomLine.nextInt());
    }

}
