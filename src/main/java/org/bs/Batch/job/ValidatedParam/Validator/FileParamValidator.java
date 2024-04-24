package org.bs.Batch.job.ValidatedParam.Validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.util.StringUtils;

import java.util.List;

public class FileParamValidator implements JobParametersValidator {

    // 단일 파일 검증
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {

        assert parameters != null;
        String fileName = parameters.getString("fileName");

        if(!StringUtils.endsWithIgnoreCase(fileName, "csv")){
            throw new JobParametersInvalidException("not csv file");
        }
    }

    // 리스트로 다수의 파일 검증
    public CompositeJobParametersValidator MultiValidator(){

        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(List.of(new FileParamValidator()));

        return validator;
    }
}
