package io.marketplace.services.contact.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.marketplace.commons.model.PagingInformation;
import io.marketplace.commons.model.dto.ListResponseDto;

@Service
public class SpringTemplateService {
    private final Integer MAX_PAGE_SIZE = 10;

    public ListResponseDto<String> getListOfHelloWorldText(final String name, final Integer pageNumber,
            final Integer pageSize) {
        List<String> listResult = new ArrayList<>();
        for (int i = 0; i < MAX_PAGE_SIZE; i++) {
            listResult.add("Hello " + (i + 1));
        }
        return ListResponseDto.<String>builder().data(listResult).paging(
                PagingInformation.builder().pageNumber(1).pageSize(MAX_PAGE_SIZE).totalRecords(MAX_PAGE_SIZE).build())
                .build();
    }
}
