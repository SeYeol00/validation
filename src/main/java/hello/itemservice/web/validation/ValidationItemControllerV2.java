package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    //@PostMapping("/add")//                          이게 핵심, 이걸로 에러 객체 처리
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            //                                  모델 어트리뷰트에 담기는 이름
            bindingResult.addError(new FieldError("item","itemName","상품 이름은 필수입니다."));
        }
        if(item.getPrice()==null||item.getPrice()<1000|| item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item","price","가격은 천원에서 백만원까지 허용합니다."));
        }
        if(item.getQuantity()==null||item.getQuantity()>=9999){
            bindingResult.addError(new FieldError("item","quantity","수량은 최대 9,999까지 허용합니다."));
        }
        // 특정 필드가 아닌 복함 룰 검증
        if(item.getPrice()!=null && item.getQuantity()!= null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice<10000){// 이건 새로운 에러를 지정할 때, 즉 필드 값이 없을 때
                bindingResult.addError(new ObjectError("item","가격 * 수량의 합은 10,000원 이상이어야합니다. 현재 값: "+resultPrice));

            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        // bindingResult의 작동 방식이 중요하다.
        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            // 바인딩 리절트는 자동으로 모델에 넘어가므로 안 담아도 된다.
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")//                          이게 핵심, 이걸로 에러 객체 처리
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            //                                  모델 어트리뷰트에 담기는 이름         rejectedValue 넣으면 값 유지
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,null,null,"상품 이름은 필수입니다."));

        }
        if(item.getPrice()==null||item.getPrice()<1000|| item.getPrice()>1000000){
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,null,null,"가격은 천원에서 백만원까지 허용합니다."));

        }
        if(item.getQuantity()==null||item.getQuantity()>=9999){
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"수량은 최대 9,999까지 허용합니다."));
        }
        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice()!=null && item.getQuantity()!= null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice<10000){// 이건 새로운 에러를 지정할 때, 즉 필드 값이 없을 때
                bindingResult.addError(new ObjectError("item",null,null,"가격 * 수량의 합은 10,000원 이상이어야합니다. 현재 값: "+resultPrice));

            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        // bindingResult의 작동 방식이 중요하다.
        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            // 바인딩 리절트는 자동으로 모델에 넘어가므로 안 담아도 된다.
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")//                          이게 핵심, 이걸로 에러 객체 처리
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){// 에러 메세지를 코드화 시킨 것
            //                                  모델 어트리뷰트에 담기는 이름         rejectedValue 넣으면 값 유지             여기서 메세지를 에러 프로퍼티즈에서 가져와서 디폴트 메세지 필요없음
            bindingResult.addError(new FieldError("item","itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null,null));

        }
        if(item.getPrice()==null||item.getPrice()<1000|| item.getPrice()>1000000){//                                                                 조건 파라미터, 에러 프로퍼티에서 파라미터가 두 개라
            bindingResult.addError(new FieldError("item","price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000.1000000},null));

        }
        if(item.getQuantity()==null||item.getQuantity()>=9999){//                                                                                       error.properties에서 요구하는 파라미터
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999},null));
        }
        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice()!=null && item.getQuantity()!= null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice<10000){// 이건 새로운 에러를 지정할 때, 즉 필드 값이 없을 때
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000, resultPrice},null));

            }
        }
        // 검증에 실패하면 다시 입력 폼으로
        // bindingResult의 작동 방식이 중요하다.
        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            // 바인딩 리절트는 자동으로 모델에 넘어가므로 안 담아도 된다.
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

