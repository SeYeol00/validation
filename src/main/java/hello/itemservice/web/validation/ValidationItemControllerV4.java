package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }
    
    // 필드는 빈 벨리데이션, 글로벌은 비즈니스 로직(서비스)에서 처리하는 것을 권장
    //@PostMapping("/add")// 벨리데이트를 쓰면 Bean Validation이 그냥 동작한다.
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // 글로벌 오류는 빈 벨리데이션말고 비즈니스 로직 딴에서 처리하자
        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice()!=null && item.getQuantity()!= null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice<10000){
                // 글로벌 에러 처리 간단화
                bindingResult.reject("totalPriceMin",new Object[]{1000,resultPrice},null);
            }
        }
        // bindingResult에 validator가 알아서 에러를 담거나 안 담는다.
        // 검증에 실패하면 다시 입력 폼으로
        // bindingResult의 작동 방식이 중요하다.
        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            // 바인딩 리절트는 자동으로 모델에 넘어가므로 안 담아도 된다.
            return "validation/v4/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }
    @PostMapping("/add")// 벨리데이트를 쓰면 Bean Validation이 그냥 동작한다.
    //                      벨리데이티드에 인터페이스를 넣으면 이것만 벨리데이트함
    public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 글로벌 오류는 빈 벨리데이션말고 비즈니스 로직 딴에서 처리하자
        // 특정 필드가 아닌 복합 룰 검증
        if(form.getPrice()!=null && form.getQuantity()!= null){
            int resultPrice = form.getQuantity() * form.getPrice();
            if(resultPrice<10000){
                // 글로벌 에러 처리 간단화
                bindingResult.reject("totalPriceMin",new Object[]{1000,resultPrice},null);
            }
        }
        // bindingResult에 validator가 알아서 에러를 담거나 안 담는다.
        // 검증에 실패하면 다시 입력 폼으로
        // bindingResult의 작동 방식이 중요하다.
        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            // 바인딩 리절트는 자동으로 모델에 넘어가므로 안 담아도 된다.
            return "validation/v4/addForm";
        }

        // 성공 로직
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

   // @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute Item item, BindingResult bindingResult) {
        // 글로벌 오류는 빈 벨리데이션말고 비즈니스 로직 딴에서 처리하자
        // 특정 필드가 아닌 복합 룰 검증
        if(item.getPrice()!=null && item.getQuantity()!= null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice<10000){
                // 글로벌 에러 처리 간단화
                bindingResult.reject("totalPriceMin",new Object[]{1000,resultPrice},null);
            }
        }
        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            return "validation/v4/editForm";
        }
        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {
        // 글로벌 오류는 빈 벨리데이션말고 비즈니스 로직 딴에서 처리하자
        // 특정 필드가 아닌 복합 룰 검증
        if(form.getPrice()!=null && form.getQuantity()!= null){
            int resultPrice = form.getQuantity() * form.getPrice();
            if(resultPrice<10000){
                // 글로벌 에러 처리 간단화
                bindingResult.reject("totalPriceMin",new Object[]{1000,resultPrice},null);
            }
        }
        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            return "validation/v4/editForm";
        }
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        itemRepository.update(itemId, item);
        return "redirect:/validation/v4/items/{itemId}";
    }

}

