package com.ae86.mypractice01.controller;

import com.ae86.mypractice01.mbg.model.PmsBrand;
import com.ae86.mypractice01.service.PmsBrandService;
//import com.ae86.mypracticeaop.annotation.MyLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
@Controller
@RequestMapping("/brand")
public class PmsBrandController {
    @Autowired
    private PmsBrandService PmsBrandService;
    public static final Logger LOGGER = LoggerFactory.getLogger(PmsBrandController.class);
    @RequestMapping(value = "listAll", method = RequestMethod.GET)
    @ResponseBody
//    @MyLog()
    public List<PmsBrand> getBrandList(){
        return PmsBrandService.listAllBrand();
    }
}
