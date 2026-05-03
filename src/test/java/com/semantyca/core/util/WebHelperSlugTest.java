package com.semantyca.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebHelperSlugTest {

    @Test
    void generateSlug_latinLigaturesAndOldEnglish_icuAsciiSlug() {
        assertEquals("hwaet", WebHelper.generateSlug("hwæt"));
        assertEquals("thing-hwaet", WebHelper.generateSlug("þing", "hwæt"));
        assertEquals("strasse", WebHelper.generateSlug("Straße"));
    }

    @Test
    void generateSlug_cyrillic_icuTransliteration() {
        assertEquals("viter-nadii", WebHelper.generateSlug("Вітер надії"));
        assertEquals("nadii", WebHelper.generateSlug("надії"));
    }

    @Test
    void generateSlug_punctuationAndAccents_collapsedToHyphens() {
        assertEquals("end-of-the-day-feat-ade", WebHelper.generateSlug("End of the Day (feat. & Adé)"));
    }

    @Test
    void generateSlug_knownFileExtension_preserved() {
        assertEquals("track.mp3", WebHelper.generateSlug("track.mp3"));
        assertEquals("my-song.flac", WebHelper.generateSlug("My Song.flac"));
    }

    @Test
    void generateSlug_dotInTitle_notTreatedAsExtension() {
        assertEquals("end-of-the-day-feat-ade", WebHelper.generateSlug("End of the Day (feat. Adé)"));
    }

    @Test
    void generateSlugPath_joinsSegments() {
        assertEquals("artist/hwaet", WebHelper.generateSlugPath("Artist", "hwæt"));
        assertEquals("a/b", WebHelper.generateSlugPath("A", "B"));
    }

    @Test
    void generatePersonSlug_stripsDomainAndSlugifiesLocalPart() {
        assertEquals("john-doe", WebHelper.generatePersonSlug("John.Doe@example.com"));
    }

    @Test
    void generateSlug_nullOrBlank_empty() {
        assertEquals("", WebHelper.generateSlug((String) null));
        assertEquals("", WebHelper.generateSlug(""));
        assertEquals("", WebHelper.generateSlug("   "));
    }
}
