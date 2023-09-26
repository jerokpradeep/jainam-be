package in.codifi.funds.model.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormDataModel {

	@RestForm("file")
	@Valid
	@NotNull
	private FileUpload file;

}
