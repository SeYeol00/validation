package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        // item == clazz
        // item = subItem
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        if(!StringUtils.hasText(item.getItemName())){// 에러 메세지를 코드화 시킨 것
            // 위 코드들을 아주 단축화 시킨 것 호우
            errors.rejectValue("itemName","required");
        }

        if(item.getPrice()==null||item.getPrice()<1000|| item.getPrice()>1000000){//                                                                 조건 파라미터, 에러 프로퍼티에서 파라미터가 두 개라
            errors.rejectValue("price","range",new Object[]{1000,10000000},null);
        }
        if(item.getQuantity()==null||item.getQuantity()>=9999){//                                                                                       error.properties에서 요구하는 파라미터
            errors.rejectValue("quantity","max", new Object[]{9999},null);
        }
        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice()!=null && item.getQuantity()!= null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice<10000){// 이건 새로운 에러를 지정할 때, 즉 필드 값이 없을 때
                // 글로벌 에러 처리 간단화
                errors.reject("totalPriceMin",new Object[]{1000,resultPrice},null);

            }
        }

    }
}
