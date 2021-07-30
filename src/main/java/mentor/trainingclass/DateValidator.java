package mentor.trainingclass;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDates, StartEndDates> {

    @Override
    public boolean isValid(StartEndDates dates, ConstraintValidatorContext context) {
        return dates.getEndDate().isAfter(dates.getStartDate());
    }
}
