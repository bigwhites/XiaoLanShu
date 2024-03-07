package com.ricky.blogserver.config;

import com.ricky.apicommon.XiaoLanShuException;
import com.ricky.apicommon.utils.result.R;
import com.ricky.apicommon.utils.result.ResultFactory;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Objects;

/**
 * <p>
 * 全局异常处理的基础类 必须实现此类
 * </p>
 *
 * @author liang
 * @date 2021/5/28 17:46
 */
@RestControllerAdvice
public class HandleExceptionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理实现此类的所有程序抛出的参数绑定异常
     *
     * @param e 参数绑定异常
     * @return 封装好的统一返回结果
     */
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseBody
    public R<Object> methodArgumentNotValidException(WebExchangeBindException e) {
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        logger.debug(message);
        return ResultFactory.fail(message);
    }

    @ExceptionHandler(XiaoLanShuException.class)
    @ResponseBody
    public R<Object> methodArgumentNotValidException(XiaoLanShuException e) {
//        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResultFactory.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R<Object> exp(Exception e) {
        e.printStackTrace();

//        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResultFactory.fail("未知错误");
    }

}
