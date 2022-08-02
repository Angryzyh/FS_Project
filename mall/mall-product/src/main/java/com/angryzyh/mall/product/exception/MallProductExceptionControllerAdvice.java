package com.angryzyh.mall.product.exception;

import com.angryzyh.common.exception.BizCodeEnum;
import com.angryzyh.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.angryzyh.mall.product.controller")
public class MallProductExceptionControllerAdvice {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, UnexpectedTypeException.class})
    public R handleValidationException(MethodArgumentNotValidException exception) {
        // 异常打印到日志
        log.error("数据校验异常,异常信息:{},异常类型:{}", exception.getMessage(), exception.getClass());
        // 绑定具体异常发生的数据校验属性
        BindingResult bindingResult = exception.getBindingResult();
        /*map集合forEach获取*/
        /*Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });*/
        /*stream流获取*/
        Map<String, String> errorMap = bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.toConcurrentMap(FieldError::getField, FieldError::getDefaultMessage));
        return R.error(BizCodeEnum.VALIDATOR_EXCEPTION.getCode(), BizCodeEnum.VALIDATOR_EXCEPTION.getMsg()).put("data", errorMap);
    }

    /*全局异常处理*/
    @ExceptionHandler(Throwable.class)
    public R handleException(Throwable throwable) {
        log.error("异常类型信息:{},异常类型:{},异常原因:{}", throwable.getMessage(), throwable.getClass(), throwable.getCause());
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION.getCode(), BizCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
