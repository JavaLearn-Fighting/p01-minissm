package com.bjpowernode.controller;

import com.bjpowernode.pojo.ProductInfo;
import com.bjpowernode.pojo.vo.ProductInfoVo;
import com.bjpowernode.service.ProductInfoSercvice;
import com.bjpowernode.utils.FileNameUtil;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/prod")
public class ProductInfoAction {
    //设置每页展示的条数
    public static final int PAGE_SIZE = 5;
    //设置数据库中图片信息
    String saveFileName = "";

    //引入服务层的对象
    @Autowired
    ProductInfoSercvice productInfoSercvice;

    //显示所有数据
    @RequestMapping("/getAll")
    public String showAll(HttpServletRequest request){
        List<ProductInfo> list = productInfoSercvice.getAll();
        request.setAttribute("list",list);
        return "product";
    }

    //显示第1页的5条数据
    @RequestMapping("/split")
    public String split(HttpServletRequest request){
        PageInfo info = null;
        Object vo = request.getSession().getAttribute("prodVo");
        //获取第一页的数据
        if (vo != null){
            info = productInfoSercvice.splitPageVo((ProductInfoVo)vo,PAGE_SIZE);
        }else {
            info = productInfoSercvice.splitPage(1,PAGE_SIZE);
        }
        request.setAttribute("info",info);
        return "product";
    }

    //ajax分页翻页
    @ResponseBody//@ResponseBody将Java对象转为json对象
    @RequestMapping("/ajaxSplit")
    public void ajaxSplit(ProductInfoVo vo, HttpSession session){
        //获取翻页所需的商品数据
        PageInfo info = productInfoSercvice.splitPageVo(vo,PAGE_SIZE);
        session.setAttribute("info",info);
    }

    //多条件商品查询
    @ResponseBody
    @RequestMapping("/selectCondition")
    public void selectCondition(ProductInfoVo vo,HttpSession session){
        List<ProductInfo> list = productInfoSercvice.selectCondition(vo);
        session.setAttribute("list",list);
    }

    //ajax文件上传
    @ResponseBody
    @RequestMapping("/ajaxImg")
    public Object ajaxImg(MultipartFile pimage,HttpServletRequest request){
        //提取生成文件名UUID+文件后缀
        saveFileName = FileNameUtil.getUUIDFileName() + FileNameUtil.getFileType(pimage.getOriginalFilename());
        //得到项目中文件存储的位置
        String path = request.getServletContext().getRealPath("/image_big");
        //转存
        try {
            pimage.transferTo(new File(path + File.separator + saveFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回客户端json对象
        JSONObject object = new JSONObject();
        object.put("imgurl",saveFileName);

        return object.toString();
    }

    //添加商品
    @RequestMapping("/save")
    public String save(ProductInfo info,HttpServletRequest request){
        //设置商品图片信息和上架日期
        info.setpImage(saveFileName);
        info.setpDate(new Date());

        //执行添加商品服务函数
        int num = -1;
        try{
            num = productInfoSercvice.save(info);
        }catch (Exception e){
            e.printStackTrace();
        }
        //设置弹窗信息提示
        if (num > 0){
            request.setAttribute("msg","增加成功");
        }else {
            request.setAttribute("msg","增加失败");
        }
        saveFileName = "";
        return "forward:/prod/split.action";
    }

    //获取单个商品信息
    @RequestMapping("/one")
    public String one(int pid,Model model,ProductInfoVo vo,HttpSession session){
        ProductInfo info = productInfoSercvice.getById(pid);
        model.addAttribute("prod",info);
        session.setAttribute("prodVo",vo);
        return "update";
    }

    //更新操作
    @RequestMapping("/update")
    public String update(ProductInfo info,HttpServletRequest request){
        //判断是否上传了新图片
        if (!saveFileName.equals("")){
            info.setpImage(saveFileName);
        }
        //完成更新操作
        int num = -1;
        try {
            num = productInfoSercvice.update(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //弹窗提示
        if (num > 0){
            request.setAttribute("msg","更新成功！");
        }else {
            request.setAttribute("msg","更新失败！");
        }
        //清空saveFileName
        saveFileName = "";

        return "forward:/prod/split.action";
    }

    //删除商品
    @RequestMapping("/delete")
    public String delete(int pid,ProductInfoVo vo, HttpServletRequest request){
        int num = -1;
        try {
            num = productInfoSercvice.delete(pid);
            System.out.println(pid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (num > 0){
            request.setAttribute("msg","删除成功");
            request.setAttribute("deleteProdVo",vo);
        }else{
            request.setAttribute("msg","删除失败");
        }
        return "forward:/prod/deleteAjaxSplit.action";
    }


    @ResponseBody
    @RequestMapping(value = "/deleteAjaxSplit",produces = "text/html;charset=UTF-8")
    public Object deleteAjaxSplit(HttpServletRequest request){
        PageInfo info = null;
        Object vo = request.getSession().getAttribute("deleteProdVo");
        if (vo != null){
            info = productInfoSercvice.splitPageVo((ProductInfoVo) vo,PAGE_SIZE);
        }else {
            info = productInfoSercvice.splitPage(1,PAGE_SIZE);
        }
        request.getSession().setAttribute("info",info);
        return request.getAttribute("msg");
    }

    //批量删除
    @RequestMapping("/deleteBatch")
    public String deleteBatch(String pids,HttpServletRequest request){
        //获得id字符串数组
        String[] ps = pids.split(",");

        int num = -1;
        num = productInfoSercvice.deleteBatch(ps);
        if (num > 0){
            request.setAttribute("msg","批量删除成功");
        }else {
            request.setAttribute("msg","批量删除失败");
        }

        return "forward:/prod/deleteAjaxSplit.action";
    }


}
