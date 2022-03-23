package io.marketplace.services.contact.adapters.dto;

import io.marketplace.services.contact.model.WalletDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WalletListResponse {
    private List<WalletDto> data;
}
