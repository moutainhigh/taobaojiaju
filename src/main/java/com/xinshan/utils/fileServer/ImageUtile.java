package com.xinshan.utils.fileServer;

import org.im4java.core.*;

import java.io.IOException;

/**
 * Created by xu on 15-9-3.
 */
public class ImageUtile {

        /**
         * 裁剪图片
         * @throws IOException
         * @throws InterruptedException
         * @throws IM4JavaException
         */
        public void cropImg(String source,String target) throws IOException, InterruptedException, IM4JavaException{
            ConvertCmd cmd = new ConvertCmd();
            IMOperation opertation = new IMOperation();
            opertation.addImage(source);
            //宽  高 起点横坐标 起点纵坐标
            opertation.crop(400, 300, 34, 100);
            opertation.addImage(target);
            cmd.run(opertation);
        }

        /**
         * 得到图片的信息
         * @throws InfoException
         */
        public void getImgInfo() throws InfoException {
            Info info = new Info("C:\\Users\\zyl\\Desktop\\imgs\\1.jpg");
            System.out.println(info.getImageHeight());
            System.out.println(info.getImageWidth());
        }

        /**
         * 等比缩放图片
         * @throws IOException
         * @throws InterruptedException
         * @throws IM4JavaException
         */
        public void resizeImg() throws IOException, InterruptedException, IM4JavaException{
            ConvertCmd cmd = new ConvertCmd();
            IMOperation opertion = new IMOperation();
            opertion.addImage("C:\\Users\\zyl\\Desktop\\imgs\\1.jpg");
            //等比缩放图片
            opertion.resize(400, 400);
            opertion.addImage("C:\\Users\\zyl\\Desktop\\imgs\\img\\1.jpg");
            cmd.run(opertion);
        }

        /**
         * 旋转图片
         * @throws IOException
         * @throws InterruptedException
         * @throws IM4JavaException
         */
        public void rotateImg() throws IOException, InterruptedException, IM4JavaException{
            ConvertCmd cmd = new ConvertCmd();
            IMOperation operation = new IMOperation();
            operation.addImage("C:\\Users\\zyl\\Desktop\\imgs\\2.jpg");
            operation.rotate(90.0);
            operation.addImage("C:\\Users\\zyl\\Desktop\\imgs\\2.jpg");
            cmd.run(operation);
        }

        /**
         * 将图片编程黑白图片
         * @throws IOException
         * @throws InterruptedException
         * @throws IM4JavaException
         */
        public void monochrome() throws IOException, InterruptedException, IM4JavaException{
            ConvertCmd cmd = new ConvertCmd();
            IMOperation operation = new IMOperation();
            operation.addImage("C:\\Users\\zyl\\Desktop\\imgs\\3.jpg");
            operation.monochrome();
            operation.addImage("C:\\Users\\zyl\\Desktop\\imgs\\3.jpg");
            cmd.run(operation);
        }

        public void annotate() throws IOException, InterruptedException, IM4JavaException {
            MogrifyCmd cmd = new MogrifyCmd();
            IMOperation operation = new IMOperation();
            operation.encoding("UTF-8");
            //gravity 设置方位 NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast   annotate 设置偏移量
            operation.font("C:\\Windows\\Fonts\\simsun.ttc").gravity("CENTER").pointsize(40).fill("red").annotate(10,10,10,10,"测试水印");
            operation.addImage();
            cmd.run(operation,"C:\\Users\\zyl\\Desktop\\imgs\\1.jpg");
        }
    }
