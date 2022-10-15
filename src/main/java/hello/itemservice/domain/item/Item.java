package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
// 글로벌 오류에 대한 벨리데이션, 권장 안 함 제약이 너무 많다.
//@ScriptAssert(lang="javascript",script ="_this.price * _this.quantity >= 10000",message = "총합이 10000원 넘게 입력해주세요.")
public class Item {

    //@NotNull(groups = UpdateCheck.class) // 수정 요구사항 추가
    private Long id;
    //필드에 validation을 걸 수 있다.
   // @NotBlank(groups = {SaveCheck.class,UpdateCheck.class},message = "공백은 불가합니다.")
    private String itemName;
    //@NotNull(groups = {SaveCheck.class,UpdateCheck.class},message = "값을 넣어주세요.")
    //@Range(groups = {SaveCheck.class,UpdateCheck.class},min = 1000, max = 1000000)
    private Integer price;
    //@NotNull(groups = {SaveCheck.class,UpdateCheck.class})
    //@Max(groups = SaveCheck.class,value = 9999) // 수정 요구사항 추가
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
