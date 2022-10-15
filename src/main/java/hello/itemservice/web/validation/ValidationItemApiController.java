package hello.itemservice.web.validation;


import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController // Rest API
@RequestMapping("/validation/api/item")
public class ValidationItemApiController {
    // REST API에서는 애초에 리퀘스트 바디의 필드가 타입이 안 맞으면 예외가 뜸
    // 로직이 성공하거나 검증이 오류가 발생하는데 여기서는 JSON형식으로 던져주기에 좀 복잡하다
    // 자세한 사항은 포스트맨으로 확인해서 JSON객체를 뜯어봐야한다.
    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult){
        log.info("API 컨트롤러 호출");

        if(bindingResult.hasErrors()){
            log.info("검증 오류 발생 errors={}",bindingResult);
            return bindingResult.getAllErrors();
        }
        log.info("성공 로직 실행");
        return form;
    }
}
