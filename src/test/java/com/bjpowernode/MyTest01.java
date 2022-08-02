package com.bjpowernode;

import com.bjpowernode.pojo.ProductType;
import com.bjpowernode.service.ProductTypeService;
import com.bjpowernode.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

public class MyTest01 {
    @Test
    public void test01(){
        String mi = MD5Util.getMD5("000000");
        System.out.println(mi);
    }

    @Test
    public void test02(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext_*.xml");
        ProductTypeService productTypeService = (ProductTypeService) context.getBean("ProductTypeServiceImpl");
        List<ProductType> typeList = productTypeService.getAll();
        for (ProductType productType : typeList) {
            System.out.println(productType);
        }

    }

}
