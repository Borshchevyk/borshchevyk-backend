package ru.kubsu.borshchevyk.sync.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kubsu.borshchevyk.sync.domain.model.SyncEvent;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PullResult {
    private List<SyncEvent> events;
    private String nextToken;
    private boolean hasMore;
}
