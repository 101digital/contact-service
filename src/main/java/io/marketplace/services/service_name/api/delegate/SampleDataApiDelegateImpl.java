package io.marketplace.services.service_name.api.delegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import io.marketplace.commons.model.dto.ListResponseDto;
import io.marketplace.services.service_name.api.SampleDataApiDelegate;
import io.marketplace.services.service_name.model.ResponseStatus;
import io.marketplace.services.service_name.model.ServiceDataResponse;
import io.marketplace.services.service_name.service.SpringTemplateService;

public class SampleDataApiDelegateImpl implements SampleDataApiDelegate {

	@Autowired
	private SpringTemplateService springTemplateService;

	private ResponseEntity<ServiceDataResponse> asBody(ListResponseDto<String> listDto) {
		ServiceDataResponse response = new ServiceDataResponse();
		response.data(listDto.getData());

		ResponseStatus status = new ResponseStatus();
		status.code("000000");
		status.message("Success");
		response.setStatus(status);
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<ServiceDataResponse> getExampleData() {
		return asBody(springTemplateService.getListOfHelloWorldText("", 0, 0));
	}

}
