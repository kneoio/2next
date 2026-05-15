package com.semantyca.mixpla.model.filter;

import com.semantyca.mixpla.model.cnst.PlaylistItemType;
import com.semantyca.mixpla.model.cnst.SourceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class SoundFragmentFilter implements IFilter {
    private boolean activated = false;
    private List<UUID> genre;
    private List<UUID> labels;
    private List<SourceType> source;
    private List<PlaylistItemType> type;
    private String searchTerm;
    private int author;
    private List<UUID> brands;
    private boolean shared;

    public boolean isActivated() {
        if (activated) {
            return true;
        }
        return hasAnyFilter();
    }

    public boolean hasAnyFilter() {
        if (genre != null && !genre.isEmpty()) {
            return true;
        }
        if (labels != null && !labels.isEmpty()) {
            return true;
        }
        if (source != null && !source.isEmpty()) {
            return true;
        }
        if (type != null && !type.isEmpty()) {
            return true;
        }
        if (brands != null && !brands.isEmpty()) {
            return true;
        }
        if (author != 0) {
            return true;
        }
        if (shared) {
            return true;
        }
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }
}