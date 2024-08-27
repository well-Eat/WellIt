package com.study.project.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/shop")
@Controller
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/")
    @ResponseBody
    public String getShopPopular() {
        return "shop_popular";
    }


    @GetMapping("/list")
    public String getShopList(Model model) {

        List<ProductDTO> prodList = shopService.getProdCateList();

        model.addAttribute("prodlist", prodList);

        return "shop/shop_list";
    }

    @GetMapping("/detail/{productId}")
    @ResponseBody
    public String getShopDetail() {
        return "shop_detail";
    }


}
