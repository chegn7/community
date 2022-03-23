package com.c.community;

import com.c.community.config.WkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WkTests {


    public static void main(String[] args) throws IOException {

        String cmd = "C:/Program Files/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com C:/software/data/wkimages/2.png";
        cmd = "C:/Program Files/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.baidu.com/ 4895c490d3ce4d58aa32dbc9a62eddd7.png C:/software/data/wkimages";
        Runtime.getRuntime().exec(cmd);
    }
}
