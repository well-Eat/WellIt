package com.wellit.project.shop;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/shop")
@Controller
@Log4j2
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


    @GetMapping("/detail/{prodId}")
    public String getShopDetail(Model model, @PathVariable("prodId") Integer prodId) {

        ProductDTO productDTO = shopService.getOneProd(prodId);
        model.addAttribute("productDTO", productDTO);


        return "shop/shop_detail";
    }


}
