package io.microvibe.booster.commons.validator;

import com.baidu.unbiz.fluentvalidator.*;
import com.baidu.unbiz.fluentvalidator.annotation.FluentValidate;
import com.baidu.unbiz.fluentvalidator.jsr303.HibernateSupportedValidator;
import io.microvibe.booster.commons.spring.MessageResources;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.Payload;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

/**
 * @author Qt
 * @since May 28, 2018
 */
@Ignore
public class ValidatorTest {

	@Test
	public void test000(){
		System.out.println(MessageResources.getMessage("test"));
		System.out.println(MessageResources.getMessage("javax.validation.constraints.Null.message"));
	}
	@Test
	public void test001() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		javax.validation.Validator validator = factory.getValidator();

		Car car = getCar();
		GenericResult result = FluentValidator.checkAll()
//			.on(car.getLicensePlate(), new ValidatorHandler<String>(){
//				@Override
//				public boolean validate(ValidatorContext context, String s) {
//					if(s != null){
//						context.addError(ValidationError.create("必须是空"));
//						return false;
//					}
//					return true;
//				}
//			})
			.on(car, new HibernateSupportedValidator<Car>().setHiberanteValidator(validator))
			.doValidate()
//			.result(ResultCollectors.toSimple());
			.result(ResultCollectors.toComplex());
		System.out.println(result);
	}

	private Car getCar() {
		Car car = new Car();
		car.setManufacturer("test");
		car.setLicensePlate("12345");
		return car;
	}

	@Data
	static class Car {
		@NotNull
		private String manufacturer;

		@FluentValidate
		private int seatCount;
		@Length(min = 5, max = 10)
		private String licensePlate;

	}
}
