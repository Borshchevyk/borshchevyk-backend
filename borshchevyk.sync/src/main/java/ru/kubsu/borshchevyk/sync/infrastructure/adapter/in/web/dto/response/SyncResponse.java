package ru.kubsu.borshchevyk.sync.infrastructure.adapter.in.web.dto.response;

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
public class SyncResponse {
    private List<SyncEvent> events;
    private String nextToken;
    private boolean hasMore;
}